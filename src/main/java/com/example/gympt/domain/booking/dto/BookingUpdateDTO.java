package com.example.gympt.domain.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class BookingUpdateDTO {

    private LocalDateTime bookingDate;
}
