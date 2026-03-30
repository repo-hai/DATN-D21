package com.example.datn_mobile.domain.repository

import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.model.Cart
import com.example.datn_mobile.domain.model.CartItem
import com.example.datn_mobile.domain.model.Order

interface CartRepository {
    /**
     * 1️⃣ Thêm sản phẩm vào giỏ hàng
     * POST /bej3/cart/add/{attId}
     */
    suspend fun addToCart(attId: String): Resource<CartItem>

    /**
     * 2️⃣ Xem danh sách giỏ hàng
     * GET /bej3/cart/view
     */
    suspend fun getCart(): Resource<Cart>

    /**
     * 3️⃣ Đặt hàng (Place Order)
     * POST /bej3/cart/place-order
     */
    suspend fun placeOrder(
        phoneNumber: String,
        email: String,
        address: String,
        description: String?,
        totalPrice: Long,
        items: List<Pair<String, String>>  // Pair<cartItemId, productAttId>
    ): Resource<Order>

    /**
     * 4️⃣ Xem lịch sử đơn hàng
     * GET /bej3/cart/my-order
     */
    suspend fun getMyOrders(): Resource<List<Order>>
}

