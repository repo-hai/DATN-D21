package com.example.datn_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.compose.DATN_MobileTheme
import com.example.datn_mobile.presentation.navigation.AppNavigation
import com.example.datn_mobile.utils.MessageDisplay
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DATN_MobileTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Main navigation
        AppNavigation()

        // Message display ở top của màn hình - không được phủ bởi AppNavigation
        MessageDisplay()
    }
}
