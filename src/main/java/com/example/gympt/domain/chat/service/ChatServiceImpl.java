package com.example.gympt.domain.chat.service;

import com.example.gympt.domain.chat.document.ChatMessages;
import com.example.gympt.domain.chat.dto.ChatHistoryDTO;
import com.example.gympt.domain.chat.dto.ChatMessageDTO;
import com.example.gympt.domain.chat.dto.ChatRoomDTO;
import com.example.gympt.domain.chat.entity.ChatRoom;
import com.example.gympt.domain.chat.repository.ChatMessageRepository;
import com.example.gympt.domain.chat.repository.ChatRoomRepository;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.repository.MemberRepository;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.repository.TrainerRepository;
import com.example.gympt.exception.NotAccessChatRoom;
import com.example.gympt.notification.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final TrainerRepository trainerRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final NotificationService notificationService;

    @Override
    public ChatMessageDTO saveMessage(ChatMessageDTO chatMessageDTO, String email) {
        log.info("chatMessageDTO: {}", chatMessageDTO);
        //shop 조회
        Trainers trainers = getTrainer(chatMessageDTO.getTrainerEmail());
        //구매자
        Member member = getMember(email);

        return this.saveMongoAndReturnChatDTO(chatMessageDTO, member, trainers);
    }


    //채팅방 아이디로 mu sql db와 mongo db 를 조회 한뒤 아이디에 해당하는 채팅방 메세지들을 전부 가져온다
    @Transactional(readOnly = true)
    @Override
    public List<ChatHistoryDTO> getChattingHistory(String email, Long roomId) {
        log.info("getChattingHistory : {}", email);
        log.info("roomId: {}", roomId);
        //회원 확인
        Member member = getMember(email);
        //채팅방이 있는지 확인
        ChatRoom chatRoom = getChatroom(roomId);

        // 파라미터로 온 이메일이 채팅방의 참여자 인지 확인
        //shop 의 셀러이거나 , 구매자의 이메일 둘 다 아니라면 ?
        if (!chatRoom.getTrainer().getMember().equals(member) && !chatRoom.getMember().equals(member)) {
            throw new NotAccessChatRoom("해당 채팅방의 참여자가 아닙니다.");
        }
        return getChatMessage(chatRoom);
    }


    @Override
    public ChatRoomDTO createChatRoom(ChatRoomDTO chatRoomDTO, String email) {
        //회원 조회
        log.info("createChatRoom : {}", chatRoomDTO);
        Member member = getMember(email);
        // shop 조회
        Trainers trainers = getTrainer(chatRoomDTO.getTrainerEmail());
        // sql ChatRoom 엔티티에 저장, id는 자동으로 생성되므로 null 로 전달

        ChatRoom chatRoom = ChatRoom.from(member, trainers, null);
        //새로운 chat room 생성 + 저장
        //대화 상대방
        String sender = this.getRoomMembers(chatRoom.getId()).stream()
                .filter(roomMember -> !roomMember.equals(member.getEmail()))
                .findFirst()
                .orElse(null);

        chatRoomRepository.save(chatRoom);
        //다시 dto로 변환하여 리턴
        return ChatRoomDTO.from(chatRoom, sender, null);

    }


    //룸 아이디로 채팅방 상세 조회
    private List<ChatHistoryDTO> getChatMessage(ChatRoom chatRoom) {
        //roomId 로 mongo db 메세지 내역을 조회
        List<ChatMessages> chatMessages = chatMessageRepository.findByRoomIdOrderBySendTimeAsc(chatRoom.getId());

        if (chatMessages.isEmpty()) {
            return Collections.emptyList();
        }

        return chatMessages.stream()
                .map(chatMessage -> ChatHistoryDTO.builder()
                        .roomId(chatRoom.getId())
                        .email(chatMessage.getEmail())
                        .trainerEmail(chatRoom.getTrainer().getMember().getEmail())
                        .trainerImage(chatMessage.getTrainerImage())
                        .message(chatMessage.getMessage())
                        .sendTime(chatMessage.getSendTime())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatRoomDTO> findAllChatRooms(String email) {
        log.info("findAllChatRooms : {}", email);
        Member member = getMember(email);

        //로그인한 회원의 메세지 룸 가져오기
        List<ChatRoom> chatRoomList = chatRoomRepository.findByMemberOrTrainerMember(member.getEmail());
        //메세지룸의 룸 아이디들을 가져오기
        List<Long> roomIds = chatRoomList.stream().map(ChatRoom::getId).toList();
        //룸 아이디들로 마지막 메세지 찾기
        List<ChatMessages> lastMessages = chatMessageRepository.findLastMessagesByRoomIds(roomIds);

//아이디 리스트 들 + 메세지 리스트 하나씩 빼서 convertToChatRoomDTO로 변환 시키고 가시 list 처리
        Map<Long, ChatMessages> lastMessageMap = lastMessages.stream()
                .filter(msg -> msg.getRoomId() != null)
                .collect(Collectors.toMap(
                        ChatMessages::getRoomId,
                        message -> message,
                        (existing, replacement) -> replacement  // 혹시 중복이 있을 경우 처리
                ));

        return chatRoomList.stream().map(chatRoom -> {
            String sender = this.getRoomMembers(chatRoom.getId()).stream()
                    .filter(roomMember -> !roomMember.equals(member.getEmail()))
                    .findFirst()
                    .orElse(null);
            ChatMessages lastMessage = lastMessageMap.get(chatRoom.getId());
            return ChatRoomDTO.from(chatRoom, sender,
                    lastMessage != null ? lastMessage.getMessage() : null);
        }).toList();
    }


    public Set<String> getRoomMembers(Long roomId) {
        return chatRoomRepository.findByChatMembers(roomId);
    }


    @Transactional
    @Override  // 읽음 상태 바꾸기
    public void changeRead(String email, Long roomId) {
        Member member = getMember(email);
        String sender = this.getRoomMembers(roomId).stream()
                .filter(roomMember -> !roomMember.equals(member.getEmail()))
                .findFirst()
                .orElse(null);
        chatMessageRepository.modifyIsRead(roomId, sender);
    }

    @Override
    public Long deleteChatRoom(String email, Long roomId) {
        Member member = getMember(email);
        chatRoomRepository.deleteByRoomIdAndEmail(member.getEmail(), roomId);
        return roomId;
    }

    @Override
    @Transactional(readOnly = true)//안읽은 알림 리스트
    public List<ChatMessageDTO> getUnreadNotifications(String email) {
        Member member = getMember(email);
        //안읽은 채티방 리스트에 참여한 멤버들을 찾기
        // 해당 사용자의 채팅방들을 가져옴
        List<ChatRoom> chatRooms = chatRoomRepository.findByMemberOrTrainerMember(member.getEmail());
        List<Long> roomIds = chatRooms.stream().map(ChatRoom::getId).toList();
        //메세지룸의 룸 아이디들을 가져오기
        List<ChatMessages> unreadMessages = chatMessageRepository.findLastMessagesAndIsReadFalseByRoomIds(roomIds);
        //룸 아이디들로 마지막 메세지 찾기
//아이디 리스트 들 + 메세지 리스트 하나씩 빼서 convertToChatRoomDTO로 변환 시키고 가시 list 처리
        Map<Long, ChatMessages> unreadMessagesMap = unreadMessages.stream()
                .filter(msg -> msg.getRoomId() != null)
                .collect(Collectors.toMap(
                        ChatMessages::getRoomId,
                        message -> message,
                        (existing, replacement) -> replacement  // 혹시 중복이 있을 경우 처리
                ));
        // 각 채팅방의 마지막 메시지 발신자 정보와 함께 DTO로 변환
        return chatRooms.stream()
                .map(room -> {
                    // 채팅방의 마지막 메시지 발신자 찾기
                    String sender = this.getRoomMembers(room.getId()).stream()
                            .filter(roomMember -> !roomMember.equals(member.getEmail()))
                            .findFirst()
                            .orElse(null);
                    ChatMessages lastMessage = unreadMessagesMap.get(room.getId());
                    return ChatMessageDTO.from(lastMessage, sender);
                })
                .toList();
    }


    private Member getMember(String email) {
        return memberRepository.getWithRoles(email).orElseThrow(() -> new EntityNotFoundException("Member with email " + email + " not found"));
    }

    private ChatRoom getChatroom(Long roomId) {
        return chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("해당 아이디의 채팅룸은 존재하지 않습니다 "));
    }

    private Trainers getTrainer(String trainerEmail) {
        return trainerRepository.findByTrainerEmail(trainerEmail).orElseThrow(() -> new EntityNotFoundException("Trainers with id " + trainerEmail + " not found"));
    }

    private ChatMessageDTO saveMongoAndReturnChatDTO(ChatMessageDTO chatMessageDTO, Member member, Trainers trainers) {
        //채팅룸의 아이디로 엔티티 조회
        ChatRoom chatRoom = getChatroom(chatMessageDTO.getRoomId());
        //상대방 (메세지를 받는 입장)
        String sender = this.getRoomMembers(chatRoom.getId()).stream()
                .filter(roomMember -> !roomMember.equals(member.getEmail()))
                .findFirst()
                .orElse(null);
        // document 변환
        ChatMessages chatMessage = ChatMessages.from(chatMessageDTO, member, trainers, chatRoom.getId());
        //mongodb 에 저장된 document
        chatMessageRepository.save(chatMessage);
        //저장된 document 를 다시 dto 로 변환하여 전달
        notificationService.sendChattingMessage(sender, ChatMessageDTO.from(chatMessage, sender), member);

        return ChatMessageDTO.from(chatMessage, sender);
    }


}
