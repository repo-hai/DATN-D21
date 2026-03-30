package com.example.datn_mobile.presentation.navigation

sealed class Routes(val route: String) {
    object Splash : Routes("splash_screen")
    object Login : Routes("login_screen")
    object Register: Routes("register_screen")
    object ForgotPassword : Routes("forgot_password_screen")
    object Home : Routes("home_screen")
    object Search : Routes("search_screen")
    object Profile : Routes("profile_screen")
    object EditProfile : Routes("edit_profile_screen")
    object Cart : Routes("cart_screen")
    object ProductDetail : Routes("product_detail_screen")
    object Detail : Routes("detail_screen/{itemId}") {
        fun createRoute(itemId: String) = "detail_screen/$itemId"
    }
}

