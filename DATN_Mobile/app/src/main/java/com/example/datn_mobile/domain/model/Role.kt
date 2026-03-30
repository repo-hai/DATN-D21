package com.example.datn_mobile.domain.model

import androidx.datastore.preferences.protobuf.EmptyOrBuilder

enum class Role(val displayName: String) {
    ADMIN("Quản trị viên"),
    EMPLOYEE_MANAGER("Quản lý nhân viên"),
    SHOP_MANAGER("Quản lý cửa hàng"),
    USER("Người dùng"),
    UNKNOWN("Không xác định");

    /**
     * safety convert from String to Role
     * return UNKNOWN if not match
     */
    companion object {
        fun fromString(value: String?) : Role {
            return when (value?.uppercase()) {
                "ADMIN" -> ADMIN
                "EMPLOYEE_MANAGER" -> EMPLOYEE_MANAGER
                "SHOP_MANAGER" -> SHOP_MANAGER
                "USER" -> USER
                else -> UNKNOWN
            }
        }
    }
}