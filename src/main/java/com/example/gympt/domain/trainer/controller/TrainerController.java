package com.example.gympt.domain.trainer.controller;

import com.amazonaws.Response;
import com.example.gympt.domain.booking.dto.BookingResponseDTO;
import com.example.gympt.domain.booking.service.BookingService;
import com.example.gympt.domain.member.dto.MemberRequestDTO;
import com.example.gympt.domain.member.dto.MemberResponseDTO;
import com.example.gympt.domain.reverseAuction.dto.AuctionTrainerBidResponseDTO;
import com.example.gympt.domain.reverseAuction.dto.AuctionTrainerHistoryDTO;
import com.example.gympt.domain.reverseAuction.dto.AuctionUpdatePrice;
import com.example.gympt.domain.reverseAuction.dto.TrainerAuctionRequestDTO;
import com.example.gympt.domain.reverseAuction.service.ReverseAuctionService;
import com.example.gympt.domain.review.dto.ReviewResponseDTO;
import com.example.gympt.domain.review.service.ReviewService;
import com.example.gympt.domain.trainer.dto.TrainerResponseDTO;
import com.example.gympt.domain.trainer.dto.TrainerSaveRequestDTO;
import com.example.gympt.domain.trainer.service.TrainerService;
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

@RestController
@RequestMapping("/api/trainer") // 트레이너 권한 필요
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Trainer API", description = "트레이너 관련 API (트레이너 권한 필요)")
public class TrainerController {

    private final TrainerService trainerService;
    private final ReverseAuctionService reverseAuctionService;
    private final BookingService bookingService;
    private final ReviewService reviewService;

    @Operation(summary = "역경매 입찰 신청", description = "트레이너가 역경매에 입찰 신청을 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "입찰 신청 성공",
                    content = @Content(schema = @Schema(implementation = AuctionTrainerBidResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 경매 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/auction")
    public ResponseEntity<AuctionTrainerBidResponseDTO> letAuctionTrainer(
            @Parameter(description = "인증된 트레이너 정보", required = true)
            @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "입찰 신청 정보", required = true,
                    content = @Content(schema = @Schema(implementation = TrainerAuctionRequestDTO.class)))
            @RequestBody TrainerAuctionRequestDTO trainerAuctionRequestDTO) {
        return ResponseEntity.ok(trainerService.applyAuction(memberAuthDTO.getEmail(), trainerAuctionRequestDTO));
    }

    @Operation(summary = "PT 가격 변경", description = "트레이너가 역경매에 입찰한 PT 가격을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가격 변경 성공",
                    content = @Content(schema = @Schema(implementation = AuctionTrainerBidResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 입찰 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/auction/{auctionId}")
    public ResponseEntity<AuctionTrainerBidResponseDTO> updateAuctionPrice(
            @Parameter(description = "인증된 트레이너 정보", required = true)
            @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO,
            @Parameter(description = "입찰 ID", required = true)
            @PathVariable Long auctionId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "변경할 가격 정보", required = true,
                    content = @Content(schema = @Schema(implementation = AuctionUpdatePrice.class)))
            @RequestBody AuctionUpdatePrice auctionUpdatePrice) {
        return ResponseEntity.ok(trainerService.changePrice(auctionId, memberAuthDTO.getEmail(), auctionUpdatePrice.getUpdatePrice()));
    }

    @Operation(summary = "역경매 참여 내역 조회", description = "트레이너가 참여했던 역경매 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내역 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuctionTrainerHistoryDTO.class)))),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/auction/history")
    public ResponseEntity<List<AuctionTrainerHistoryDTO>> getAuctionHistory(
            @Parameter(description = "인증된 트레이너 정보", required = true)
            @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO) {
        return ResponseEntity.ok(reverseAuctionService.getAuctionHistoryToTrainer(memberAuthDTO.getEmail()));
    }

    @Operation(summary = "트레이너 정보 수정", description = "트레이너 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 수정 성공",
                    content = @Content(schema = @Schema(type = "integer", format = "int64"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "트레이너를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping
    public ResponseEntity<Long> modifyMember(
            @Parameter(description = "인증된 트레이너 정보", required = true)
            @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 트레이너 정보", required = true,
                    content = @Content(schema = @Schema(implementation = TrainerSaveRequestDTO.class)))
            @RequestBody TrainerSaveRequestDTO trainerSaveRequestDTO) {
        return ResponseEntity.ok(trainerService.updateTrainer(memberAuthDTO.getEmail(), trainerSaveRequestDTO));
    }

    @Operation(summary = "트레이너 정보 조회", description = "로그인한 트레이너의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = TrainerResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "트레이너를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<TrainerResponseDTO> getMembers(
            @Parameter(description = "인증된 트레이너 정보", required = true)
            @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO) {
        return ResponseEntity.ok(trainerService.getTrainerDetail(memberAuthDTO.getEmail()));
    }

    @Operation(summary = "예약 목록 조회", description = "트레이너에게 예약된 PT 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingResponseDTO.class)))),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/booking")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookingsHistory(
            @Parameter(description = "인증된 트레이너 정보", required = true)
            @AuthenticationPrincipal final MemberAuthDTO memberDTO) {
        return ResponseEntity.ok(bookingService.getBookingListByTrainer(memberDTO.getEmail()));
    }

    @Operation(summary = "리뷰 내역 조회", description = "트레이너에 대한 모든 리뷰를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewResponseDTO.class)))),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviewHistory(
            @Parameter(description = "인증된 트레이너 정보", required = true)
            @AuthenticationPrincipal final MemberAuthDTO memberDTO) {
        return ResponseEntity.ok(reviewService.getReviewListByTrainer(memberDTO.getEmail()));
    }

//    //트레이너에게만 보여지는 역경매 신청 list
//    @GetMapping("/auction/list")
//    public ResponseEntity<List<AuctionResponseToTrainerDTO>> getAuctionRequestList() {
//        List<AuctionResponseToTrainerDTO> response = reverseAuctionService.getAuctionListToTrainers(String );
//        return ResponseEntity.ok().body(response);
//
//    }

//    //사용자가 신청한 역경매 정보 디테일
//    @GetMapping("/auction/{auctionId}")
//    public ResponseEntity<AuctionResponseToTrainerDTO> getAuctionDetailById(@PathVariable Long auctionId) {
//        AuctionResponseToTrainerDTO auctionResponseToTrainerDTO = reverseAuctionService.getAuctionToTrainer(auctionId);
//        return ResponseEntity.ok().body(auctionResponseToTrainerDTO);
//    }
}