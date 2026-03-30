package com.DATN.Bej.service.payment;

import com.DATN.Bej.config.VNPayConfig;
import com.DATN.Bej.dto.response.payment.PaymentCallbackResponse;
import com.DATN.Bej.dto.response.payment.PaymentResponse;
import com.DATN.Bej.entity.cart.Orders;
import com.DATN.Bej.event.OrderStatusUpdateEvent;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.repository.product.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VNPayService {

    private final VNPayConfig vnPayConfig;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public String createOrder(int total, String orderInfo, String returnBaseUrl, HttpServletRequest request) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VNPayConfig.getIpAddress(request);
        String orderType = "other";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(total * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");

        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String createDate = formatter.format(cld.getTime());
        cld.add(Calendar.MINUTE, 15);
        String expireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", createDate);
        vnp_Params.put("vnp_ExpireDate", expireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext();) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append('=').append(fieldValue);
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                     .append('=')
                     .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String vnp_SecureHash = VNPayConfig.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return vnPayConfig.getPayUrl() + "?" + query;
    }

    /**
     * Tạo payment URL và QR code cho đơn hàng
     * @param orderId ID đơn hàng
     * @param amount Số tiền thanh toán (VND)
     * @param orderInfo Thông tin đơn hàng (optional)
     * @param request HttpServletRequest
     * @return PaymentResponse chứa paymentUrl, qrCodeUrl, transactionRef
     */
    public PaymentResponse createPayment(String orderId, Long amount, String orderInfo, HttpServletRequest request) {
        // Kiểm tra đơn hàng tồn tại
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        // Nếu không có orderInfo, tạo từ orderId
        if (orderInfo == null || orderInfo.isEmpty()) {
            orderInfo = "Thanh toan don hang " + orderId;
        }
        
        // Tạo payment URL
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String paymentUrl = createOrder(amount.intValue(), orderInfo, baseUrl, request);
        
        // Lấy transactionRef từ URL (vnp_TxnRef)
        String transactionRef = extractTransactionRef(paymentUrl);
        
        // Tạo QR code URL (VNPay hỗ trợ QR code qua URL)
        // Có thể generate QR code từ paymentUrl bằng thư viện bên ngoài
        String qrCodeUrl = generateQRCodeUrl(paymentUrl);
        
        log.info("💳 Payment created - Order: {}, Amount: {}, TransactionRef: {}", 
                orderId, amount, transactionRef);
        
        return PaymentResponse.builder()
                .orderId(orderId)
                .paymentUrl(paymentUrl)
                .qrCodeUrl(qrCodeUrl)
                .qrCodeData(paymentUrl)  // Có thể dùng để generate QR code ở client
                .transactionRef(transactionRef)
                .amount(amount)
                .message("Payment URL created successfully")
                .build();
    }
    
    /**
     * Xử lý callback từ VNPay và cập nhật trạng thái đơn hàng
     * @param request HttpServletRequest chứa thông tin từ VNPay
     * @return PaymentCallbackResponse với kết quả thanh toán
     */
    @Transactional
    public PaymentCallbackResponse handlePaymentCallback(HttpServletRequest request) {
        log.info("📞 Processing payment callback from VNPay");
        
        // Validate signature
        int paymentStatus = orderReturn(request);
        
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String transactionRef = request.getParameter("vnp_TxnRef");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String amountStr = request.getParameter("vnp_Amount");
        
        // Extract orderId từ orderInfo (format: "Thanh toan don hang {orderId}")
        String orderId = extractOrderIdFromOrderInfo(orderInfo);
        
        Long amount = amountStr != null ? Long.parseLong(amountStr) / 100 : 0L;
        
        // Cập nhật trạng thái đơn hàng
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        int oldStatus = order.getStatus();
        boolean success = (paymentStatus == 1);
        String message;
        int newStatus;
        
        if (success) {
            // Thanh toán thành công: status = 2 (đã thanh toán)
            newStatus = 2;
            order.setStatus(newStatus);
            message = "Payment successful";
            log.info("✅ Payment successful - Order: {}, Transaction: {}", orderId, transactionId);
        } else if (paymentStatus == 0) {
            // Thanh toán thất bại: status = 3 (thanh toán thất bại)
            newStatus = 3;
            order.setStatus(newStatus);
            message = "Payment failed";
            log.warn("❌ Payment failed - Order: {}", orderId);
        } else {
            // Lỗi signature: không cập nhật status
            message = "Invalid payment signature";
            log.error("❌ Invalid payment signature - Order: {}", orderId);
            // Không cập nhật order nếu signature không hợp lệ
            return PaymentCallbackResponse.builder()
                    .orderId(orderId)
                    .transactionRef(transactionRef)
                    .transactionId(transactionId)
                    .paymentTime(paymentTime)
                    .amount(amount)
                    .paymentStatus(paymentStatus)
                    .message(message)
                    .success(false)
                    .build();
        }
        
        orderRepository.save(order);
        
        // Publish event để gửi notification
        if (oldStatus != newStatus) {
            OrderStatusUpdateEvent statusUpdateEvent = new OrderStatusUpdateEvent(
                    orderId,
                    order.getUser().getId(),
                    oldStatus,
                    newStatus,
                    getStatusName(newStatus),
                    message
            );
            eventPublisher.publishEvent(statusUpdateEvent);
            log.info("✅ Payment status update event published - Order: {}, Status: {} -> {}", 
                    orderId, oldStatus, newStatus);
        }
        
        return PaymentCallbackResponse.builder()
                .orderId(orderId)
                .transactionRef(transactionRef)
                .transactionId(transactionId)
                .paymentTime(paymentTime)
                .amount(amount)
                .paymentStatus(paymentStatus)
                .message(message)
                .success(success)
                .build();
    }
    
    /**
     * Legacy method - giữ lại để backward compatibility
     */
    public int orderReturn(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext();) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            hashData.append(fieldName).append('=').append(fieldValue);
            if (itr.hasNext()) hashData.append('&');
        }

        String signValue = VNPayConfig.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        if (signValue.equals(vnp_SecureHash)) {
            return "00".equals(request.getParameter("vnp_TransactionStatus")) ? 1 : 0;
        } else {
            return -1; 
        }
    }
    
    /**
     * Extract transactionRef từ payment URL
     */
    private String extractTransactionRef(String paymentUrl) {
        try {
            String[] parts = paymentUrl.split("vnp_TxnRef=");
            if (parts.length > 1) {
                String[] refParts = parts[1].split("&");
                return refParts[0];
            }
        } catch (Exception e) {
            log.warn("Could not extract transactionRef from URL: {}", paymentUrl);
        }
        return VNPayConfig.getRandomNumber(8);
    }
    
    /**
     * Extract orderId từ orderInfo
     * Format: "Thanh toan don hang {orderId}"
     */
    private String extractOrderIdFromOrderInfo(String orderInfo) {
        if (orderInfo == null || orderInfo.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }
        
        // Tìm orderId sau "don hang "
        String prefix = "don hang ";
        int index = orderInfo.indexOf(prefix);
        if (index >= 0) {
            return orderInfo.substring(index + prefix.length()).trim();
        }
        
        // Nếu không tìm thấy, trả về toàn bộ orderInfo (có thể orderId được truyền trực tiếp)
        return orderInfo.trim();
    }
    
    /**
     * Generate QR code URL từ payment URL
     * Có thể sử dụng service như qrcode.tec-it.com hoặc generate ở client
     */
    private String generateQRCodeUrl(String paymentUrl) {
        // Option 1: Sử dụng QR code service
        // return "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" + URLEncoder.encode(paymentUrl, StandardCharsets.UTF_8);
        
        // Option 2: Trả về paymentUrl để client tự generate QR code
        return paymentUrl;
    }
    
    /**
     * Lấy tên trạng thái đơn hàng
     */
    private String getStatusName(int status) {
        return switch (status) {
            case 0 -> "Chờ xử lý";
            case 1 -> "Đã xác nhận";
            case 2 -> "Đã thanh toán";
            case 3 -> "Thanh toán thất bại";
            case 4 -> "Đang giao hàng";
            case 5 -> "Đã hoàn thành";
            default -> "Không xác định";
        };
    }
}
