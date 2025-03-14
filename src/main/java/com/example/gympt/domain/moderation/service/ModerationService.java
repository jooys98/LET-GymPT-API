package com.example.gympt.domain.moderation.service;

import com.example.gympt.domain.review.dto.ReviewResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;


@Slf4j
@Service
public class ModerationService {

    private final WebClient webClient;

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    public ModerationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/chat/completions").build();
    }

    //Mono : 지금 당장 값이 없지만, 나중에 (비동기적으로) 하나의 결과가 도착할 것이라는 약속
    //Mono<Map<String, Object>> : ai 모더레이션 api 의 결과를 담을 상자 같은것

    // ex ) 배민으로 신전떡볶이를 주문한뒤 음식을 기다리면서 열심히 개발을 한다
    // 음식이 도착하면 초인종이 울린다 , 음식을 접시에 담고 야무지게 음식을 먹는다
    // 내가 배달을 시킨 행위 : 비동기적 작업 , 주문한 신전떡볶이 : ai 가 내릴 결과 , 기다리면서 하는 개발 : 스레드가 멈추지 않는다
    // 조리가 다 돼어 우리집에 도착한 떡볶이 : 비동기적 반환 , 초인종 : ai 의 리뷰 작업이 끝날을 시 푸쉬 알림 (fcm)
    // 떡볶이를 담을 접시  : Map<String, Object>

    public Mono<Map<String, Object>> moderateReview(List<String> reviewTexts) {
        Mono<Map<String, Object>> openAi = webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(Map.of(
                        "model", "gpt-3.5-turbo",
                        "messages", List.of(
                                Map.of(
                                        "role", "system",
                                        "content", "당신은 리뷰 콘텐츠를 검토하는 모더레이터입니다. 특히 한국어 욕설, 비속어, 성적 표현, 혐오 발언 등을 감지하는 데 주의해야 합니다. 검토 결과를 다음과 같은 JSON 형식으로 반환하세요: {\"flagged\": boolean, \"categories\": {\"hate\": boolean, \"harassment\": boolean, \"sexual\": boolean, \"violence\": boolean, \"spam\": boolean}, \"reason\": \"부적절하다고 판단한 이유\"}"
                                ),
                                Map.of(
                                        "role", "user",
                                        "content", String.join("\n---\n", reviewTexts)
                                )
                        ),
                        "response_format", Map.of("type", "json_object")
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    // 응답 처리 로직
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                    String content = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");

                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> result = mapper.readValue(content, Map.class);

                        // 결과를 통일된 형식으로 변환
                        return Map.of("results", List.of(result));
                    } catch (Exception e) {
                        log.error("JSON 파싱 오류: {}", e.getMessage());
                        return Map.of("results", List.of(Map.of("flagged", false)));
                    }
                });
        return openAi;
    }
}