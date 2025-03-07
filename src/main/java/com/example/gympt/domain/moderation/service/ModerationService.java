package com.example.gympt.domain.moderation.service;

import com.example.gympt.domain.review.dto.ReviewResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Service
public class ModerationService {

    private final WebClient webClient;

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    public ModerationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/moderations").build();
    }

    //Mono : 지금 당장 값이 없지만, 나중에 (비동기적으로) 하나의 결과가 도착할 것이라는 약속
//Mono<Map<String, Object>> : ai 모더레이션 api 의 결과를 담을 상자 같은것

    public Mono<Map<String, Object>> moderateReview(List<String> reviewText) {
        return webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(Map.of("input", reviewText))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                });
    }
}
