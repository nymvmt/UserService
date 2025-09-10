package com.example.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NicknameGenerateRequest {
    
    @NotBlank(message = "사용자명은 필수입니다")
    @Size(max = 50, message = "사용자명은 50자를 초과할 수 없습니다")
    private String username;
    
    @Size(max = 200, message = "관심사는 200자를 초과할 수 없습니다")
    private String interests; // 관심사 (예: "게임, 스포츠, 음악")
}
