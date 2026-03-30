package com.example.datn_mobile.utils

/**
 * Data class đại diện cho một message/thông báo
 */
data class Message(
    val id: String = System.nanoTime().toString(),
    val text: String,
    val type: MessageType = MessageType.INFO,
    val duration: Long = 1000L  // milliseconds
) {
    enum class MessageType {
        INFO,      //  Thông tin
        SUCCESS,   // Thành công
        WARNING,   //  Cảnh báo
        ERROR      // Lỗi
    }
}

