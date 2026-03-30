package com.DATN.Bej.service;

import com.DATN.Bej.dto.request.fcm.RegisterFcmTokenRequest;
import com.DATN.Bej.dto.response.fcm.FcmTokenResponse;
import com.DATN.Bej.entity.FcmDeviceToken;
import com.DATN.Bej.entity.identity.User;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.mapper.FcmDeviceTokenMapper;
import com.DATN.Bej.repository.FcmDeviceTokenRepository;
import com.DATN.Bej.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class FcmDeviceTokenService {

    FcmDeviceTokenRepository fcmDeviceTokenRepository;
    UserRepository userRepository;
    FcmDeviceTokenMapper fcmDeviceTokenMapper;

    /**
     * Đăng ký FCM token từ mobile client
     * Note: userId từ JWT token là phoneNumber, không phải UUID id
     */
    public FcmTokenResponse registerToken(String phoneNumber, RegisterFcmTokenRequest request) {
        log.info("📱 Registering FCM token for user: {}", phoneNumber);

        // Kiểm tra user tồn tại - tìm bằng phoneNumber (vì JWT sub = phoneNumber)
        User user = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> {
                log.error("❌ User not found with phoneNumber: {}", phoneNumber);
                return new AppException(ErrorCode.USER_NOT_EXISTED);
            });

        // Kiểm tra token đã được register trước không
        var existingToken = fcmDeviceTokenRepository.findByToken(request.getToken());
        if (existingToken.isPresent()) {
            log.info("⚠️ Token already registered, updating lastUsed");
            // Update lastUsed time
            FcmDeviceToken token = existingToken.get();
            token.setLastUsed(Instant.now());
            fcmDeviceTokenRepository.save(token);
            return fcmDeviceTokenMapper.toResponse(token);
        }

        // Tạo token mới
        FcmDeviceToken token = new FcmDeviceToken();
        token.setUser(user);
        token.setToken(request.getToken());
        token.setLastUsed(Instant.now());

        FcmDeviceToken savedToken = fcmDeviceTokenRepository.save(token);
        log.info("✅ FCM token registered successfully for user: {}", phoneNumber);

        return fcmDeviceTokenMapper.toResponse(savedToken);
    }

    /**
     * Xóa một token cụ thể
     * Note: phoneNumber từ JWT token, không phải userId
     */
    public void deleteToken(String tokenId, String phoneNumber) {
        log.info("🗑️ Deleting FCM token: {} for user: {}", tokenId, phoneNumber);

        FcmDeviceToken token = fcmDeviceTokenRepository.findById(tokenId)
            .orElseThrow(() -> {
                log.error("❌ Token not found: {}", tokenId);
                return new AppException(ErrorCode.USER_NOT_EXISTED);
            });

        // Kiểm tra ownership - so sánh phoneNumber
        if (!token.getUser().getPhoneNumber().equals(phoneNumber)) {
            log.error("❌ Token ownership mismatch. Token user: {}, Request user: {}", 
                token.getUser().getPhoneNumber(), phoneNumber);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        fcmDeviceTokenRepository.deleteById(tokenId);
        log.info("✅ FCM token deleted: {}", tokenId);
    }

    /**
     * Xóa tất cả tokens của user (logout)
     * Note: phoneNumber từ JWT token
     */
    public void deleteAllTokensForUser(String phoneNumber) {
        log.info("🗑️ Deleting all FCM tokens for user: {}", phoneNumber);

        // Tìm user bằng phoneNumber
        User user = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> {
                log.error("❌ User not found with phoneNumber: {}", phoneNumber);
                return new AppException(ErrorCode.USER_NOT_EXISTED);
            });

        int count = fcmDeviceTokenRepository.findByUser_Id(user.getId()).size();
        fcmDeviceTokenRepository.deleteByUser_Id(user.getId());
        
        log.info("✅ Deleted {} FCM tokens for user: {}", count, phoneNumber);
    }

    /**
     * Lấy tất cả tokens của user
     */
    public List<FcmDeviceToken> getTokensForUser(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return fcmDeviceTokenRepository.findByUser_Id(user.getId());
    }
    
    /**
     * Lấy danh sách token strings (active) của user theo userId (UUID)
     * Dùng cho NotificationService
     */
    public List<String> getActiveTokensForUser(String userId) {
        List<FcmDeviceToken> tokens = fcmDeviceTokenRepository.findByUser_Id(userId);
        return tokens.stream()
                .map(FcmDeviceToken::getToken)
                .toList();
    }
    
    /**
     * Xóa token theo giá trị token string
     * Dùng khi token không hợp lệ hoặc gửi FCM thất bại
     */
    public void deleteTokenByValue(String tokenValue) {
        fcmDeviceTokenRepository.findByToken(tokenValue)
            .ifPresent(fcmDeviceTokenRepository::delete);
        log.info("🗑️ Deleted FCM token by value");
    }
}
