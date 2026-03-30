package com.DATN.Bej.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http.csrf(AbstractHttpConfigurer::disable)
//             .authorizeHttpRequests(auth -> auth
//                 .anyRequest().permitAll()
//             );
//         return http.build();
//     }

    /**
     * SecurityFilterChain cho các endpoint PUBLIC (không cần OAuth2)
     * Đặt order = 1 để chạy TRƯỚC filterChain chính
     */
    @Bean
    @Order(1)
    public SecurityFilterChain publicFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/upload/**", "/images/**", "/home/**", "/banners/**", 
                                "/ws/**", "/payment/**", "/auth/**", "/users/create",
                                "/api/device-token/test-send-notification", "/")
                .authorizeHttpRequests(request -> request.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());
        
        // KHÔNG cấu hình OAuth2 cho public endpoints
        return httpSecurity.build();
    }

    /**
     * SecurityFilterChain chính cho các endpoint cần authentication
     * Đặt order = 2 để chạy SAU publicFilterChain
     */
    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(request ->
                                // Admin endpoints - yêu cầu ROLE_ADMIN
                                request.requestMatchers("/manage/product/**", "/manage/category/**", "/manage/orders/**").hasRole("ADMIN")
                                        .requestMatchers("/manage/users/**", "/manage/schedule/**").hasRole("ADMIN")
                                        // Order endpoints yêu cầu authentication
                                        .requestMatchers("/orders/**").authenticated()
                                        // Các endpoint khác yêu cầu authentication
                                        .anyRequest().authenticated()
                );

        // OAuth2 Resource Server - chỉ áp dụng cho các endpoint cần authentication
        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer ->
                                jwtConfigurer.decoder(customJwtDecoder)
                                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        );
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }



    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}
