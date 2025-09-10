package com.example.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm.azure-openai")
public class AzureOpenAIConfig {
    private String apiKey;
    private String endpoint;
    private String deploymentName;
    private String apiVersion;
    private int timeout;
}
