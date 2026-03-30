package com.example.datn_mobile.data.util

/**
 * Hướng dẫn cách dùng:
 * Đối với các gói tin mất nhiều thời gian thì Repository tốt nhất nenen trả về một Flow
 * Khi bắt đầu thực hiện thao tác dữ liệu với backend cần phát đi một emit Loading
 * emit(Resource.Loading())
 * Phía ViewModel hay UseCase cần bắt flow này bằng cách sử dụng .collect{ result -> ...}
 */

sealed class Resource<T> (
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(val isLoading: Boolean = true) : Resource<T>(null)
}