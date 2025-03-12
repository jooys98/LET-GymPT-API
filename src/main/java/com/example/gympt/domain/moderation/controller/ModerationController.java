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
//Mono (비동기 작업) 의 작동 방식
// 1. 사용자가 리뷰를 제출하면 백엔드는 AI 서비스에 분석 요청을 보냄
// 2. 사용자는 Ai가 분석을 완료할때까지 다른 작업을 진행 할 수 있음 (스레드가 멈추지 않고 계속 활성화)
// 3. AI의  판단이 왼료되어 준비되었다면 그때 클라이언트에게 response 를 전달(비동기적 반환)


public class ModerationController {

    private final ModerationService moderationService;

    @PostMapping
    public Mono<Map<String, Object>> checkReview(@RequestBody List<ReviewResponseDTO> reviewResponseDTO) {
        List<String> reviewTexts = reviewResponseDTO.stream()
                .map(ReviewResponseDTO::getContent) //실제 리뷰 내용을 담고 있는 필드
                .toList();
        return moderationService.moderateReview(reviewTexts);
    }
}
