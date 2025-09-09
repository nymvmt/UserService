package com.example.user.dto;

import com.example.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private String userId;
    private Boolean isAdmin;
    private String username;
    private String nickname;
    
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .isAdmin(user.getIsAdmin())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }
}
