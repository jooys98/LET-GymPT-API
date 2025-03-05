package com.example.gympt.domain.likes.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
@ToString
public class LikesRequestDTO {
    private Long gymId;
    private Long trainerId;
}
