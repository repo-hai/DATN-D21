package com.DATN.Bej.dto.request.fcm;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterFcmTokenRequest {
    String token;
    String deviceName;
    String osType; // iOS, Android
}
