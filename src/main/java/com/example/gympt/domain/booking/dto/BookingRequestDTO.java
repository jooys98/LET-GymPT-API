package com.example.gympt.domain.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class BookingRequestDTO {
    private String email;
    private Long gymId;
    private String gymName;
    private Long trainerId;
    private String trainerName;
    private LocalDateTime bookingDate;
}
