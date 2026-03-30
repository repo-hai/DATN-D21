package com.DATN.Bej.controller.cart;

import com.DATN.Bej.dto.request.ApiResponse;
import com.DATN.Bej.dto.request.cartRequest.OrderRequest;
import com.DATN.Bej.dto.response.cart.CartItemResponse;
import com.DATN.Bej.dto.response.cart.OrderDetailsResponse;
import com.DATN.Bej.service.guest.CartService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller xử lý các API liên quan đến giỏ hàng và đơn hàng
 * Tất cả endpoints đều yêu cầu authentication (JWT token)
 * User được xác định từ JWT token trong SecurityContext
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/cart")
public class CartController {

    CartService cartService;

    /**
     * POST /cart/add/{attId}
     * Thêm product vào giỏ hàng
     * 
     * @param attId ID của ProductAttribute (variant attribute) cần thêm vào giỏ hàng
     * @return CartItemResponse - Thông tin item đã thêm vào giỏ hàng
     * 
     * Logic:
     * - Nếu item chưa có trong giỏ hàng → tạo mới với quantity = 1
     * - Nếu item đã có trong giỏ hàng → tăng quantity lên 1
     * - User được xác định từ JWT token
     * 
     * Example:
     * - POST /cart/add/attr-123 → Thêm ProductAttribute có ID = attr-123 vào giỏ hàng
     */
    @PostMapping("/add/{attId}")
    ApiResponse<CartItemResponse> addToCart(@PathVariable String attId){
        log.info("🛒 Adding product to cart - Attribute ID: {}", attId);
        CartItemResponse result = cartService.addToCart(attId);
        log.info("✅ Product added to cart successfully - Cart Item ID: {}", result.getId());
        return ApiResponse.<CartItemResponse>builder()
                .result(result)
                .build();
    }

    /**
     * GET /cart/view
     * Xem tất cả items trong giỏ hàng của user hiện tại
     * 
     * @return List<CartItemResponse> - Danh sách items trong giỏ hàng
     * 
     * User được xác định từ JWT token
     */
    @GetMapping("/view")
    ApiResponse<List<CartItemResponse>> viewCart(){
        log.info("🛒 Viewing cart");
        List<CartItemResponse> result = cartService.viewCart();
        log.info("✅ Cart retrieved - {} items", result.size());
        return ApiResponse.<List<CartItemResponse>>builder()
                .result(result)
                .build();
    }
    
    /**
     * PUT /cart/update/{cartItemId}?quantity={quantity}
     * Cập nhật số lượng của item trong giỏ hàng
     * 
     * @param cartItemId ID của cart item cần cập nhật
     * @param quantity Số lượng mới (phải > 0)
     * @return CartItemResponse - Thông tin item đã được cập nhật
     * 
     * Logic:
     * - Chỉ user sở hữu cart item mới có thể cập nhật
     * - Nếu quantity <= 0 → xóa item khỏi giỏ hàng
     * 
     * Example:
     * - PUT /cart/update/cart-123?quantity=5 → Cập nhật số lượng thành 5
     */
    @PutMapping("/update/{cartItemId}")
    ApiResponse<CartItemResponse> updateCartItemQuantity(
            @PathVariable String cartItemId,
            @RequestParam int quantity) {
        log.info("🛒 Updating cart item - ID: {}, Quantity: {}", cartItemId, quantity);
        CartItemResponse result = cartService.updateCartItemQuantity(cartItemId, quantity);
        log.info("✅ Cart item updated successfully - ID: {}", cartItemId);
        return ApiResponse.<CartItemResponse>builder()
                .result(result)
                .build();
    }
    
    /**
     * DELETE /cart/remove/{cartItemId}
     * Xóa item khỏi giỏ hàng
     * 
     * @param cartItemId ID của cart item cần xóa
     * @return ApiResponse với result = null
     * 
     * Logic:
     * - Chỉ user sở hữu cart item mới có thể xóa
     * 
     * Example:
     * - DELETE /cart/remove/cart-123 → Xóa cart item có ID = cart-123
     */
    @DeleteMapping("/remove/{cartItemId}")
    ApiResponse<Void> removeFromCart(@PathVariable String cartItemId) {
        log.info("🛒 Removing item from cart - ID: {}", cartItemId);
        cartService.removeFromCart(cartItemId);
        log.info("✅ Item removed from cart successfully - ID: {}", cartItemId);
        return ApiResponse.<Void>builder().build();
    }
    
    /**
     * DELETE /cart/clear
     * Xóa tất cả items trong giỏ hàng của user hiện tại
     * 
     * @return ApiResponse với result = null
     * 
     * User được xác định từ JWT token
     */
    @DeleteMapping("/clear")
    ApiResponse<Void> clearCart() {
        log.info("🛒 Clearing cart");
        cartService.clearCart();
        log.info("✅ Cart cleared successfully");
        return ApiResponse.<Void>builder().build();
    }

    /**
     * POST /cart/place-order
     * Đặt hàng từ giỏ hàng
     * 
     * @param request OrderRequest chứa thông tin đơn hàng và danh sách items
     * @return OrderDetailsResponse - Chi tiết đơn hàng đã được tạo
     * 
     * Logic:
     * - Tạo đơn hàng mới từ các items trong giỏ hàng
     * - Xóa các items đã đặt hàng khỏi giỏ hàng
     * - User được xác định từ JWT token
     * 
     * Request body:
     * {
     *   "phoneNumber": "0123456789",
     *   "email": "user@example.com",
     *   "address": "123 Main St",
     *   "description": "Giao hàng nhanh",
     *   "items": [
     *     {
     *       "cartItemId": "cart-123",
     *       "productAttId": "attr-456",
     *       "quantity": 2
     *     }
     *   ]
     * }
     */
    @Transactional
    @PostMapping("/place-order")
    ApiResponse<OrderDetailsResponse> placeOrder(@RequestBody @Valid OrderRequest request){
        log.info("📦 Placing order - {} items", request.getItems() != null ? request.getItems().size() : 0);
        OrderDetailsResponse result = cartService.placeOrder(request);
        log.info("✅ Order placed successfully - Order ID: {}", result.getId());
        return ApiResponse.<OrderDetailsResponse>builder()
                .result(result)
                .build();
    }

    /**
     * GET /cart/my-order
     * Lấy danh sách tất cả đơn hàng của user hiện tại
     * 
     * @return List<OrderDetailsResponse> - Danh sách đơn hàng
     * 
     * User được xác định từ JWT token
     */
    @GetMapping("/my-order")
    ApiResponse<List<OrderDetailsResponse>> getMyOrders(){
        log.info("📦 Getting my orders");
        List<OrderDetailsResponse> result = cartService.getMyOrder();
        log.info("✅ Retrieved {} orders", result.size());
        return ApiResponse.<List<OrderDetailsResponse>>builder()
                .result(result)
                .build();
    }

//    @PutMapping("/repair/{orderId}/confirm")
//    ApiResponse<OrderDetailsResponse> confirmRepairOrder(@PathVariable String orderId){
//
//        OrderDetailsResponse result = cartService.confirmRepairOrder(String orderId);
//
//        return ApiResponse.<OrderDetailsResponse>builder()
//                .result(result)
//                .build();
//    }
}