package com.example.user.controller;

import com.example.user.dto.ApiResponse;
import com.example.user.dto.NicknameGenerateRequest;
import com.example.user.dto.NicknameGenerateResponse;
import com.example.user.dto.UserCreateRequest;
import com.example.user.dto.UserLoginRequest;
import com.example.user.dto.UserResponse;
import com.example.user.service.NicknameService;
import com.example.user.service.UserService;

import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    private final NicknameService nicknameService;
    
    /**
     * 모든 사용자 조회
     * GET /users
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        try {
            List<UserResponse> users = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success("모든 사용자 조회 성공", users));
        } catch (Exception e) {
            log.error("모든 사용자 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("서버 오류가 발생했습니다"));
        }
    }
    
    /**
     * 사용자 등록
     * POST /users
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        try {
            UserResponse userResponse = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("사용자가 성공적으로 등록되었습니다", userResponse));
        } catch (IllegalArgumentException e) {
            log.warn("사용자 등록 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("사용자 등록 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("서버 오류가 발생했습니다"));
        }
    }
    
    /**
     * 사용자 조회
     * GET /users/{user_id}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String userId) {
        try {
            UserResponse userResponse = userService.getUser(userId);
            return ResponseEntity.ok(ApiResponse.success(userResponse));
        } catch (IllegalArgumentException e) {
            log.warn("사용자 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("사용자 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("서버 오류가 발생했습니다"));
        }
    }
    
    /**
     * 사용자 삭제
     * DELETE /users/{user_id}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(ApiResponse.success("사용자가 성공적으로 삭제되었습니다", null));
        } catch (IllegalArgumentException e) {
            log.warn("사용자 삭제 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("사용자 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("서버 오류가 발생했습니다"));
        }
    }
    
    /**
     * 로그인
     * POST /users/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody UserLoginRequest request) {
        try {
            UserResponse userResponse = userService.login(request);
            return ResponseEntity.ok(ApiResponse.success("로그인이 성공했습니다", userResponse));
        } catch (IllegalArgumentException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("로그인 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("서버 오류가 발생했습니다"));
        }
    }
    
    /**
     * 로그아웃
     * POST /users/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestParam String userId) {
        try {
            userService.logout(userId);
            return ResponseEntity.ok(ApiResponse.success("로그아웃이 성공했습니다", null));
        } catch (Exception e) {
            log.error("로그아웃 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("서버 오류가 발생했습니다"));
        }
    }
    
    /**
     * 닉네임 생성 (단일 닉네임 재시도 방식)
     * POST /users/nickname/generate
     */
    @PostMapping("/nickname/generate")
    public ResponseEntity<ApiResponse<NicknameGenerateResponse>> generateNickname(
            @Valid @RequestBody NicknameGenerateRequest request) {
        try {
            NicknameGenerateResponse response = nicknameService.generateNickname(request);
            return ResponseEntity.ok(ApiResponse.success("닉네임이 생성되었습니다", response));
        } catch (Exception e) {
            log.error("닉네임 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("닉네임 생성 중 오류가 발생했습니다"));
        }
    }
    
}
