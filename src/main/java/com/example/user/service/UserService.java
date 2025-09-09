package com.example.user.service;

import com.example.user.dto.UserCreateRequest;
import com.example.user.dto.UserLoginRequest;
import com.example.user.dto.UserResponse;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.example.user.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    
    /**
     * 사용자 등록
     * @param request 사용자 등록 요청
     * @return 등록된 사용자 정보
     */
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // INV-U003: userID는 시스템 전체에서 유일해야 함
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다: " + request.getUserId());
        }
        
        // INV-U003: 닉네임도 유일해야 함 (추가 검증)
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다: " + request.getNickname());
        }
        
        // INV-U005: password는 해시된 상태로만 저장되어야 함
        String hashedPassword = passwordUtil.encode(request.getPassword());
        
        User user = User.builder()
                .userId(request.getUserId())
                .isAdmin(false) // INV-U004: isAdmin은 기본값이 FALSE여야 함
                .username(request.getUsername())
                .nickname(request.getNickname())
                .password(hashedPassword)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("사용자 등록 완료: {}", savedUser.getUserId());
        
        return UserResponse.from(savedUser);
    }
    
    /**
     * 모든 사용자 조회
     * @return 모든 사용자 목록
     */
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 사용자 조회
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    public UserResponse getUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        
        return UserResponse.from(user);
    }
    
    /**
     * 사용자 삭제
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteUser(String userId) {
        if (!userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId);
        }
        
        userRepository.deleteById(userId);
        log.info("사용자 삭제 완료: {}", userId);
    }
    
    /**
     * 로그인
     * @param request 로그인 요청
     * @return 로그인 성공 시 사용자 정보
     */
    public UserResponse login(UserLoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + request.getUserId()));
        
        // 비밀번호 검증
        if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        
        log.info("사용자 로그인 성공: {}", user.getUserId());
        return UserResponse.from(user);
    }
    
    /**
     * 로그아웃 (단순 로그 기록)
     * @param userId 사용자 ID
     */
    public void logout(String userId) {
        log.info("사용자 로그아웃: {}", userId);
    }
}
