package com.example.datn_mobile.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Event Bus để phát sự kiện message
 * Sử dụng SharedFlow để có thể có nhiều subscriber
 */
object MessageBus {
    private val _messageFlow = MutableSharedFlow<Message>(replay = 0)
    val messageFlow: SharedFlow<Message> = _messageFlow.asSharedFlow()

    suspend fun sendMessage(message: Message) {
        _messageFlow.emit(message)
    }
}

