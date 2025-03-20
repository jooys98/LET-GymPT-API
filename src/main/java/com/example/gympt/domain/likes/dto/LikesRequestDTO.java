package com.example.gympt.domain.likes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
@ToString
@Schema(description = "좋아요 누를 헬스장과 트레이너 id 가 담긴 dto 이며 헬스장 좋아요시 trainerId는 null 로 보내집니다.")
public class LikesRequestDTO {

    private Long gymId;
    private Long trainerId;
}
