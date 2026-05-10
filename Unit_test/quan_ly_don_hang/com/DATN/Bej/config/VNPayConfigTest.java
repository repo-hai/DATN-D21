package com.DATN.Bej.config;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class VNPayConfigTest {

    @Mock
    private HttpServletRequest request;

    @Test
    void hmacSHA512_validInput_expectedKnownHashReturned() {
        // Test Case ID theo report: UTC-PAY-CFG-001

        // Arrange: use deterministic key/data to verify a known hash value.
        String key = "secret";
        String data = "payload";

        // Act: generate HMAC SHA512.
        String hash = VNPayConfig.hmacSHA512(key, data);

        // Assert: hash must exactly match expected canonical value.
        assertThat(hash)
                .isEqualTo("291ddaaa23cafa3aaae1c9755391f4bef35bbdbcb92739a5618a5c896f6520d2b0d28d2d2987dac97479e31214a51d96cfceafa28e46a4f961b63c46352a189e");
    }

    @Test
    void getRandomNumber_lengthEight_expectedEightDigitsOnly() {
        // Test Case ID theo report: UTC-PAY-CFG-002

        // Arrange: request random numeric text with length 8.
        int length = 8;

        // Act: create random numeric text.
        String randomNumber = VNPayConfig.getRandomNumber(length);

        // Assert: only digits and exact required length.
        assertThat(randomNumber).hasSize(8).matches("\\d{8}");
    }

    @Test
    void getIpAddress_forwardedHeaderPresent_expectedForwardedIpReturned() {
        // Test Case ID theo report: UTC-PAY-CFG-003

        // Arrange: X-FORWARDED-FOR exists, typically set by reverse proxy.
        when(request.getHeader("X-FORWARDED-FOR")).thenReturn("203.113.10.25");

        // Act: resolve IP from request.
        String ipAddress = VNPayConfig.getIpAddress(request);

        // Assert: forwarded IP should be preferred over remote address.
        assertThat(ipAddress).isEqualTo("203.113.10.25");
    }
}


