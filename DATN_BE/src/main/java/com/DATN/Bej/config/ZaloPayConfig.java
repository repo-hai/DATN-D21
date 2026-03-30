package com.DATN.Bej.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "zalopay")
@Data
public class ZaloPayConfig {
    private String appId;
    private String key1;
    private String key2;
    private String createOrderUrl;
    private String callbackUrl;
    private String returnUrl;
}