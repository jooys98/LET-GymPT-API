package com.example.gympt.domain.chat.document;

import com.example.gympt.domain.chat.dto.ChatMessageDTO;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.trainer.entity.Trainers;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "chatMessages")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessages {
    @Id
    private String id; // mongodb 에선 string 타입의 id 사용 bson{}
    //MySQL의 ChatRoom 테이블의 ID를 참조
    private Long roomId;

    // 메시지 보낸 사람의 Member ID
    private String email;
    private String trainerEmail;
    private String trainerImage;
    private String message;     // 채팅 내용
    private LocalDateTime sendTime;

    @Builder.Default
    private boolean isRead = false;

//    public LocalDateTime getSendTimeAsInstant() {
//        return this.sendTime.atZone(ZoneOffset.UTC).toInstant();
//    }

//    public Instant toInstant() {
//        return this.sendTime != null
//                ? this.sendTime.atOffset(ZoneOffset.UTC).toInstant()
//                : null;
//    }

    public static ChatMessages from(ChatMessageDTO chatMessageDTO, Member member, Trainers trainers, Long id) {
        return ChatMessages.builder()
                .roomId(id)
                .trainerEmail(trainers.getMember().getEmail())
                .email(member.getEmail())
                .sendTime(chatMessageDTO.getSendTime())
                .trainerImage(trainers.getImageList().get(0).getTrainerImageName())
                .message(chatMessageDTO.getMessage())
                .isRead(false)
                .build();

    }

}

