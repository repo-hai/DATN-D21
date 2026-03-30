package com.example.datn_mobile.presentation.register

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.datn_mobile.presentation.viewmodel.RegisterState
import com.example.datn_mobile.presentation.viewmodel.RegisterViewModel
import com.example.compose.DATN_MobileTheme


@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.isRegisterSuccess, state.errorMessage) {
        if (state.isRegisterSuccess) {
            Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
            onRegisterSuccess()
        }
        if (state.errorMessage != null) {
            Toast.makeText(context, state.errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    RegisterContent(
        state = state,
        onRegisterClicked = { phoneNumber, password, confirmPass ->
            viewModel.onRegisterClicked(phoneNumber, password, confirmPass)
        },
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun RegisterContent(
    state: RegisterState,
    onRegisterClicked: (String, String, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Đăng ký tài khoản", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Số điện thoại") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Xác nhận mật khẩu") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                onRegisterClicked(phoneNumber, password, confirmPassword)
            },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Đăng ký")
        }

        if (state.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateBack) {
            Text("Đã có tài khoản? Đăng nhập")
        }
    }
}

@Preview(showBackground = true, name = "Trạng thái mặc định")
@Composable
fun RegisterScreenPreview() {
    DATN_MobileTheme {
        RegisterContent(
            state = RegisterState(isLoading = false, errorMessage = null),
            onRegisterClicked = { _, _, _ -> },
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Trạng thái Đang tải")
@Composable
fun RegisterScreenLoadingPreview() {
    DATN_MobileTheme {
        RegisterContent(
            state = RegisterState(isLoading = true, errorMessage = null),
            onRegisterClicked = { _, _, _ -> },
            onNavigateBack = {}
        )
    }
}