package com.DATN.Bej.repository;

import com.DATN.Bej.entity.FcmDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmDeviceTokenRepository extends JpaRepository<FcmDeviceToken, String> {
    Optional<FcmDeviceToken> findByToken(String token);
    List<FcmDeviceToken> findByUser_Id(String userId);
    void deleteByUser_Id(String userId);
}
