package com.DATN.Bej.service.guest;

import com.DATN.Bej.dto.request.cartRequest.OrderRequest;
import com.DATN.Bej.dto.response.cart.CartItemResponse;
import com.DATN.Bej.dto.response.cart.OrderDetailsResponse;
import com.DATN.Bej.dto.response.cart.OrdersResponse;
import com.DATN.Bej.entity.cart.CartItem;
import com.DATN.Bej.entity.cart.OrderItem;
import com.DATN.Bej.entity.cart.OrderNote;
import com.DATN.Bej.entity.cart.Orders;
import com.DATN.Bej.entity.identity.User;
import com.DATN.Bej.entity.product.ProductAttribute;
import com.DATN.Bej.event.OrderCreatedEvent;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.mapper.product.CartItemMapper;
import com.DATN.Bej.mapper.product.OrderMapper;
import com.DATN.Bej.repository.UserRepository;
import com.DATN.Bej.repository.product.CartItemRepository;
import com.DATN.Bej.repository.product.OrderRepository;
import com.DATN.Bej.repository.product.ProductAttributeRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class CartService {

    UserRepository userRepository;
    ProductAttributeRepository productAttributeRepository;
    CartItemRepository cartItemRepository;
    OrderRepository ordersRepository;

    OrderMapper orderMapper;
    CartItemMapper cartItemMapper;
    ApplicationEventPublisher eventPublisher;

    public CartItemResponse addToCart(String attId){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        log.info(name);
        User user = userRepository.findByPhoneNumber(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        ProductAttribute productA = productAttributeRepository.findById(attId).orElseThrow(
                () -> new AppException(ErrorCode.UNAUTHENTICATED));

        CartItem cartItem = cartItemRepository.findByUser_IdAndProductA_Id(user.getId(), productA.getId());

        if( cartItem == null){
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProductA(productA);
            cartItem.setColor(productA.getVariant().getColor());
            cartItem.setQuantity(1);
            cartItem.setPrice(productA.getFinalPrice());
            cartItem.setAddedAt(LocalDate.now());
            cartItem.setProductName(productA.getVariant().getProduct().getName());
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        }

        return cartItemMapper.toCartItemResponse(cartItemRepository.save(cartItem));
    }

    public List<CartItemResponse> viewCart(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        log.info("Viewing cart for user: {}", name);
        User user = userRepository.findByPhoneNumber(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return cartItemRepository.findAllByUserId(user.getId()).stream().map(cartItemMapper::toCartItemResponse).toList();
    }
    
    /**
     * Cập nhật số lượng của item trong giỏ hàng
     * @param cartItemId ID của cart item
     * @param quantity Số lượng mới (phải > 0)
     * @return CartItemResponse - Thông tin item đã được cập nhật
     * @throws AppException nếu quantity <= 0 hoặc user không sở hữu cart item
     */
    public CartItemResponse updateCartItemQuantity(String cartItemId, int quantity) {
        if (quantity <= 0) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }
        
        var context = SecurityContextHolder.getContext();
        String phoneNumber = context.getAuthentication().getName();
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        // Kiểm tra user sở hữu cart item
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        
        // Cập nhật quantity và price (price có thể thay đổi theo thời gian)
        cartItem.setQuantity(quantity);
        cartItem.setPrice(cartItem.getProductA().getFinalPrice());
        
        CartItem saved = cartItemRepository.save(cartItem);
        log.info("Cart item quantity updated - ID: {}, New quantity: {}", cartItemId, quantity);
        return cartItemMapper.toCartItemResponse(saved);
    }
    
    /**
     * Xóa item khỏi giỏ hàng
     * @param cartItemId ID của cart item cần xóa
     */
    public void removeFromCart(String cartItemId) {
        var context = SecurityContextHolder.getContext();
        String phoneNumber = context.getAuthentication().getName();
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        // Kiểm tra user sở hữu cart item
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        
        cartItemRepository.delete(cartItem);
        log.info("Cart item removed - ID: {}", cartItemId);
    }
    
    /**
     * Xóa tất cả items trong giỏ hàng của user hiện tại
     */
    public void clearCart() {
        var context = SecurityContextHolder.getContext();
        String phoneNumber = context.getAuthentication().getName();
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        List<CartItem> cartItems = cartItemRepository.findAllByUserId(user.getId());
        cartItemRepository.deleteAll(cartItems);
        log.info("Cart cleared - Removed {} items", cartItems.size());
    }

    public OrderDetailsResponse placeOrder(OrderRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByPhoneNumber(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Orders orders = orderMapper.toOrder(request);
        orders.setUser(user);

        List<OrderItem> orderItems = new ArrayList<>();
        for (var itemReq : request.getItems()) {
            log.info(itemReq.getCartItemId());
            ProductAttribute productAtt = productAttributeRepository
                    .findById(itemReq.getProductAttId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            OrderItem orderItem = orderMapper.toOrderItem(itemReq);
            orderItem.setProductA(productAtt);
            orderItem.setOrder(orders);
            orderItem.setPrice(productAtt.getFinalPrice());
            cartItemRepository.deleteById(itemReq.getCartItemId());

//            productAttributeRepository.increaseSoldQuantity(UUID.fromString(productAtt.getId()), orderItem.getQuantity());

            orderItems.add(orderItem);
        }
        double totalPrice = orderItems.stream()
                .mapToDouble(OrderItem::getPrice)
                .sum();

        orders.setTotalPrice(totalPrice);
        orders.setOrderItems(orderItems);
        orders.setOrderAt(LocalDate.now());

        Orders saved = ordersRepository.save(orders);
        
        // Publish event để gửi thông báo cho user, admin và staff
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                saved.getId(),
                user.getId(),
                saved.getType(),
                saved.getTotalPrice(),
                saved.getDescription()
        );
        eventPublisher.publishEvent(orderCreatedEvent);
        
        log.info("✅ Order created, event published - Order: {}, User: {}, Type: {}", 
                saved.getId(), user.getId(), saved.getType());
        
        return orderMapper.toOrderDetailsResponse(saved);
    }

    public List<OrderDetailsResponse> getMyOrder(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        log.info("Getting orders for user: {}", name);
        User user = userRepository.findByPhoneNumber(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        List<Orders> orders = ordersRepository.findAllByUserId(user.getId());

        return orders.stream().map(orderMapper::toOrderDetailsResponse).toList();
    }
    
    /**
     * Lấy chi tiết đơn hàng của user hiện tại
     * @param orderId ID của đơn hàng
     * @return OrderDetailsResponse
     * @throws AppException nếu đơn hàng không tồn tại hoặc không thuộc về user
     */
    public OrderDetailsResponse getMyOrderDetails(String orderId) {
        var context = SecurityContextHolder.getContext();
        String phoneNumber = context.getAuthentication().getName();
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        // Kiểm tra user sở hữu đơn hàng
        if (!order.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        
        log.info("Order details retrieved - Order: {}, User: {}", orderId, phoneNumber);
        return orderMapper.toOrderDetailsResponse(order);
    }

    public OrdersResponse confirmRepairOrder(String orderId){
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByPhoneNumber(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        order.setStatus(2);
        OrderNote newNote = new OrderNote();
        newNote.setOrder(order);
        newNote.setNote("Khách hàng xác nhận đơn sửa chữa");
        newNote.setUpdateTime(LocalDateTime.now());
        newNote.setUpdateBy(user);
        order.getOrderNotes().add(newNote);

        return orderMapper.toOrdersResponse(order);
    }

    //  ======================================================================================
    public List<OrdersResponse> getAllOrders(){
        return ordersRepository.findAllByOrderByOrderAtDesc().stream().map(orderMapper::toOrdersResponse).toList();
    }

    public List<OrdersResponse> getOrdersByType(int type){
        return ordersRepository.findDistinctByTypeOrderByOrderAtDesc(type).stream().map(orderMapper::toOrdersResponse).toList();
    }

    public List<OrdersResponse> getOrdersByPhoneOrName(String phoneNumber){
        return ordersRepository.searchByPhoneNumberOrderByOrderAtDesc(phoneNumber).stream().map(orderMapper::toOrdersResponse).toList();
    }

    public OrderDetailsResponse getOrderDetails(String orderId){
        return ordersRepository.findById(orderId).map(orderMapper::toOrderDetailsResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

}

