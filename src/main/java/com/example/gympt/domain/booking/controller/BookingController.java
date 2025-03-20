package com.example.gympt.domain.booking.controller;

import com.example.gympt.domain.booking.dto.BookingRequestDTO;
import com.example.gympt.domain.booking.dto.BookingResponseDTO;
import com.example.gympt.domain.booking.dto.BookingUpdateDTO;
import com.example.gympt.domain.booking.entity.Booking;
import com.example.gympt.domain.booking.service.BookingService;
import com.example.gympt.domain.chat.dto.ChatRoomDTO;
import com.example.gympt.security.MemberAuthDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booking")
@Slf4j
@Tag(name = "booking-api", description = "예약하기 , 예약 조회/취소/수정 기능을 제공하는 API ")
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "예약내역 조회 ", description = "유저의 인증정보를 파라미터로 받아서 유저의 예약 내역을 조회 합니다")
    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings(@Parameter(description = "인증된 사용자 정보", hidden = true) @AuthenticationPrincipal final MemberAuthDTO memberDTO) {
        return ResponseEntity.ok(bookingService.getBookingList(memberDTO.getEmail()));
    }


    @Operation(
            summary = "예약하기",
            description = "유저의 인증정보를 파라미터로 받아서 유저의 예약 내역을 조회 합니다",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "방문 예정인 헬스장의 아이디 ,트레이너 아이디 , 예약 날짜/시간을 입력하여 예약을 확정합니다. ",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BookingRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "예약하기 성공")
            }
    )
    @PostMapping
    public ResponseEntity<Long> createBooking(@Parameter(description = "인증된 사용자 정보", hidden = true) @AuthenticationPrincipal final MemberAuthDTO memberDTO,
                                              @RequestBody BookingRequestDTO bookingRequestDTO) {
        return ResponseEntity.ok(bookingService.bookingGym(memberDTO.getEmail(), bookingRequestDTO));
    }

    @Operation(summary = "예약 취소 ", description = "유저의 인증정보와 예약 아이디를 파라미터로 받아서 유저의 예약을 취소 합니다")
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> cancelBooking(@Parameter(description = "인증된 사용자 정보", hidden = true) @AuthenticationPrincipal final MemberAuthDTO memberDTO,
                                              @Parameter(description = "booking ID", required = true)  @PathVariable Long id) {
        return ResponseEntity.ok(bookingService.deleteBooking(memberDTO.getEmail(), id));
    }


    @Operation(
            summary = "예약 수정",
            description = "유저의 인증정보와 예약 아이디를 파라미터로 받아서 유저의 예약날짜/시간을 수정 합니다",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정하길 원하는 예약 시간을 body 담아서 서버에 전송합니다 ",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChatRoomDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "예약수정 성공")
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Long> changeBooking(@Parameter(description = "인증된 사용자 정보", hidden = true)@AuthenticationPrincipal final MemberAuthDTO memberDTO,
                                              @Parameter(description = "booking ID", required = true)  @PathVariable Long id, @RequestBody BookingUpdateDTO bookingUpdateDTO) {
        return ResponseEntity.ok(bookingService.modifyBooking(memberDTO.getEmail(), id, bookingUpdateDTO));
    }
}
