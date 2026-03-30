package com.example.datn_mobile.data.network.api

import com.example.datn_mobile.domain.model.HomeResponse
import com.example.datn_mobile.domain.model.ProductDetailResponse
import com.example.datn_mobile.data.network.interceptor.NoAuth
import com.example.datn_mobile.domain.model.BaseResponse
import com.example.datn_mobile.domain.model.CartItem
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Response

interface ProductApiService {
    @NoAuth
    @GET("/bej3/home")
    suspend fun getHomeProducts(): Response<HomeResponse>

    @NoAuth
    @GET("/bej3/home/product/{productId}")
    suspend fun getProductDetail(@Path("productId") productId: String): Response<ProductDetailResponse>

    @POST("/bej3/cart/add/{attId}")
    suspend fun addToCart(@Path("attId") attId: String): Response<BaseResponse<CartItem>>
}

