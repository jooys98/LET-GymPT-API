package com.example.gympt.domain.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class BookingResponseDTO {
    private Long id;
    private Long gymId;
    private String gymName;
    private String gymAddress;
    private Long trainerId;
    private String trainerName;
    private LocalDateTime bookingDate;
}
