package com.example.gympt.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Schema(description = "채팅 상세 내역 조회")
public class ChatHistoryDTO {
    @Schema(description = "채팅방을 식별하는 id , ChatRoomDTO 에 있는 id를 참조한다 ")
    private Long roomId;
    @Schema(description = "메세지를 보낸 사용자의 이메일")
    private String email;
    private String name;
    private String trainerEmail;
    private String trainerName;
    private String trainerImage;
    private String shopImage;
    private String message;     // 채팅 내용

    //    @JsonFormat( pattern = "yyyy-MM-dd' 'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime sendTime; //마지막 대화 시간
}