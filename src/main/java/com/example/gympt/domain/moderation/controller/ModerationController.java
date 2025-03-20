package com.example.gympt.domain.moderation.controller;

import com.example.gympt.domain.moderation.service.ModerationService;
import com.example.gympt.domain.review.dto.ReviewResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Review Content Moderation API", description = "리뷰 내용 검수 및 모더레이션 관련 API")
//Mono (비동기 작업) 의 작동 방식
// 1. 사용자가 리뷰를 제출하면 백엔드는 AI 서비스에 분석 요청을 보냄
// 2. 사용자는 Ai가 분석을 완료할때까지 다른 작업을 진행 할 수 있음 (스레드가 멈추지 않고 계속 활성화)
// 3. AI의  판단이 왼료되어 준비되었다면 그때 클라이언트에게 response 를 전달(비동기적 반환)


public class ModerationController {

    private final ModerationService moderationService;
    @Operation(summary = "리뷰 내용 검수", description = "AI를 통해 리뷰 텍스트를 분석하여 부적절한 내용이 있는지 검수합니다. (비동기 처리)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검수 완료",
                    content = @Content(schema = @Schema(type = "object",
                            description = "검수 결과를 포함한 객체"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public Mono<Map<String, Object>> checkReview( @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "검수할 리뷰 목록", required = true,
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewResponseDTO.class))))
                                                      @RequestBody List<ReviewResponseDTO> reviewResponseDTO) {
        List<String> reviewTexts = reviewResponseDTO.stream()
                .map(ReviewResponseDTO::getContent) //실제 리뷰 내용을 담고 있는 필드
                .toList();
        return moderationService.moderateReview(reviewTexts);
    }
}
