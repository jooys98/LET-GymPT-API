package com.example.gympt.domain.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime bookingDate;
}
