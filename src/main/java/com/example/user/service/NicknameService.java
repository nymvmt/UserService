package com.example.user.service;

import com.example.user.client.AzureOpenAIClient;
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
    
    private final AzureOpenAIClient azureOpenAIClient;
    private final UserRepository userRepository;
    
    /**
     * 사용자 정보를 기반으로 닉네임을 생성합니다.
     * 
     * @param request 닉네임 생성 요청
     * @return 생성된 닉네임과 사용 가능 여부
     */
    public NicknameGenerateResponse generateNickname(NicknameGenerateRequest request) {
        try {
            // 최대 3번 시도하여 중복되지 않는 닉네임 생성
            for (int attempt = 1; attempt <= 3; attempt++) {
                String prompt = buildPrompt(request, attempt);
                log.info("닉네임 생성 시도 {}: {}", attempt, prompt);
                
                String generatedNickname = azureOpenAIClient.generateNickname(prompt);
                log.info("생성된 닉네임: {}", generatedNickname);
                
                // 닉네임 유효성 검증 및 중복 확인
                if (isValidNickname(generatedNickname) && !userRepository.existsByNickname(generatedNickname)) {
                    return new NicknameGenerateResponse(generatedNickname, true);
                }
                
                log.warn("닉네임 '{}' 사용 불가 (중복 또는 유효하지 않음), 재시도 중...", generatedNickname);
            }
            
            // 3번 시도 후에도 실패한 경우, 마지막 생성된 닉네임을 반환하되 사용 불가로 표시
            String lastAttempt = azureOpenAIClient.generateNickname(buildPrompt(request, 4));
            return new NicknameGenerateResponse(lastAttempt, false);
            
        } catch (Exception e) {
            log.error("닉네임 생성 중 오류 발생", e);
            throw new RuntimeException("닉네임 생성 서비스에 일시적인 문제가 발생했습니다", e);
        }
    }
    
    
    /**
     * LLM에게 전달할 프롬프트를 구성합니다.
     */
    private String buildPrompt(NicknameGenerateRequest request, int attempt) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("다음 정보를 바탕으로 '동사/형용사 + 명사' 형식의 창의적이고 독특한 닉네임을 하나만 생성해주세요:\n");
        prompt.append("- 사용자명: ").append(request.getUsername()).append("\n");
        
        if (request.getInterests() != null && !request.getInterests().trim().isEmpty()) {
            prompt.append("- 관심사: ").append(request.getInterests()).append("\n");
            prompt.append("- 위 관심사를 '동사/형용사' 부분에 자연스럽게 활용해주세요\n");
        } else {
            prompt.append("- '동사/형용사' 부분은 창의적인 형용사나 동사를 사용해주세요\n");
        }
        
        if (attempt > 1) {
            prompt.append("- 이전 시도에서 중복된 닉네임이 나왔으니, 더 독창적인 닉네임을 만들어주세요.\n");
        }
        
        prompt.append("\n형식 예시:\n");
        prompt.append("- '귀여운 너구리', '영리한 토끼', '주식사랑하는 머그컵', '연주하는 배추', '신묘한 얼룩말'\n");
        
        prompt.append("\n조건:\n");
        prompt.append("- 2-10자 사이의 길이\n");
        prompt.append("- 한글만 조합 가능\n");
        prompt.append("- 특수문자는 '_' 만 사용 가능\n");
        prompt.append("- 욕설이나 부적절한 내용 금지\n");
        prompt.append("- 닉네임만 답변하고 다른 설명은 하지 마세요\n");
        
        return prompt.toString();
    }
    
    
    /**
     * 닉네임의 유효성을 검증합니다.
     */
    private boolean isValidNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return false;
        }
        
        nickname = nickname.trim();
        
        // 길이 검증 (2-20자)
        if (nickname.length() < 2 || nickname.length() > 20) {
            return false;
        }
        
        // 허용된 문자만 사용하는지 검증 (한글, 영어, 숫자, 언더스코어)
        if (!nickname.matches("^[가-힣a-zA-Z0-9_]+$")) {
            return false;
        }
        
        // 부적절한 단어 필터링 (기본적인 필터)
        String[] inappropriateWords = {"admin", "test", "null", "undefined", "바보", "멍청", "짜증", "화남", "싫어"};
        String lowerNickname = nickname.toLowerCase();
        for (String word : inappropriateWords) {
            if (lowerNickname.contains(word)) {
                return false;
            }
        }
        
        return true;
    }
}
