package com.example.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @Column(name = "user_id", length = 100)
    private String userId;
    
    @Column(name = "is_admin", nullable = false)
    @Builder.Default
    private Boolean isAdmin = false;
    
    @Column(name = "username", length = 50, nullable = false)
    private String username;
    
    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;
    
    @Column(name = "password", length = 255, nullable = false)
    private String password;
}
