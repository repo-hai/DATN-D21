package com.DATN.Bej.mapper;

import com.DATN.Bej.dto.response.fcm.FcmTokenResponse;
import com.DATN.Bej.entity.FcmDeviceToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FcmDeviceTokenMapper {
    FcmTokenResponse toResponse(FcmDeviceToken fcmDeviceToken);
    FcmDeviceToken toEntity(FcmTokenResponse response);
}
