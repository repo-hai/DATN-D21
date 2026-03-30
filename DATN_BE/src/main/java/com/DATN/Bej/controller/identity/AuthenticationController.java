package com.DATN.Bej.controller.identity;

import com.DATN.Bej.dto.request.ApiResponse;
import com.DATN.Bej.dto.request.identityRequest.AuthenticationRequest;
import com.DATN.Bej.dto.request.identityRequest.IntrospectRequest;
import com.DATN.Bej.dto.response.UserResponse;
import com.DATN.Bej.dto.response.identity.AuthenticationResponse;
import com.DATN.Bej.dto.response.identity.IntrospectResponse;
import com.DATN.Bej.service.FcmDeviceTokenService;
import com.DATN.Bej.service.identity.AuthenticationService;
import com.DATN.Bej.service.identity.UserService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;
    FcmDeviceTokenService fcmDeviceTokenService;
    UserService userService;

    @PostMapping("/log-in")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        System.out.println("Received request: " + request);
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    /**
     * GET /auth/me
     * Lấy thông tin user hiện tại từ JWT token
     * Endpoint này yêu cầu authentication (có Bearer token trong header)
     */
    @GetMapping("/me")
    ApiResponse<UserResponse> getCurrentUser() {
        log.info("📋 Getting current user info from token");
        UserResponse userInfo = userService.getMyInfo();
        log.info("✅ Retrieved user info for: {}", userInfo.getPhoneNumber());
        return ApiResponse.<UserResponse>builder()
                .result(userInfo)
                .build();
    }

    @PostMapping("/logout")
    ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request)
            throws ParseException, JOSEException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.<Void>builder()
                            .message("Missing or invalid Authorization header")
                            .build()
            );
        }

        String token = authHeader.substring(7);
        IntrospectRequest introspectRequest = IntrospectRequest.builder()
                .token(token)
                .build();
        
        // 🔐 Logout người dùng từ hệ thống - thêm token vào blacklist
        // Method này trả về phoneNumber từ JWT token để xóa FCM tokens
        String phoneNumber = authenticationService.logout(introspectRequest);
        
        // 🗑️ Xóa tất cả FCM tokens của user (để không nhận push notification sau khi logout)
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            try {
                fcmDeviceTokenService.deleteAllTokensForUser(phoneNumber);
                log.info("✅ FCM tokens deleted for user: {}", phoneNumber);
            } catch (Exception e) {
                log.warn("⚠️ Could not delete FCM tokens during logout: {}", e.getMessage());
                // Không throw exception, logout vẫn thành công ngay cả khi xóa FCM token thất bại
            }
        } else {
            log.warn("⚠️ PhoneNumber is null or empty, cannot delete FCM tokens");
        }
        
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("Logged out successfully")
                        .build()
        );
    }

}
