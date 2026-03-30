package com.example.datn_mobile.domain.repository

import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.model.Product
import com.example.datn_mobile.domain.model.ProductDetail

interface ProductRepository {
    suspend fun getHomeProducts(): Resource<List<Product>>
    suspend fun getProductDetail(productId: String): Resource<ProductDetail>
    suspend fun addToCart(attId: String): Resource<Any> // Thêm hàm này
}

