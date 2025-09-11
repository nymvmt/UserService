package com.example.user.client;

import com.example.user.dto.NicknameGenerateRequest;
import com.example.user.dto.NicknameGenerateResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PythonNicknameServiceClient {
    
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    
    @Value("${python.nickname-service.url:http://nickname-service-python:8088}")
    private String pythonServiceUrl;
    
    @Value("${python.nickname-service.timeout:30}")
    private int timeoutSeconds;
    
    /**
     * Python 닉네임 서비스를 호출하여 닉네임을 생성합니다.
     * 
     * @param request 닉네임 생성 요청
     * @return 닉네임 생성 응답
     */
    public NicknameGenerateResponse generateNickname(NicknameGenerateRequest request) {
        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(pythonServiceUrl)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
            
            Map<String, Object> requestBody = createRequestBody(request);
            
            log.info("Python 닉네임 서비스 호출 - URL: {}/generate-nickname", pythonServiceUrl);
            
            String response = webClient
                    .post()
                    .uri("/generate-nickname")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
            
            return parseResponse(response);
            
        } catch (Exception e) {
            log.error("Python 닉네임 서비스 호출 중 오류 발생", e);
            throw new RuntimeException("닉네임 생성 중 오류가 발생했습니다", e);
        }
    }
    
    /**
     * Python 서비스 요청 본문을 생성합니다.
     */
    private Map<String, Object> createRequestBody(NicknameGenerateRequest request) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", request.getUsername());
        if (request.getInterests() != null && !request.getInterests().trim().isEmpty()) {
            requestBody.put("interests", request.getInterests());
        }
        return requestBody;
    }
    
    /**
     * Python 서비스 응답을 파싱합니다.
     */
    private NicknameGenerateResponse parseResponse(String response) {
        try {
            log.debug("Python 닉네임 서비스 응답: {}", response);
            
            JsonNode jsonNode = objectMapper.readTree(response);
            String nickname = jsonNode.path("nickname").asText();
            boolean isAvailable = jsonNode.path("is_available").asBoolean();
            
            log.info("파싱된 응답 - 닉네임: {}, 사용가능: {}", nickname, isAvailable);
            
            return new NicknameGenerateResponse(nickname, isAvailable);
            
        } catch (Exception e) {
            log.error("Python 서비스 응답 파싱 중 오류 발생: {}", response, e);
            throw new RuntimeException("닉네임 응답 파싱 중 오류가 발생했습니다", e);
        }
    }
}
