package com.example.gympt.domain.review.controller;

import com.example.gympt.domain.review.dto.ReviewRequestDTO;
import com.example.gympt.domain.review.dto.ReviewResponseDTO;
import com.example.gympt.domain.review.entity.Review;
import com.example.gympt.domain.review.service.ReviewService;
import com.example.gympt.security.MemberAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    //TODO : 예약일로 부터 1일 이 지난뒤 리뷰를 작성할 수 있게 조건 걸어주기

@PostMapping
public ResponseEntity<String> createReview(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO, ReviewRequestDTO review) {
    log.info("Create for member {}", memberAuthDTO.getEmail());
    log.info("Review request: {}", review);
    reviewService.createReview(memberAuthDTO.getEmail(), review);
    return ResponseEntity.ok("리뷰 작성 완료!");
}
@GetMapping
public ResponseEntity<List<ReviewResponseDTO>> getMyReviews(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO ) {
    return ResponseEntity.ok(reviewService.getMyReviewList(memberAuthDTO.getEmail()));
}

@GetMapping("/list/{gymId}")
    public ResponseEntity <List<ReviewResponseDTO>> getGymReview(@PathVariable Long gymId) {
    return ResponseEntity.ok(reviewService.getGymReviews(gymId));
}

@DeleteMapping("/{reviewId}")
    public ResponseEntity<Long> deleteMyReview(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO , Long reviewId) {
return ResponseEntity.ok(reviewService.deleteReview(memberAuthDTO.getEmail(),reviewId));
}
}
