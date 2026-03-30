package com.example.datn_mobile.config

/**
 * Cấu hình API Base URL
 * 
 * LƯU Ý:
 * - Android Emulator: Sử dụng 10.0.2.2 để trỏ đến localhost của máy host
 * - Device thật: Sử dụng IP thực của máy (192.168.x.x) hoặc localhost nếu cùng mạng
 * - Docker: API chạy ở http://localhost:8080/bej3
 */
object ApiConfig {
    // Base URL cho Docker (localhost)
    // Cho Android Emulator: 10.0.2.2 là địa chỉ đặc biệt để trỏ đến localhost của máy host
    // Cho Device thật: Thay bằng IP thực của máy (ví dụ: 192.168.1.100)
    // LƯU Ý: 
    // - Retrofit yêu cầu baseUrl phải kết thúc bằng dấu "/"
    // - Không thêm "/bej3" vào baseUrl vì các API endpoints đã có prefix "/bej3" sẵn
    const val BASE_URL = "http://10.0.2.2:8080/"
    
    // Hoặc nếu test trên device thật, uncomment dòng dưới và thay IP:
    // const val BASE_URL = "http://192.168.1.100:8080/"
    
    // Timeout settings
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
}

