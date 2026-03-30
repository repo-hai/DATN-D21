package com.DATN.Bej.dto.request.identityRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @NotBlank(message = "phone number is required")
    String phoneNumber;
    String password;
//    String role = "USER";
    
    // Thêm 2 trường mới
    @NotBlank(message = "Full name is required")
    String fullName;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    String email;
}
