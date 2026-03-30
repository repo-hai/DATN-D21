package com.DATN.Bej.service.identity;

import com.DATN.Bej.dto.request.identityRequest.AuthenticationRequest;
import com.DATN.Bej.dto.request.identityRequest.IntrospectRequest;
import com.DATN.Bej.dto.response.identity.AuthenticationResponse;
import com.DATN.Bej.dto.response.identity.IntrospectResponse;
import com.DATN.Bej.entity.identity.InvalidatedToken;
import com.DATN.Bej.entity.identity.User;
import com.DATN.Bej.exception.AppException;
import com.DATN.Bej.exception.ErrorCode;
import com.DATN.Bej.repository.FcmDeviceTokenRepository;
import com.DATN.Bej.repository.InvalidatedTokenRepositoy;
import com.DATN.Bej.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepositoy invalidatedTokenRepositoy;
    FcmDeviceTokenRepository fcmDeviceTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyTokenForLogout(token);
        } catch (AppException e){
            isValid = false;
        }

        return  IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated)
            throw  new AppException(ErrorCode.UNAUTHENTICATED);

        // 🗑️ Xóa tất cả device tokens cũ của user khi đăng nhập
        // Để đảm bảo mỗi lần đăng nhập chỉ có device hiện tại được liên kết
        try {
            var existingTokens = fcmDeviceTokenRepository.findByUser_Id(user.getId());
            if (!existingTokens.isEmpty()) {
                log.info("🗑️ Deleting {} old device tokens for user: {}", existingTokens.size(), user.getPhoneNumber());
                fcmDeviceTokenRepository.deleteByUser_Id(user.getId());
                log.info("✅ Successfully deleted old device tokens for user: {}", user.getPhoneNumber());
            }
        } catch (Exception e) {
            log.warn("⚠️ Could not delete old device tokens during login: {}", e.getMessage());
            // Không throw exception, login vẫn thành công ngay cả khi xóa device token thất bại
        }

        // Tạo token mới
        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    /**
     * Logout user và thêm token vào blacklist
     * @return phoneNumber của user đã logout (để xóa FCM tokens)
     */
    public String logout(IntrospectRequest request) throws ParseException, JOSEException {

        var signToken = verifyTokenForLogout(request.getToken());

        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
        String phoneNumber = signToken.getJWTClaimsSet().getSubject(); // Lấy phoneNumber từ JWT subject

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepositoy.save(invalidatedToken);
        log.info("✅ Token invalidated (blacklisted) for user: {}", phoneNumber);
        
        return phoneNumber;
    }

    /**
     * Verify token cho Spring Security (JWT validation)
     * - Check signature
     * - Check expiry time
     * - KHÔNG check blacklist (vì token mới từ login không nằm trong blacklist)
     */
    public SignedJWT verifyToken(String token) throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);

        if(!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    /**
     * Verify token cho Logout/Introspect
     * - Check signature
     * - Check expiry time
     * - Check blacklist (token đã logout trước đó?)
     */
    public SignedJWT verifyTokenForLogout(String token) throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);

        if(!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        // Check blacklist - để không logout token đã logout trước đó
        if (invalidatedTokenRepositoy.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    private String generateToken(User user){
        JWSHeader header =  new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getPhoneNumber())
                .issuer("aduvjp.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create Token!", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");

        if(!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if(!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });
        }

        return stringJoiner.toString();
    }
}
