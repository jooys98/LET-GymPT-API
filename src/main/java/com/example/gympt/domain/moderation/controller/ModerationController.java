package com.example.gympt.domain.moderation.controller;

import com.example.gympt.domain.moderation.service.ModerationService;
import com.example.gympt.domain.review.dto.ReviewResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/moderation")
public class ModerationController {

    private final ModerationService moderationService;

    @PostMapping
    public Mono<Map<String, Object>> checkReview(@RequestBody List<ReviewResponseDTO> reviewResponseDTO) {
        List<String> reviewTexts = reviewResponseDTO.stream()
                .map(ReviewResponseDTO::getContent) // 또는 실제 리뷰 내용을 담고 있는 필드
                .toList();
        return moderationService.moderateReview(reviewTexts);
    }
}
