package com.example.gympt.domain.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
@Schema(description ="예약 정보를 담는 dto 이며 , 헬스장 단독 예약시 trainerId는 null 이 됩니다.")
public class BookingRequestDTO {
    private String email;
    private Long gymId;
    private Long trainerId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime bookingDate;
}
