package com.example.gympt.domain.chat.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "채팅 메세지 생성 /응답 DTO")
public class ChatMessageDTO {
    @Schema(description = "채팅방을 식별하는 id , ChatRoomDTO 에 있는 id를 참조한다 ")
    private Long roomId;
    @Schema(description = "response 일 경우앤 메세지를 받는 상대방의 이메일 , request 일 경우엔 null ")
    private String sender;
    @Schema(description = "메세지를 보낸 유저의 이메일")
    private String email;
    private String trainerEmail;
    private String message;
    private boolean isRead;
    //    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime sendTime;
}