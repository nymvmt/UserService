package com.example.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    
    @NotBlank(message = "사용자 ID는 필수입니다")
    @Email(message = "올바른 이메일 형식이어야 합니다")
    @Pattern(regexp = ".*@kt\\.com$", message = "KT 사내 이메일 형식이어야 합니다")
    private String userId;
    
    @NotBlank(message = "사용자명은 필수입니다")
    @Size(min = 1, max = 50, message = "사용자명은 1자 이상 50자 이하여야 합니다")
    private String username;
    
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 1, max = 50, message = "닉네임은 1자 이상 50자 이하여야 합니다")
    private String nickname;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    private String password;
}
