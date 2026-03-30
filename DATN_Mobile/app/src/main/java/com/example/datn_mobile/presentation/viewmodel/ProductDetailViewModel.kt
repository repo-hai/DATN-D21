package com.example.datn_mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.model.ProductDetail
import com.example.datn_mobile.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailState(
    val product: ProductDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAddingToCart: Boolean = false,
    val addToCartSuccess: Boolean = false,
    val addToCartError: String? = null
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _productDetailState = MutableStateFlow(ProductDetailState())
    val productDetailState = _productDetailState.asStateFlow()

    fun loadProductDetail(productId: String) {
        viewModelScope.launch {
            _productDetailState.value = ProductDetailState(isLoading = true)

            when (val result = productRepository.getProductDetail(productId)) {
                is Resource.Success -> {
                    _productDetailState.value = ProductDetailState(
                        product = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _productDetailState.value = ProductDetailState(
                        product = null,
                        isLoading = false,
                        error = result.message
                    )
                }
                else -> { }
            }
        }
    }

    fun addToCart(attId: String) {
        viewModelScope.launch {
            _productDetailState.value = _productDetailState.value.copy(isAddingToCart = true)

            when (val result = productRepository.addToCart(attId)) {
                is Resource.Success -> {
                    _productDetailState.value = _productDetailState.value.copy(
                        isAddingToCart = false,
                        addToCartSuccess = true,
                        addToCartError = null
                    )
                }
                is Resource.Error -> {
                    _productDetailState.value = _productDetailState.value.copy(
                        isAddingToCart = false,
                        addToCartSuccess = false,
                        addToCartError = result.message
                    )
                }
                else -> { }
            }
        }
    }
}
