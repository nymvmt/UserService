package com.example.user.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    public PasswordUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    /**
     * 평문 비밀번호를 해시화
     * @param rawPassword 평문 비밀번호
     * @return 해시된 비밀번호
     */
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    /**
     * 평문 비밀번호와 해시된 비밀번호가 일치하는지 확인
     * @param rawPassword 평문 비밀번호
     * @param encodedPassword 해시된 비밀번호
     * @return 일치 여부
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
