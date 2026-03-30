package com.example.datn_mobile.data.repository

import com.example.datn_mobile.data.network.api.CartApiService
import com.example.datn_mobile.data.network.api.CartItemResponse
import com.example.datn_mobile.data.network.api.PlaceOrderItemRequest
import com.example.datn_mobile.data.network.api.PlaceOrderRequest
import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.model.Cart
import com.example.datn_mobile.domain.model.CartItem
import com.example.datn_mobile.domain.model.Order
import com.example.datn_mobile.domain.repository.CartRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartApiService: CartApiService
) : CartRepository {

    /**
     * 1️⃣ Thêm sản phẩm vào giỏ hàng
     * POST /bej3/cart/add/{attId}
     *
     * Trả về CartItemResponse (chi tiết sản phẩm được thêm)
     */
    override suspend fun addToCart(attId: String): Resource<CartItem> {
        return try {
            val response = cartApiService.addToCart(attId)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.result != null) {
                    Resource.Success(apiResponse.result.toCartItemDomain())
                } else {
                    Resource.Error("Phản hồi thêm vào giỏ rỗng")
                }
            } else {
                when (response.code()) {
                    404 -> Resource.Error("Sản phẩm không tồn tại")
                    400 -> Resource.Error("ID sản phẩm không hợp lệ")
                    401 -> Resource.Error("Vui lòng đăng nhập")
                    else -> Resource.Error("Lỗi thêm vào giỏ: ${response.message()}")
                }
            }
        } catch (e: HttpException) {
            Resource.Error("Lỗi mạng: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Lỗi kết nối: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Lỗi không xác định: ${e.message}")
        }
    }

    /**
     * 2️⃣ Xem danh sách giỏ hàng
     * GET /bej3/cart/view
     *
     * Trả về List<CartItemResponse>
     */
    override suspend fun getCart(): Resource<Cart> {
        return try {
            val response = cartApiService.getCart()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.result != null) {
                    Resource.Success(apiResponse.result.toCartDomain())
                } else {
                    Resource.Error("Giỏ hàng rỗng")
                }
            } else {
                when (response.code()) {
                    401 -> Resource.Error("Vui lòng đăng nhập")
                    else -> Resource.Error("Lỗi lấy giỏ hàng: ${response.message()}")
                }
            }
        } catch (e: HttpException) {
            Resource.Error("Lỗi mạng: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Lỗi kết nối: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Lỗi không xác định: ${e.message}")
        }
    }

    /**
     * 3️⃣ Đặt hàng (Place Order)
     * POST /bej3/cart/place-order
     *
     * Trả về OrderDetailsResponse
     */
    override suspend fun placeOrder(
        phoneNumber: String,
        email: String,
        address: String,
        description: String?,
        totalPrice: Long,
        items: List<Pair<String, String>>  // Pair<cartItemId, productAttId>
    ): Resource<Order> {
        return try {
            // Chuẩn bị request
            val orderItems = items.map { (cartItemId, productAttId) ->
                PlaceOrderItemRequest(
                    cartItemId = cartItemId,
                    productAttId = productAttId,
                    quantity = 1  // Lấy từ giỏ, mặc định 1
                )
            }

            val request = PlaceOrderRequest(
                phoneNumber = phoneNumber,
                email = email,
                address = address,
                description = description,
                totalPrice = totalPrice,
                items = orderItems
            )

            val response = cartApiService.placeOrder(request)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.result != null) {
                    Resource.Success(apiResponse.result.toOrderDomain())
                } else {
                    Resource.Error("Phản hồi đặt hàng rỗng")
                }
            } else {
                when (response.code()) {
                    400 -> Resource.Error("Thông tin đơn hàng không hợp lệ")
                    401 -> Resource.Error("Vui lòng đăng nhập")
                    404 -> Resource.Error("Sản phẩm không tồn tại")
                    else -> Resource.Error("Lỗi đặt hàng: ${response.message()}")
                }
            }
        } catch (e: HttpException) {
            Resource.Error("Lỗi mạng: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Lỗi kết nối: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Lỗi không xác định: ${e.message}")
        }
    }

    /**
     * 4️⃣ Xem lịch sử đơn hàng
     * GET /bej3/cart/my-order
     *
     * Trả về List<OrderDetailsResponse>
     */
    override suspend fun getMyOrders(): Resource<List<Order>> {
        return try {
            val response = cartApiService.getMyOrders()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.result != null) {
                    Resource.Success(apiResponse.result.map { it.toOrderDomain() })
                } else {
                    Resource.Error("Danh sách đơn hàng rỗng")
                }
            } else {
                when (response.code()) {
                    401 -> Resource.Error("Vui lòng đăng nhập")
                    else -> Resource.Error("Lỗi lấy đơn hàng: ${response.message()}")
                }
            }
        } catch (e: HttpException) {
            Resource.Error("Lỗi mạng: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Lỗi kết nối: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Lỗi không xác định: ${e.message}")
        }
    }
}

// Extension functions to convert API response to domain model

/**
 * Chuyển CartItemResponse thành CartItem
 */
fun CartItemResponse.toCartItemDomain(): CartItem {
    return CartItem(
        id = this.id,
        attId = this.attId,
        productAttName = this.productAttName,
        quantity = this.quantity,
        price = this.price,
        color = this.color,
        productName = this.productName,
        img = this.img
    )
}

/**
 * Chuyển List<CartItemResponse> thành Cart
 */
fun List<CartItemResponse>.toCartDomain(): Cart {
    val totalPrice = this.sumOf { it.price * it.quantity }
    val totalQuantity = this.sumOf { it.quantity }

    return Cart(
        id = "",  // Backend không return id cho cart
        items = this.map { it.toCartItemDomain() },
        totalPrice = totalPrice,
        totalQuantity = totalQuantity
    )
}

/**
 * Chuyển OrderDetailsResponse thành Order
 */
fun com.example.datn_mobile.data.network.api.OrderDetailsResponse.toOrderDomain(): Order {
    return Order(
        id = this.id,
        userName = this.userName,
        phoneNumber = this.phoneNumber,
        email = this.email,
        address = this.address,
        description = this.description,
        totalPrice = this.totalPrice,
        orderAt = this.orderAt,
        updatedAt = this.updatedAt,
        orderItems = this.orderItems.map { item ->
            com.example.datn_mobile.domain.model.OrderItem(
                productAttName = item.productAttName,
                quantity = item.quantity,
                price = item.price,
                color = item.color,
                productName = item.productName,
                img = item.img
            )
        }
    )
}



