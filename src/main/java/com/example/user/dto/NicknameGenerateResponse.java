package com.example.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NicknameGenerateResponse {
    private String nickname;
    private boolean isAvailable; // 사용 가능 여부
}
