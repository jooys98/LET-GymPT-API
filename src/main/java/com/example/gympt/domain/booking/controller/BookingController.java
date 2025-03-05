package com.example.gympt.domain.booking.controller;

import com.example.gympt.domain.booking.dto.BookingRequestDTO;
import com.example.gympt.domain.booking.dto.BookingResponseDTO;
import com.example.gympt.domain.booking.dto.BookingUpdateDTO;
import com.example.gympt.domain.booking.entity.Booking;
import com.example.gympt.domain.booking.service.BookingService;
import com.example.gympt.security.MemberAuthDTO;
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
public class BookingController {
    private final BookingService bookingService;
//예약 내역 확인
    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings(@AuthenticationPrincipal final MemberAuthDTO memberDTO) {
        return ResponseEntity.ok(bookingService.getBookingList(memberDTO.getEmail()));
    }

//예약
    @PostMapping
    public ResponseEntity<Long> createBooking(@AuthenticationPrincipal final MemberAuthDTO memberDTO, @RequestBody BookingRequestDTO bookingRequestDTO) {
        return ResponseEntity.ok(bookingService.bookingGym(memberDTO.getEmail(), bookingRequestDTO));
    }
//예약 취소
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> cancelBooking(@AuthenticationPrincipal final MemberAuthDTO memberDTO, @PathVariable Long id) {
        return ResponseEntity.ok(bookingService.deleteBooking(memberDTO.getEmail(), id));
    }
//예약 날짜 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Long> changeBooking(@AuthenticationPrincipal final MemberAuthDTO memberDTO, @PathVariable Long id, @RequestBody BookingUpdateDTO bookingUpdateDTO) {
        return ResponseEntity.ok(bookingService.modifyBooking(memberDTO.getEmail(), id, bookingUpdateDTO));
    }
}
