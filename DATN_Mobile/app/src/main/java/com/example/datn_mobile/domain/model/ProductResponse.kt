package com.example.datn_mobile.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ============= HOME PAGE - Danh sách sản phẩm =============
@JsonClass(generateAdapter = true)
data class ProductVariant(
    @field:Json(name = "originalPrice")
    val originalPrice: Long? = null,
    @field:Json(name = "finalPrice")
    val finalPrice: Long? = null,
    @field:Json(name = "thumbnail")
    val thumbnail: String? = null,
    @field:Json(name = "color")
    val color: String? = null,
    @field:Json(name = "price")
    val price: Long? = null,
    @field:Json(name = "quantity")
    val quantity: Int? = null
)

@JsonClass(generateAdapter = true)
data class Product(
    @field:Json(name = "id")
    val id: String,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "image")
    val image: String,
    @field:Json(name = "status")
    val status: Int,
    @field:Json(name = "createDate")
    val createDate: String,
    @field:Json(name = "variant")
    val variant: ProductVariant
)

@JsonClass(generateAdapter = true)
data class HomeResponse(
    @field:Json(name = "result")
    val result: List<Product>,
    @field:Json(name = "code")
    val code: Int,
    @field:Json(name = "message")
    val message: String? = null
)

// ============= PRODUCT DETAIL PAGE - Chi tiết sản phẩm =============
@JsonClass(generateAdapter = true)
data class ProductDetailImage(
    @field:Json(name = "url")
    val url: String
)

@JsonClass(generateAdapter = true)
data class ProductDetailVariantAttribute(
    @field:Json(name = "id")
    val id: String,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "originalPrice")
    val originalPrice: Long? = null,
    @field:Json(name = "finalPrice")
    val finalPrice: Long? = null,
    @field:Json(name = "discount")
    val discount: Double? = null
)

@JsonClass(generateAdapter = true)
data class ProductDetailVariant(
    @field:Json(name = "color")
    val color: String,
    @field:Json(name = "detailImages")
    val detailImages: List<ProductDetailImage>? = null,
    @field:Json(name = "attributes")
    val attributes: List<ProductDetailVariantAttribute>? = null
)

@JsonClass(generateAdapter = true)
data class ProductDetail(
    @field:Json(name = "id")
    val id: String,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "image")
    val image: String,
    @field:Json(name = "status")
    val status: Int,
    @field:Json(name = "introImages")
    val introImages: List<ProductDetailImage>? = null,
    @field:Json(name = "variants")
    val variants: List<ProductDetailVariant>? = null
)

@JsonClass(generateAdapter = true)
data class ProductDetailResponse(
    @field:Json(name = "result")
    val result: ProductDetail,
    @field:Json(name = "code")
    val code: Int,
    @field:Json(name = "message")
    val message: String? = null
)
