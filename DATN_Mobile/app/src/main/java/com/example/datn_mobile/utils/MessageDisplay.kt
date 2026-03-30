package com.example.datn_mobile.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Composable hiển thị message/thông báo tại top của màn hình
 * Tự động tắt sau duration được quy định
 */
@Composable
fun MessageDisplay() {
    var currentMessage by remember { mutableStateOf<Message?>(null) }
    var isVisible by remember { mutableStateOf(false) }

    // Collect messages từ MessageBus
    LaunchedEffect(Unit) {
        MessageBus.messageFlow.collect { message ->
            currentMessage = message
            isVisible = true
            // Tự động ẩn sau duration
            delay(message.duration)
            isVisible = false
        }
    }

    // Hiển thị message với animation
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        currentMessage?.let { message ->
            MessageDisplayCard(message)
        }
    }
}

@Composable
private fun MessageDisplayCard(message: Message) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = getMessageBackgroundColor(message.type),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Icon(
                imageVector = getMessageIcon(message.type),
                contentDescription = message.type.toString(),
                tint = getMessageIconColor(message.type),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.size(12.dp))

            // Text
            Text(
                text = message.text,
                color = getMessageTextColor(message.type),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun getMessageBackgroundColor(type: Message.MessageType): Color = when (type) {
    Message.MessageType.INFO -> Color(0xFFE3F2FD)      // Light Blue
    Message.MessageType.SUCCESS -> Color(0xFFE8F5E9)   // Light Green
    Message.MessageType.WARNING -> Color(0xFFFFF3E0)   // Light Orange
    Message.MessageType.ERROR -> Color(0xFFFFEBEE)     // Light Red
}

private fun getMessageTextColor(type: Message.MessageType): Color = when (type) {
    Message.MessageType.INFO -> Color(0xFF1976D2)      // Blue
    Message.MessageType.SUCCESS -> Color(0xFF388E3C)   // Green
    Message.MessageType.WARNING -> Color(0xFFF57C00)   // Orange
    Message.MessageType.ERROR -> Color(0xFFC62828)     // Red
}

private fun getMessageIconColor(type: Message.MessageType): Color = when (type) {
    Message.MessageType.INFO -> Color(0xFF1976D2)      // Blue
    Message.MessageType.SUCCESS -> Color(0xFF388E3C)   // Green
    Message.MessageType.WARNING -> Color(0xFFF57C00)   // Orange
    Message.MessageType.ERROR -> Color(0xFFC62828)     // Red
}

private fun getMessageIcon(type: Message.MessageType): ImageVector = when (type) {
    Message.MessageType.INFO -> Icons.Filled.Info
    Message.MessageType.SUCCESS -> Icons.Filled.CheckCircle
    Message.MessageType.WARNING -> Icons.Filled.Warning
    Message.MessageType.ERROR -> Icons.Filled.Close
}

