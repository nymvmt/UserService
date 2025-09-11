package com.example.user.service;

import com.example.user.client.PythonNicknameServiceClient;
import com.example.user.dto.NicknameGenerateRequest;
import com.example.user.dto.NicknameGenerateResponse;
import com.example.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class NicknameService {
    
    private final PythonNicknameServiceClient pythonNicknameServiceClient;
    private final UserRepository userRepository;
    
    /**
     * 사용자 정보를 기반으로 닉네임을 생성합니다.
     * Python 닉네임 서비스를 호출하여 처리합니다.
     * 
     * @param request 닉네임 생성 요청
     * @return 생성된 닉네임과 사용 가능 여부
     */
    public NicknameGenerateResponse generateNickname(NicknameGenerateRequest request) {
        try {
            log.info("Python 닉네임 서비스 호출 - username: {}, interests: {}", 
                    request.getUsername(), request.getInterests());
            
            // Python 서비스 호출 (중복 체크 및 재시도 로직은 Python에서 처리)
            NicknameGenerateResponse response = pythonNicknameServiceClient.generateNickname(request);
            
            log.info("Python 서비스 응답 - nickname: {}, isAvailable: {}", 
                    response.getNickname(), response.isAvailable());
            
            return response;
            
        } catch (Exception e) {
            log.error("닉네임 생성 중 오류 발생", e);
            throw new RuntimeException("닉네임 생성 서비스에 일시적인 문제가 발생했습니다", e);
        }
    }
}
