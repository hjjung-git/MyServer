package com.example.my_server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class ClaudeClient {

    private static final Logger log = LoggerFactory.getLogger(ClaudeClient.class);

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.groq.com/openai/v1")
            .defaultHeader("Content-Type", "application/json")
            .build();

    @Value("${groq.api.key:}")
    private String apiKey;

    public String summarizeNews(String title, String rawSummary) {
        if (apiKey == null || apiKey.isBlank()) return null;

        String prompt = """
                다음 영문 암호화폐 뉴스를 한국어로 번역·요약해주세요.
                핵심 내용을 3~5줄로 간결하게 정리해주세요. 두괄식으로 가장 중요한 내용을 먼저 서술해주세요.
                마크다운 없이 평문으로 작성해주세요.

                제목: %s

                내용: %s
                """.formatted(title, rawSummary != null ? rawSummary : "");

        try {
            Map<String, Object> request = Map.of(
                "model", "llama-3.1-8b-instant",
                "max_tokens", 512,
                "messages", List.of(Map.of("role", "user", "content", prompt))
            );

            Map<String, Object> response = restClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (response != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    if (message != null) {
                        return (String) message.get("content");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Groq API 호출 실패: {}", e.getMessage());
        }
        return null;
    }
}
