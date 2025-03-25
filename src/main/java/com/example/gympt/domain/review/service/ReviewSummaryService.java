package com.example.gympt.domain.review.service;


import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.repository.GymRepository;
import com.example.gympt.domain.review.entity.Review;
import com.example.gympt.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewSummaryService {

    private final WebClient webClient;
    private final ReviewRepository reviewRepository;
    private final GymRepository gymRepository;


    @Value("${OPENAI_API_KEY}")
    private String apiKey;

//리뷰가 달릴때마다 요약 업데이트 진행 ( 3개 이상일 경우에만 실행)
    @Transactional
    public void updateReviewSummary(Review newReview) {
        Gym gym = newReview.getGym();

        // 해당 헬스장의 모든 리뷰 가져오기 ( 안좋은 리뷰도 포함)
        List<Review> reviews = reviewRepository.findByGymId(gym.getId());

        // 리뷰가 충분히 있는 경우에만 요약 생성
        if (reviews.size() >= 3) {
            String summaryResult = generateReviewSummary(reviews);
            gym.updateReviewSummary(summaryResult);
            gymRepository.save(gym);
        }
    }

    private String generateReviewSummary(List<Review> reviews) {
        // 리뷰 내용 추출
        List<String> reviewContents = reviews.stream()
                .map(Review::getContent)
                .toList();

        // GPT API 호출
        try {
            Map<String, Object> response = webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(Map.of(
                            "model", "gpt-3.5-turbo",
                            "messages", List.of(
                                    Map.of(
                                            "role", "system",
                                            "content", "당신은 체육관 리뷰를 분석하고 요약하는 전문가입니다. 다음 리뷰들을 분석하여 장점, 단점, 주요 특징을 300자 이내로 요약해주세요. 비속어가 들어간 리뷰는 분석에서 제외 합니다 ."
                                    ),
                                    Map.of(
                                            "role", "user",
                                            "content", String.join("\n\n", reviewContents)
                                    )
                            ),
                            "max_tokens", 500
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(); // 동기 호출

            if (response != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                return (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
            }
        } catch (Exception e) {
            // 예외 처리
            return "리뷰 요약을 생성하는 중 오류가 발생했습니다.";
        }

        return "충분한 리뷰가 없어 요약을 생성할 수 없습니다.";
    }
}


