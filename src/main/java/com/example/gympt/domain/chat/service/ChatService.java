package com.example.gympt.domain.chat.service;

import com.example.gympt.domain.chat.document.ChatMessages;
import com.example.gympt.domain.chat.dto.ChatHistoryDTO;
import com.example.gympt.domain.chat.dto.ChatMessageDTO;
import com.example.gympt.domain.chat.dto.ChatRoomDTO;
import com.example.gympt.domain.chat.entity.ChatRoom;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.trainer.entity.TrainerImage;
import com.example.gympt.domain.trainer.entity.Trainers;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatService {
    ChatMessageDTO saveMessage(ChatMessageDTO chatMessageDTO, String email);

    Long deleteChatRoom(String email, Long roomId);

    void changeRead(String email, Long roomId);

    List<ChatMessageDTO> getUnreadNotifications(String email);

    ChatRoomDTO createChatRoom(ChatRoomDTO chatRoomDTO, String email);

    List<ChatRoomDTO> findAllChatRooms(String email);

    List<ChatHistoryDTO> getChattingHistory(String email, Long roomId);

    default ChatRoomDTO convertToChatRoomDTO(ChatRoom chatRoom, String sender, String message) {
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

    default ChatRoom createChatRoomEntity(Member member, Trainers trainers, Long id) {
        return ChatRoom.builder()
                .id(id) //null 이면 자동으로 생성된다
                .trainer(trainers)
                .createdAt(LocalDateTime.now())
                .member(member)
                .lastMessageTime(LocalDateTime.now())
                .trainerProfile(trainers.getImageList().get(0).getTrainerImageName())
                .build();
    }

    default ChatMessages convertToDocument(ChatMessageDTO chatMessageDTO, Member member, Trainers trainers, Long id) {
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


    default ChatMessageDTO convertToDTO(ChatMessages chatMessages, String sender) {
        return ChatMessageDTO.builder()
                .roomId(chatMessages.getRoomId())
                .sender(sender)
                .email(chatMessages.getEmail())
                .trainerEmail(chatMessages.getTrainerEmail())
                .message(chatMessages.getMessage())
                .sendTime(chatMessages.getSendTime())
                .isRead(chatMessages.isRead())
                .build();

    }
}
