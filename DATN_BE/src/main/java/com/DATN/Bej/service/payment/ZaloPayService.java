package com.DATN.Bej.service.payment;

import com.DATN.Bej.config.ZaloPayConfig;
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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZaloPayService {

    private final ZaloPayConfig zaloPayConfig;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Tạo payment URL cho đơn hàng với ZaloPay
     * @param orderId ID đơn hàng
     * @param request HttpServletRequest
     * @return PaymentResponse chứa orderUrl để redirect
     */
    public PaymentResponse createPayment(String orderId, HttpServletRequest request) {
        // Kiểm tra đơn hàng tồn tại
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        // Kiểm tra đơn hàng chưa được thanh toán
        if (order.getStatus() == 2 || order.getStatus() == 5) {
            log.warn("⚠️ Order already paid - Order: {}, Status: {}", orderId, order.getStatus());
            throw new AppException(ErrorCode.INVALID_KEY); // Đơn đã thanh toán rồi
        }
        
        // Lấy totalPrice từ Orders
        Long amount = (long) order.getTotalPrice();
        if (amount <= 0) {
            log.warn("⚠️ Invalid amount - Order: {}, Amount: {}", orderId, amount);
            throw new AppException(ErrorCode.INVALID_KEY); // Số tiền không hợp lệ
        }
        
        try {
            // Tạo app_trans_id: format YYMMDD_timestamp_random (tối đa 40 ký tự, đảm bảo unique)
            String appTransId = generateAppTransId(orderId);
            
            // Tạo timestamp (milliseconds)
            long appTime = Instant.now().toEpochMilli();
            
            // Tạo embed_data với redirecturl (thêm orderId để return callback có thể update DB)
            Map<String, String> embedData = new HashMap<>();
            String baseUrl = request.getScheme() + "://" + request.getServerName() + 
                           (request.getServerPort() != 80 && request.getServerPort() != 443 ? 
                            ":" + request.getServerPort() : "") + 
                           request.getContextPath();
            embedData.put("redirecturl", baseUrl + "/payment/zalopay/return?orderId=" + orderId);
            
            // Tạo item (danh sách sản phẩm) - phải là array JSON string
            List<Map<String, Object>> items = new ArrayList<>();
            Map<String, Object> item = new HashMap<>();
            item.put("itemid", orderId);
            item.put("itemname", "Thanh toan don hang " + orderId);
            item.put("itemprice", amount);
            item.put("itemquantity", 1);
            items.add(item);
            String itemJson = objectMapper.writeValueAsString(items);
            
            // Tạo embed_data JSON string
            String embedDataJson = objectMapper.writeValueAsString(embedData);
            
            // Tạo request body - đảm bảo thứ tự và format đúng
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("app_id", Integer.parseInt(zaloPayConfig.getAppId()));
            requestBody.put("app_user", "user_" + orderId); // Có thể lấy từ order.getUserId()
            requestBody.put("app_trans_id", appTransId);
            requestBody.put("app_time", appTime);
            requestBody.put("amount", amount);
            requestBody.put("description", "Thanh toan don hang " + orderId);
            requestBody.put("item", itemJson);
            requestBody.put("embed_data", embedDataJson);
            requestBody.put("callback_url", zaloPayConfig.getCallbackUrl());
            
            // Tạo MAC (Message Authentication Code) - phải tính trước khi thêm mac vào body
            String mac = createMac(requestBody);
            requestBody.put("mac", mac);
            
            // Log request body để debug
            log.debug("📤 ZaloPay request body: {}", objectMapper.writeValueAsString(requestBody));
            
            // Gửi request đến ZaloPay
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            log.info("💳 Sending payment request to ZaloPay - Order: {}, Amount: {}, AppTransId: {}", 
                    orderId, amount, appTransId);
            
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    zaloPayConfig.getCreateOrderUrl(),
                    HttpMethod.POST,
                    entity,
                    (Class<Map<String, Object>>) (Class<?>) Map.class
            );
            
            Map<String, Object> responseBody = response.getBody();
            
            if (responseBody == null) {
                throw new AppException(ErrorCode.INVALID_KEY);
            }
            
            Integer returnCode = (Integer) responseBody.get("return_code");
            String returnMessage = (String) responseBody.get("return_message");
            Object subReturnCode = responseBody.get("sub_return_code");
            String orderUrl = (String) responseBody.get("order_url");
            
            // Log full response để debug
            log.debug("📥 ZaloPay response: {}", objectMapper.writeValueAsString(responseBody));
            
            if (returnCode == null || returnCode != 1 || orderUrl == null) {
                log.error("❌ ZaloPay create order failed - Order: {}, ReturnCode: {}, SubReturnCode: {}, Message: {}", 
                         orderId, returnCode, subReturnCode, returnMessage);
                // Log thêm thông tin để debug
                if (subReturnCode != null) {
                    if (subReturnCode.equals(-68)) {
                        log.error("❌ SubReturnCode -68: Mã giao dịch bị trùng (app_trans_id đã tồn tại)");
                    } else if (subReturnCode.equals(-402)) {
                        log.error("❌ SubReturnCode -402: Chữ ký không hợp lệ (MAC sai)");
                    }
                }
                throw new AppException(ErrorCode.INVALID_KEY);
            }
            
            log.info("✅ ZaloPay payment created - Order: {}, OrderUrl: {}", orderId, orderUrl);
            
            return PaymentResponse.builder()
                    .orderId(orderId)
                    .paymentUrl(orderUrl) // orderUrl từ ZaloPay
                    .orderUrl(orderUrl)   // Thêm field orderUrl để rõ ràng
                    .transactionRef(appTransId)
                    .amount(amount)
                    .message("Payment URL created successfully")
                    .build();
                    
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Error creating ZaloPay payment - Order: {}", orderId, e);
            throw new AppException(ErrorCode.INVALID_KEY);
        }
    }

    /**
     * Xử lý callback từ ZaloPay (server-to-server, method POST)
     * Body callback:
     * {
     *   "data": "{...json string...}",
     *   "mac": "hmac_sha256(key2, data)",
     *   "type": 1
     * }
     *
     * @param body Map body callback (data, mac, type)
     * @return true nếu xử lý thành công và MAC hợp lệ
     */
    public boolean handleCallback(Map<String, Object> body) {
        try {
            log.info("📞 ZaloPay callback body: {}", objectMapper.writeValueAsString(body));

            String data = (String) body.get("data");
            String mac = (String) body.get("mac");

            if (data == null || mac == null) {
                log.error("❌ ZaloPay callback missing data or mac");
                return false;
            }

            // 1. Verify MAC với key2
            String reqMac = hmacSHA256(zaloPayConfig.getKey2(), data);
            if (!reqMac.equals(mac)) {
                log.error("❌ ZaloPay callback MAC invalid. expected={}, actual={}", reqMac, mac);
                return false;
            }

            // 2. Parse 'data' (bên trong là JSON string)
            Map<String, Object> callbackData = objectMapper.readValue(
                    data, new TypeReference<Map<String, Object>>() {}
            );

            log.info("📥 ZaloPay callback data parsed: {}", objectMapper.writeValueAsString(callbackData));

            String appTransId = (String) callbackData.get("app_trans_id");
            Object amountObj = callbackData.get("amount");
            Long amount = amountObj != null ? Long.parseLong(amountObj.toString()) : 0L;

            // Lấy item -> orderId (itemid)
            String orderId = null;
            Object itemObj = callbackData.get("item");
            if (itemObj != null) {
                // Trong callback, item thường là JSON array string
                String itemJson = itemObj.toString();
                try {
                    List<Map<String, Object>> items = objectMapper.readValue(
                            itemJson, new TypeReference<List<Map<String, Object>>>() {}
                    );
                    if (!items.isEmpty()) {
                        Object itemIdObj = items.get(0).get("itemid");
                        if (itemIdObj != null) {
                            orderId = itemIdObj.toString();
                        }
                    }
                } catch (Exception e) {
                    log.warn("⚠️ Could not parse item array from callback data: {}", itemJson, e);
                }
            }

            if (orderId == null) {
                log.error("❌ Could not determine orderId from ZaloPay callback");
                return false;
            }

            // 3. Cập nhật đơn hàng trong DB
            Orders order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            int oldStatus = order.getStatus();
            // Giả định callback này chỉ gửi khi thu tiền thành công
            int newStatus = 2; // Đã thanh toán
            order.setStatus(newStatus);
            orderRepository.save(order);
            
            // Publish event để gửi notification
            if (oldStatus != newStatus) {
                OrderStatusUpdateEvent statusUpdateEvent = new OrderStatusUpdateEvent(
                        orderId,
                        order.getUser().getId(),
                        oldStatus,
                        newStatus,
                        getStatusName(newStatus),
                        "Thanh toán thành công qua ZaloPay"
                );
                eventPublisher.publishEvent(statusUpdateEvent);
                log.info("✅ ZaloPay payment status update event published - Order: {}, Status: {} -> {}", 
                        orderId, oldStatus, newStatus);
            }

            log.info("✅ ZaloPay callback processed - Order: {}, AppTransId: {}, Amount: {}",
                    orderId, appTransId, amount);

            return true;
        } catch (Exception e) {
            log.error("❌ Error handling ZaloPay callback", e);
            return false;
        }
    }
    
    /**
     * Xử lý return callback từ ZaloPay (user redirect về sau khi thanh toán, method GET)
     * ZaloPay sẽ redirect user về URL này với query params chứa kết quả thanh toán
     * 
     * Query params từ ZaloPay có thể bao gồm:
     * - appid: ID ứng dụng
     * - apptransid: Mã giao dịch
     * - pmcid: ID phương thức thanh toán
     * - bankcode: Mã ngân hàng
     * - amount: Số tiền
     * - discountamount: Số tiền giảm giá
     * - status: Trạng thái (1 = thành công, khác = thất bại)
     * - checksum: Chữ ký để verify (optional)
     * - orderId: ID đơn hàng (được thêm vào redirecturl khi tạo payment)
     * 
     * Lưu ý: 
     * - Việc cập nhật DB chủ yếu được xử lý bởi server-to-server callback (POST /payment/zalopay/callback)
     * - Method này cũng sẽ cập nhật DB nếu payment thành công và order chưa được cập nhật (fallback mechanism)
     * - Đảm bảo order được cập nhật kịp thời ngay cả khi server callback bị delay
     * 
     * @param request HttpServletRequest chứa query params từ ZaloPay
     * @return Map chứa appTransId, status, success, amount, message hoặc null nếu không parse được
     */
    public Map<String, Object> handleReturnCallback(HttpServletRequest request) {
        try {
            log.info("📞 ZaloPay return callback received (user redirect)");
            
            // Lấy tất cả query params
            Map<String, String> params = new HashMap<>();
            for (String paramName : Collections.list(request.getParameterNames())) {
                params.put(paramName, request.getParameter(paramName));
            }
            
            log.info("📥 ZaloPay return params: {}", params);
            
            String appTransId = params.get("apptransid");
            String statusStr = params.get("status");
            String amountStr = params.get("amount");
            String orderId = params.get("orderId"); // Lấy orderId từ query params (đã thêm vào redirecturl)
            
            if (appTransId == null) {
                log.warn("⚠️ ZaloPay return callback missing apptransid");
                return null;
            }
            
            int status = statusStr != null ? Integer.parseInt(statusStr) : -1;
            Long amount = amountStr != null ? Long.parseLong(amountStr) : 0L;
            
            boolean success = (status == 1);
            
            // Nếu payment thành công và có orderId, cập nhật DB (fallback nếu server callback chưa chạy)
            if (orderId != null && success) {
                try {
                    Orders order = orderRepository.findById(orderId).orElse(null);
                    
                    if (order != null && order.getStatus() != 2) {
                        // Chỉ update nếu chưa được update bởi server callback (status != 2)
                        int oldStatus = order.getStatus();
                        int newStatus = 2; // Đã thanh toán
                        order.setStatus(newStatus);
                        orderRepository.save(order);
                        
                        // Publish event để gửi notification
                        OrderStatusUpdateEvent statusUpdateEvent = new OrderStatusUpdateEvent(
                                orderId,
                                order.getUser().getId(),
                                oldStatus,
                                newStatus,
                                getStatusName(newStatus),
                                "Thanh toán thành công qua ZaloPay"
                        );
                        eventPublisher.publishEvent(statusUpdateEvent);
                        
                        log.info("✅ ZaloPay return callback updated order status - Order: {}, AppTransId: {}, Status: {} -> {}", 
                                orderId, appTransId, oldStatus, newStatus);
                    } else if (order != null && order.getStatus() == 2) {
                        log.info("ℹ️ ZaloPay return callback: Order already updated by server callback - Order: {}, Status: 2", 
                                orderId);
                    }
                } catch (Exception e) {
                    log.error("❌ Error updating order status in return callback - Order: {}, AppTransId: {}", 
                            orderId, appTransId, e);
                    // Không throw exception, vẫn trả về result cho frontend
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("appTransId", appTransId);
            result.put("status", status);
            result.put("success", success);
            result.put("amount", amount);
            result.put("message", success ? "Payment successful" : "Payment failed or pending");
            
            log.info("✅ ZaloPay return callback processed - OrderId: {}, AppTransId: {}, Status: {}, Success: {}", 
                    orderId, appTransId, status, success);
            
            return result;
            
        } catch (Exception e) {
            log.error("❌ Error handling ZaloPay return callback", e);
            return null;
        }
    }
    
    /**
     * Tạo app_trans_id: format YYMMDD_timestamp_random (tối đa 40 ký tự)
     * Thêm timestamp và random number để đảm bảo unique cho mỗi lần tạo payment
     */
    private String generateAppTransId(String orderId) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        int year = cal.get(Calendar.YEAR) % 100;
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        
        String dateStr = String.format("%02d%02d%02d", year, month, day);
        
        // Thêm timestamp (milliseconds) và random number để đảm bảo unique
        long timestamp = Instant.now().toEpochMilli();
        int random = new Random().nextInt(1000); // Random 0-999
        String appTransId = dateStr + "_" + timestamp + "_" + random;
        
        // Giới hạn 40 ký tự
        if (appTransId.length() > 40) {
            // Nếu quá dài, rút ngắn random number
            String timestampStr = String.valueOf(timestamp);
            int maxRandomLength = 40 - dateStr.length() - 1 - timestampStr.length() - 1;
            if (maxRandomLength > 0) {
                String randomStr = String.format("%0" + maxRandomLength + "d", random % (int)Math.pow(10, maxRandomLength));
                appTransId = dateStr + "_" + timestampStr + "_" + randomStr;
            } else {
                // Nếu vẫn quá dài, chỉ dùng timestamp
                appTransId = dateStr + "_" + timestampStr;
            }
        }
        
        return appTransId;
    }

    /**
     * Tạo HMAC SHA256 bất kỳ (dùng cho cả key1, key2)
     */
    private String hmacSHA256(String key, String data) {
        try {
            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            hmacSHA256.init(secretKey);
            byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Error creating HMAC SHA256", e);
            throw new RuntimeException("Failed to create HMAC SHA256", e);
        }
    }

    /**
     * Tạo MAC (Message Authentication Code) để xác thực request
     * Theo tài liệu ZaloPay: hmac_input = app_id|app_trans_id|app_user|amount|app_time|embed_data|item
     * Sử dụng dấu | (pipe) làm separator, không phải & và không có key=value format
     */
    private String createMac(Map<String, Object> params) {
        try {
            // Lấy các giá trị theo thứ tự cố định
            Integer appId = (Integer) params.get("app_id");
            String appTransId = (String) params.get("app_trans_id");
            String appUser = (String) params.get("app_user");
            Long amount = params.get("amount") instanceof Long ? (Long) params.get("amount") : 
                         params.get("amount") instanceof Integer ? ((Integer) params.get("amount")).longValue() :
                         Long.parseLong(params.get("amount").toString());
            Long appTime = params.get("app_time") instanceof Long ? (Long) params.get("app_time") : 
                          Long.parseLong(params.get("app_time").toString());
            String embedData = (String) params.get("embed_data");
            String item = (String) params.get("item");
            
            // Tạo hmac_input theo format: app_id|app_trans_id|app_user|amount|app_time|embed_data|item
            StringBuilder hmacInput = new StringBuilder();
            hmacInput.append(appId != null ? appId : "");
            hmacInput.append("|");
            hmacInput.append(appTransId != null ? appTransId : "");
            hmacInput.append("|");
            hmacInput.append(appUser != null ? appUser : "");
            hmacInput.append("|");
            hmacInput.append(amount != null ? amount : "");
            hmacInput.append("|");
            hmacInput.append(appTime != null ? appTime : "");
            hmacInput.append("|");
            hmacInput.append(embedData != null ? embedData : "{}");
            hmacInput.append("|");
            hmacInput.append(item != null ? item : "[]");
            
            String hmacInputString = hmacInput.toString();
            log.info("🔐 MAC hmac_input: {}", hmacInputString);
            log.info("🔐 Using Key1: {}", zaloPayConfig.getKey1());
            
            // Tạo HMAC SHA256
            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    zaloPayConfig.getKey1().getBytes(StandardCharsets.UTF_8), 
                    "HmacSHA256"
            );
            hmacSHA256.init(secretKey);
            byte[] hash = hmacSHA256.doFinal(hmacInputString.getBytes(StandardCharsets.UTF_8));
            
            // Convert sang hex string (lowercase)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            String mac = hexString.toString();
            log.info("🔐 Generated MAC: {}", mac);
            return mac;
        } catch (Exception e) {
            log.error("Error creating MAC", e);
            throw new RuntimeException("Failed to create MAC", e);
        }
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