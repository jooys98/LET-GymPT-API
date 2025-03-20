package com.example.gympt.domain.booking.dto;

import com.example.gympt.domain.booking.entity.Booking;
import com.example.gympt.domain.gym.entity.GymImage;
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
    private String gymImage;
    private String gymAddress;
    private Long trainerId;
    private String trainerName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime bookingDate;


  public static BookingResponseDTO from(Booking booking) {
        return BookingResponseDTO.builder()
                .id(booking.getId())
                .gymId(booking.getGym().getId())
                .gymName(booking.getGym().getGymName())
                .gymImage(booking.getGym().getImageList().stream().map(GymImage::getGymImageName).findFirst().toString())
                .gymAddress(booking.getGym().getAddress())
                .trainerName(booking.getTrainers()==null ? null : booking.getTrainers().getTrainerName())
                .trainerId(booking.getTrainers()==null ? null : booking.getTrainers().getId())
                .bookingDate(booking.getBookingDate())
                .build();

    }
}
