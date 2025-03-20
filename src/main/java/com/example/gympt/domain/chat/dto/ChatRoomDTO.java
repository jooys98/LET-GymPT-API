package com.example.gympt.domain.chat.dto;


import com.example.gympt.domain.chat.entity.ChatRoom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅방 생성 요청/응답 DTO")
public class ChatRoomDTO {
    @Schema(description = "채팅방을 식별하는 id ")
    private Long id;
    @Schema(description = "request 시엔 null , response 일 경우엔 채팅 상대방의 이메일이 들어간다 ")
    private String sender;
    private String trainerEmail;
    private String trainerImage;
    private String trainerName;
    private String lastMessage;
    private LocalDateTime createdAt;

    private LocalDateTime lastMessageTime;




    public static ChatRoomDTO from(ChatRoom chatRoom, String sender, String message) {
        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .trainerImage(chatRoom.getTrainerProfile())
                .trainerEmail(chatRoom.getTrainer().getMember().getEmail())
                .trainerName(chatRoom.getTrainer().getTrainerName())
                .sender(sender)
                .createdAt(chatRoom.getCreatedAt())
                .lastMessageTime(chatRoom.getLastMessageTime())
                .lastMessage(message)
                .build();

    }
}
//처음 채팅방이 생성될때 서버에 전달되는 dto