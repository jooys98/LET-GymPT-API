package com.example.gympt.domain.review.controller;

import com.example.gympt.domain.review.dto.ReviewRequestDTO;
import com.example.gympt.domain.review.dto.ReviewResponseDTO;
import com.example.gympt.domain.review.entity.Review;
import com.example.gympt.domain.review.service.ReviewService;
import com.example.gympt.security.MemberAuthDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Review API", description = "리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "새로운 리뷰를 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 작성 성공",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<String> createReview(
            @Parameter(description = "인증된 회원 정보", required = true)
            @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "작성할 리뷰 정보", required = true,
                    content = @Content(schema = @Schema(implementation = ReviewRequestDTO.class)))
            @RequestBody ReviewRequestDTO review) {
        log.info("Create for member {}", memberAuthDTO.getEmail());
        log.info("Review request: {}", review);
        reviewService.createReview(memberAuthDTO.getEmail(), review);
        return ResponseEntity.ok("리뷰 작성 완료!");
    }

    @Operation(summary = "내 리뷰 목록 조회", description = "로그인한 사용자가 작성한 리뷰 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewResponseDTO.class)))),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getMyReviews(
            @Parameter(description = "인증된 회원 정보", required = true)
            @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO) {
        return ResponseEntity.ok(reviewService.getMyReviewList(memberAuthDTO.getEmail()));
    }

    @Operation(summary = "체육관별 리뷰 목록 조회", description = "특정 체육관에 대한 리뷰 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "체육관을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/list/{gymId}")
    public ResponseEntity<List<ReviewResponseDTO>> getGymReview(
            @Parameter(description = "체육관 ID", required = true)
            @PathVariable Long gymId) {
        return ResponseEntity.ok(reviewService.getGymReviews(gymId));
    }

    @Operation(summary = "트레이너별 리뷰 목록 조회", description = "특정 트레이너에 대한 리뷰 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "트레이너를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/list/trainers/{trainerId}")
    public ResponseEntity<List<ReviewResponseDTO>> getTrainerReview(
            @Parameter(description = "트레이너 ID", required = true)
            @PathVariable Long trainerId) {
        return ResponseEntity.ok(reviewService.getTrainerReviews(trainerId));
    }

    @Operation(summary = "리뷰 삭제", description = "작성한 리뷰를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공",
                    content = @Content(schema = @Schema(type = "integer", format = "int64"))),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Long> deleteMyReview(
            @Parameter(description = "인증된 회원 정보", required = true)
            @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO,
            @Parameter(description = "삭제할 리뷰 ID", required = true)
            @PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.deleteReview(memberAuthDTO.getEmail(), reviewId));
    }
}