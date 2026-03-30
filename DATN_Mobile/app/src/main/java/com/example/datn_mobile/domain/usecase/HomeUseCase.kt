package com.example.datn_mobile.domain.usecase

import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.model.Product
import com.example.datn_mobile.domain.repository.ProductRepository
import javax.inject.Inject

class HomeUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(): Resource<List<Product>> {
        return productRepository.getHomeProducts()
    }
}

