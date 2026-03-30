package com.example.datn_mobile.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.DATN_MobileTheme

@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit = {},
    onResetClick: (String) -> Unit = {}
) {
    ForgotPasswordContent(
        onBackClick = onBackClick,
        onResetClick = onResetClick
    )
}

@Composable
fun ForgotPasswordContent(
    onBackClick: () -> Unit = {},
    onResetClick: (String) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(0.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.Black
                )
            }

            Text(
                text = "Quên Mật Khẩu",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
        }

        // Form content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Instructions
            Text(
                text = "Nhập địa chỉ email của bạn để nhận hướng dẫn đặt lại mật khẩu",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email hoặc Tên Đăng Nhập") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Reset button
            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        isLoading = true
                        errorMessage = null
                        successMessage = null
                        onResetClick(email)
                        // Simulate loading
                        isLoading = false
                        successMessage = "Đã gửi hướng dẫn đặt lại mật khẩu đến email của bạn"
                    } else {
                        errorMessage = "Vui lòng nhập email hoặc tên đăng nhập"
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("Gửi Hướng Dẫn")
                }
            }

            // Messages
            successMessage?.let { message ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = Color(0xFF4CAF50),
                    fontSize = 12.sp
                )
            }

            errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Back to login link
            androidx.compose.material3.TextButton(
                onClick = onBackClick
            ) {
                Text(
                    text = "Quay lại Đăng Nhập",
                    fontSize = 14.sp,
                    color = Color(0xFF2196F3)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "default status")
@Composable
fun ForgotPasswordPreview() {
    DATN_MobileTheme {
        ForgotPasswordContent(
            onBackClick = {},
            onResetClick = {}
        )
    }
}

