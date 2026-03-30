package com.example.datn_mobile.data.network.api

import com.example.datn_mobile.data.network.dto.ApiResponse
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CartApiService {

    /**
     * 1️⃣ Thêm sản phẩm vào giỏ hàng
     * POST /bej3/cart/add/{attId}
     *
     * Nếu sản phẩm chưa có trong giỏ → quantity = 1
     * Nếu sản phẩm đã có trong giỏ → quantity += 1
     */
    @POST("/bej3/cart/add/{attId}")
    suspend fun addToCart(@Path("attId") attId: String): Response<ApiResponse<CartItemResponse>>

    /**
     * 2️⃣ Xem danh sách giỏ hàng
     * GET /bej3/cart/view
     */
    @GET("/bej3/cart/view")
    suspend fun getCart(): Response<ApiResponse<List<CartItemResponse>>>

    /**
     * 3️⃣ Đặt hàng (Place Order)
     * POST /bej3/cart/place-order
     *
     * Sau khi đặt hàng thành công, toàn bộ CartItem sẽ bị xóa khỏi giỏ
     */
    @POST("/bej3/cart/place-order")
    suspend fun placeOrder(@Body request: PlaceOrderRequest): Response<ApiResponse<OrderDetailsResponse>>

    /**
     * 4️⃣ Xem lịch sử đơn hàng của tôi
     * GET /bej3/cart/my-order
     */
    @GET("/bej3/cart/my-order")
    suspend fun getMyOrders(): Response<ApiResponse<List<OrderDetailsResponse>>>
}

// DTO classes for API response

/**
 * Response từ API thêm vào giỏ / xem giỏ hàng
 * Mỗi item đại diện 1 sản phẩm trong giỏ
 */
@JsonClass(generateAdapter = true)
data class CartItemResponse(
    val id: String,                  // ID CartItem (dùng khi place order)
    val attId: String,               // ID ProductAttribute
    val productAttName: String,      // Tên biến thể (VD: "12 / 256 GB")
    val quantity: Int,               // Số lượng (mặc định 1, nếu add lại → +1)
    val price: Long,                 // Giá sản phẩm
    val color: String,               // Màu sắc
    val productName: String,         // Tên sản phẩm
    val img: String                  // URL ảnh
)

/**
 * Request body cho API Place Order
 */
@JsonClass(generateAdapter = true)
data class PlaceOrderRequest(
    val phoneNumber: String,         // Số điện thoại giao hàng (bắt buộc)
    val email: String,               // Email giao hàng (bắt buộc)
    val address: String,             // Địa chỉ giao hàng (bắt buộc)
    val description: String? = null, // Ghi chú, không bắt buộc
    val totalPrice: Long,            // Tổng tiền (bắt buộc)
    val items: List<PlaceOrderItemRequest>  // Danh sách sản phẩm cần order (bắt buộc)
)

/**
 * Item trong PlaceOrderRequest
 */
@JsonClass(generateAdapter = true)
data class PlaceOrderItemRequest(
    val cartItemId: String,          // ID từ giỏ hàng
    val productAttId: String,        // ID ProductAttribute
    val quantity: Int                // Số lượng
)

/**
 * Response từ API Place Order / My Orders
 */
@JsonClass(generateAdapter = true)
data class OrderDetailsResponse(
    val id: String,                  // ID đơn hàng
    val userName: String,            // Tên người dùng
    val phoneNumber: String,         // SĐT giao hàng
    val email: String,               // Email giao hàng
    val address: String,             // Địa chỉ giao hàng
    val description: String? = null, // Ghi chú
    val totalPrice: Long,            // Tổng tiền
    val orderAt: String,             // Ngày đặt hàng (YYYY-MM-DD)
    val updatedAt: String? = null,   // Ngày cập nhật
    val orderItems: List<OrderItemResponse> // Chi tiết các sản phẩm trong đơn
)

/**
 * Item trong OrderDetailsResponse
 */
@JsonClass(generateAdapter = true)
data class OrderItemResponse(
    val productAttName: String,      // Tên biến thể
    val quantity: Int,               // Số lượng
    val price: Long,                 // Giá
    val color: String,               // Màu sắc
    val productName: String,         // Tên sản phẩm
    val img: String                  // URL ảnh
)

