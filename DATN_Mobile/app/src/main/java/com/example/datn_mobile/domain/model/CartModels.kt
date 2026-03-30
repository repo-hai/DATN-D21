package com.example.datn_mobile.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Generic response wrapper
@JsonClass(generateAdapter = true)
data class BaseResponse<T>(
    @field:Json(name = "result")
    val result: T?,
    @field:Json(name = "code")
    val code: Int,
    @field:Json(name = "message")
    val message: String? = null
)

/**
 * Đại diện 1 sản phẩm trong giỏ hàng
 * Tương ứng với CartItemResponse từ API
 */
@JsonClass(generateAdapter = true)
data class CartItem(
    @field:Json(name = "id")
    val id: String,                  // ID CartItem
    @field:Json(name = "attId")
    val attId: String,               // ID ProductAttribute
    @field:Json(name = "productAttName")
    val productAttName: String,      // Tên biến thể (VD: "12 / 256 GB")
    @field:Json(name = "quantity")
    val quantity: Int,               // Số lượng
    @field:Json(name = "price")
    val price: Long,                 // Giá sản phẩm
    @field:Json(name = "color")
    val color: String,               // Màu sắc
    @field:Json(name = "productName")
    val productName: String,         // Tên sản phẩm
    @field:Json(name = "img")
    val img: String                  // URL ảnh
)

/**
 * Giỏ hàng của user
 * Chứa danh sách CartItem
 */
@JsonClass(generateAdapter = true)
data class Cart(
    @field:Json(name = "id")
    val id: String = "",
    @field:Json(name = "items")
    val items: List<CartItem> = emptyList(),
    @field:Json(name = "totalPrice")
    val totalPrice: Long = 0,
    @field:Json(name = "totalQuantity")
    val totalQuantity: Int = 0
)

/**
 * Đơn hàng của user
 * Tương ứng với OrderDetailsResponse từ API
 */
@JsonClass(generateAdapter = true)
data class Order(
    @field:Json(name = "id")
    val id: String,                  // ID đơn hàng
    @field:Json(name = "userName")
    val userName: String,            // Tên người dùng
    @field:Json(name = "phoneNumber")
    val phoneNumber: String,         // SĐT giao hàng
    @field:Json(name = "email")
    val email: String,               // Email giao hàng
    @field:Json(name = "address")
    val address: String,             // Địa chỉ giao hàng
    @field:Json(name = "description")
    val description: String? = null, // Ghi chú
    @field:Json(name = "totalPrice")
    val totalPrice: Long,            // Tổng tiền
    @field:Json(name = "orderAt")
    val orderAt: String,             // Ngày đặt hàng (YYYY-MM-DD)
    @field:Json(name = "updatedAt")
    val updatedAt: String? = null,   // Ngày cập nhật
    @field:Json(name = "orderItems")
    val orderItems: List<OrderItem> = emptyList()  // Danh sách sản phẩm
)

/**
 * Chi tiết sản phẩm trong đơn hàng
 * Tương ứng với OrderItemResponse từ API
 */
@JsonClass(generateAdapter = true)
data class OrderItem(
    @field:Json(name = "productAttName")
    val productAttName: String,      // Tên biến thể
    @field:Json(name = "quantity")
    val quantity: Int,               // Số lượng
    @field:Json(name = "price")
    val price: Long,                 // Giá
    @field:Json(name = "color")
    val color: String,               // Màu sắc
    @field:Json(name = "productName")
    val productName: String,         // Tên sản phẩm
    @field:Json(name = "img")
    val img: String                  // URL ảnh
)
