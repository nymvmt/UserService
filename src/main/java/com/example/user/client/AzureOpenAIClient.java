package com.example.user.client;

import com.example.user.config.AzureOpenAIConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AzureOpenAIClient {
    
    private final AzureOpenAIConfig azureConfig;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    
    /**
     * Azure OpenAI API를 사용하여 닉네임을 생성합니다.
     * 
     * @param prompt 닉네임 생성을 위한 프롬프트
     * @return 생성된 닉네임
     */
    public String generateNickname(String prompt) {
        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(azureConfig.getEndpoint())
                    .defaultHeader("api-key", azureConfig.getApiKey())
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
            
            Map<String, Object> requestBody = createRequestBody(prompt);
            
            String uri = String.format("https://kt-pj2-nicknamegenerator-openai.services.ai.azure.com/api/projects/AIFoundryProject/openai/deployments/%s/chat/completions?api-version=%s",
                    azureConfig.getDeploymentName(), azureConfig.getApiVersion());
            
            // String uri = String.format("/openai/deployments/%s/chat/completions?api-version=%s",
            //         azureConfig.getDeploymentName(), azureConfig.getApiVersion());
            
            log.info("Azure OpenAI API 호출 - URI: {}, Deployment: {}", uri, azureConfig.getDeploymentName());
            
            String response = webClient
                    .post()
                    .uri(uri)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(azureConfig.getTimeout()))
                    .block();
            
            return extractNicknameFromResponse(response);
            
        } catch (Exception e) {
            log.error("Azure OpenAI API 호출 중 오류 발생", e);
            throw new RuntimeException("닉네임 생성 중 오류가 발생했습니다", e);
        }
    }
    
    /**
     * Azure OpenAI API 요청 본문을 생성합니다.
     */
    private Map<String, Object> createRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("max_completion_tokens", 200);
        requestBody.put("temperature", 0.8);
        requestBody.put("top_p", 1.0);
        requestBody.put("frequency_penalty", 0);
        requestBody.put("presence_penalty", 0);
        
        // System message + User message 구조
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "당신은 한국어 닉네임 생성 전문가입니다. 사용자의 정보를 바탕으로 '형용사/동사 + 명사' 형식의 귀여운 닉네임을 정확히 생성해주세요. 반드시 지정된 형식으로만 답변하세요.");
        
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        
        requestBody.put("messages", List.of(systemMessage, userMessage));
        
        return requestBody;
    }
    
    /**
     * Azure OpenAI API 응답에서 닉네임을 추출합니다.
     */
    private String extractNicknameFromResponse(String response) {
        try {
            log.debug("Azure OpenAI API 응답: {}", response);
            
            JsonNode jsonNode = objectMapper.readTree(response);
            String content = jsonNode
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
            
            log.info("생성된 응답 내용: {}", content);
            
            return content;
            
        } catch (Exception e) {
            log.error("Azure OpenAI 응답 파싱 중 오류 발생: {}", response, e);
            throw new RuntimeException("닉네임 추출 중 오류가 발생했습니다", e);
        }
    }
}