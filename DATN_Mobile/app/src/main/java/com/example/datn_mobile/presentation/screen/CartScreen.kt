package com.example.datn_mobile.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.datn_mobile.domain.model.Cart
import com.example.datn_mobile.domain.model.CartItem
import com.example.datn_mobile.presentation.viewmodel.CartViewModel

@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {}
) {
    val cartState by viewModel.cartState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        CartHeader(onBackClick = onBackClick)

        // Content
        if (cartState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (cartState.cart == null || cartState.cart!!.items.isEmpty()) {
            EmptyCartScreen(onContinueShopping = onBackClick)
        } else {
            CartContent(
                cart = cartState.cart!!,
                isUpdating = cartState.isUpdating,
                onCheckout = onCheckoutClick
            )
        }
    }
}

@Composable
private fun CartHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2196F3))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                tint = Color.White
            )
        }

        Text(
            text = "Giỏ Hàng",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun EmptyCartScreen(onContinueShopping: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🛒",
                fontSize = 64.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Giỏ hàng của bạn trống",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Thêm sản phẩm để bắt đầu mua sắm",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = onContinueShopping,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text("Tiếp tục mua sắm")
            }
        }
    }
}

@Composable
private fun CartContent(
    cart: Cart,
    isUpdating: Boolean,
    onCheckout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Cart items list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cart.items) { item ->
                CartItemCard(item = item)
            }
        }

        // Summary & Checkout
        CartSummary(
            cart = cart,
            isUpdating = isUpdating,
            onCheckout = onCheckout
        )
    }
}

@Composable
private fun CartItemCard(
    item: CartItem
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.productName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${item.price.toFormattedPrice()} đ",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quantity display
                Text(
                    text = "Số lượng: ${item.quantity}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Total price
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(0.5f)
            ) {
                Text(
                    text = "${(item.price * item.quantity).toFormattedPrice()} đ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
            }
        }
    }
}

@Composable
private fun CartSummary(
    cart: Cart,
    isUpdating: Boolean,
    onCheckout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Summary items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Số lượng sản phẩm:", fontSize = 14.sp, color = Color.Gray)
            Text(cart.totalQuantity.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tổng tiền:", fontSize = 14.sp, color = Color.Gray)
            Text(
                "${cart.totalPrice.toFormattedPrice()} đ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Checkout button
        Button(
            onClick = onCheckout,
            enabled = !isUpdating && cart.items.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Text(
                text = if (isUpdating) "Đang cập nhật..." else "Thanh toán",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Helper function to format price
fun Long.toFormattedPrice(): String {
    return String.format(java.util.Locale.getDefault(), "%,d", this)
}

