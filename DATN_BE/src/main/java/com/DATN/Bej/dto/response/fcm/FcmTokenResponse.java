package com.DATN.Bej.dto.response.fcm;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FcmTokenResponse {
    String id;
    String token;
    String deviceName;
    String osType;
    Instant registeredAt;
}
