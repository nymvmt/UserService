package com.example.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {
    
    @NotBlank(message = "사용자 ID는 필수입니다")
    private String userId;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}
