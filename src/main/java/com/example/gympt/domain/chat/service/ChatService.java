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

}
