package com.example.datn_mobile.utils

import androidx.annotation.Keep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Singleton Manager quản lý thông báo
 * Sử dụng để gửi message và tự động hiển thị chúng
 */
@Keep
object MessageManager {
    private val scope = CoroutineScope(Dispatchers.Main)

    /**
     * Gửi thông báo INFO
     */
    fun showInfo(text: String, duration: Long = 1000L) {
        showMessage(Message(
            text = text,
            type = Message.MessageType.INFO,
            duration = duration
        ))
    }

    /**
     * Gửi thông báo SUCCESS
     */
    fun showSuccess(text: String, duration: Long = 1000L) {
        showMessage(Message(
            text = text,
            type = Message.MessageType.SUCCESS,
            duration = duration
        ))
    }

    /**
     * Gửi thông báo WARNING
     */
    fun showWarning(text: String, duration: Long = 1000L) {
        showMessage(Message(
            text = text,
            type = Message.MessageType.WARNING,
            duration = duration
        ))
    }

    /**
     * Gửi thông báo ERROR
     */
    fun showError(text: String, duration: Long = 2000L) {
        showMessage(Message(
            text = text,
            type = Message.MessageType.ERROR,
            duration = duration
        ))
    }

    /**
     * Gửi thông báo custom
     */
    fun showMessage(message: Message) {
        scope.launch {
            try {
                MessageBus.sendMessage(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

