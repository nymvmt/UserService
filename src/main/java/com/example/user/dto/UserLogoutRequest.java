package com.example.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLogoutRequest {
    
    @NotBlank(message = "사용자 ID는 필수입니다")
    private String userId;
}
