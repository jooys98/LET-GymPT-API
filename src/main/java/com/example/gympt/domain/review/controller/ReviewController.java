package com.example.gympt.domain.review.controller;

import com.example.gympt.domain.review.dto.ReviewRequestDTO;
import com.example.gympt.domain.review.dto.ReviewResponseDTO;
import com.example.gympt.domain.review.entity.Review;
import com.example.gympt.domain.review.service.ReviewService;
import com.example.gympt.security.MemberAuthDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;


@PostMapping
public ResponseEntity<String> createReview(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO, ReviewRequestDTO review) {
    reviewService.createReview(memberAuthDTO.getUsername(), review);
    return ResponseEntity.ok("리뷰 작성 완료!");
}
@GetMapping
public ResponseEntity<List<ReviewResponseDTO>> getMyReviews(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO ) {
    return ResponseEntity.ok(reviewService.getMyReviewList(memberAuthDTO.getUsername()));
}

@GetMapping("/list/{gymId}")
    public ResponseEntity <List<ReviewResponseDTO>> getGymReview(@PathVariable Long gymId) {
    return ResponseEntity.ok(reviewService.getGymReviews(gymId));
}

@DeleteMapping("/{reviewId}")
    public ResponseEntity<Long> deleteMyReview(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO , Long reviewId) {
return ResponseEntity.ok(reviewService.deleteReview(memberAuthDTO.getUsername(),reviewId));
}
}
