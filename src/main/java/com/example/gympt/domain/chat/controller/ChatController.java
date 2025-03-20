package com.example.gympt.domain.chat.controller;


import com.example.gympt.domain.chat.dto.ChatHistoryDTO;
import com.example.gympt.domain.chat.dto.ChatMessageDTO;
import com.example.gympt.domain.chat.dto.ChatRoomDTO;
import com.example.gympt.domain.chat.service.ChatService;
import com.example.gympt.exception.NotAccessChatRoom;
import com.example.gympt.security.MemberAuthDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "chat-api", description = "채팅방 생성, 메시지 전송, 대화 기록 조회 등 채팅 관련 기능을 제공하는 API ")
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;


    //메세지를 전송하는 api
    //양방향 통신이므로 return 값이 따로 없고 messagingTemplate.convertAndSend 를 통해 stomp 형식으로 클라이언트에 전송된다
    @MessageMapping("/message")
    public void handleMessage(@Parameter(description = "전송할 채팅 메시지 정보", required = true) ChatMessageDTO chatMessageDTO,
                              @Parameter(description = "STOMP 헤더에서 토큰을 추출하여 인증 처리 후에 양방향 통신이 시작됩니다", hidden = true) StompHeaderAccessor stompHeaderAccessor) {
        log.info("chatMessageDTO {}", chatMessageDTO);
        Authentication authentication = (Authentication) stompHeaderAccessor.getUser();
        MemberAuthDTO member = (MemberAuthDTO) authentication.getPrincipal();
        // roomId가 null 인 경우 (첫 메시지) 채팅방 생성 후 메시지 저장
        // 생성된 채팅방 ID 설정

        ChatMessageDTO savedMessage = chatService.saveMessage(chatMessageDTO, member.getEmail());
        messagingTemplate.convertAndSend("/topic/chat/message/" + chatMessageDTO.getRoomId(), savedMessage);
        //구독자에게 메세지 전송
        //클라이언트 구독주소(destination) + 채팅방 번호 + 전송되고 mongodb 애 저장될 메세지(payload)
        //mu sql 테이블에도 insert
        //topic/chat/room/{roomId} 에게 전송


    }

    @Operation(summary = "채팅 내역 조회", description = "유저의 인증정보와 roomId 를 파라미터로 받아서 해당 하는 채팅방의 상세 메세지 내역을 조회 합니다")
    @GetMapping("/history/{roomId}")
    public ResponseEntity<List<ChatHistoryDTO>> getChatHistory(@Parameter(description = "인증된 사용자 정보", hidden = true) @AuthenticationPrincipal final MemberAuthDTO memberDTO,
                                                               @Parameter(description = "채팅방 ID", required = true) @PathVariable Long roomId) {
        log.info("roomId {}", roomId);
        log.info("memberDTO {}", memberDTO);
        return ResponseEntity.ok(chatService.getChattingHistory(memberDTO.getEmail(), roomId));
    }

    //유저의 채팅방 리스트 보기
    @Operation(summary = "채팅방 리스트 조회 ", description = "유저의 인증정보를 받아서 해당 유저의 채팅 룸 리스트를 마지막 매세지와 함께 조회 합니다 ")
    @GetMapping("/history")
    public ResponseEntity<List<ChatRoomDTO>> getChatRooms(@Parameter(description = "인증된 사용자 정보", hidden = true) @AuthenticationPrincipal final MemberAuthDTO memberDTO) {
        log.info("memberDTO {}", memberDTO);
        return ResponseEntity.ok(chatService.findAllChatRooms(memberDTO.getEmail()));
    }


    @Operation(
            summary = "채팅방 생성",
            description = "새로운 채팅방을 생성합니다. 로그인한 사용자가 발신자가 됩니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "채팅방 생성에 필요한 정보, sender 필드는 서버에 null 로 전달 되며 response 시 sender 는 대화 상대방의 이메일 값이 저장되어 보내집니다 ",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChatRoomDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "채팅방 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "이미 존재하는 채팅방")
            }
    )
    @PostMapping
    public ResponseEntity<ChatRoomDTO> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO,
                                                      @Parameter(description = "인증된 사용자 정보", hidden = true)
                                                      @AuthenticationPrincipal final MemberAuthDTO memberDTO) {
        log.info("chatRoomDTO {}", chatRoomDTO);
        log.info("memberDTO {}", memberDTO);
        //로그인 한 회원 (보낸 회원의 이메일을 회원 필드에 주입)
        chatRoomDTO.setSender(memberDTO.getEmail());
        if (chatRoomDTO.getId() != null) {
            throw new NotAccessChatRoom("이미 존재하는 채팅방 입니다 ");
        }
        ChatRoomDTO newRoom = chatService.createChatRoom(chatRoomDTO, memberDTO.getEmail());
        return ResponseEntity.ok(newRoom);
    }


    @GetMapping("/notifications")
    @Operation(summary = "안읽은 수신 메세지 리스트", description = "isRead 값 false 인 메세지 리스트를 조회 합니다 ")
    public ResponseEntity<List<ChatMessageDTO>> getMessageNotifications(@Parameter(description = "인증된 사용자 정보", hidden = true)
                                                                        @AuthenticationPrincipal final MemberAuthDTO memberDTO) {
        log.info("memberDTO {}", memberDTO);
        return ResponseEntity.ok(chatService.getUnreadNotifications(memberDTO.getEmail()));
    }


    @Operation(summary = "읽음 상태를 바꾸는 api",
            description = "유저가 프론트 단에서 알림으로 뜬 안읽은 메세지를 클릭 하면 이 api 가 호출되어 읽음 상태가 바뀝니다")
    @PatchMapping("/{roomId}")
    public ResponseEntity<?> changeReadStatus(@Parameter(description = "인증된 사용자 정보", hidden = true)
                                              @AuthenticationPrincipal final MemberAuthDTO memberDTO,
                                              @Parameter(description = "채팅방 ID", required = true)
                                              @PathVariable Long roomId) {
        log.info("notificationId {}", roomId);
        chatService.changeRead(memberDTO.getEmail(), roomId);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "채팅방을 삭제하는 api ",
            description = "기존 채팅방이 삭제되지만 메세지는 따로 삭제가 안되는 api 입니다 ")
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Long> deleteChatRoom(@Parameter(description = "인증된 사용자 정보", hidden = true)
                                               @AuthenticationPrincipal final MemberAuthDTO memberDTO,
                                               @Parameter(description = "채팅방 ID", required = true)
                                               @PathVariable Long roomId) {
        log.info("memberDTO {}", memberDTO);
        Long deleteRoomId = chatService.deleteChatRoom(memberDTO.getEmail(), roomId);
        return ResponseEntity.ok(deleteRoomId);
    }
//    @GetMapping
//    public ResponseEntity<Boolean> existsChatRoom(@AuthenticationPrincipal final MemberDTO memberDTO, @RequestParam Long shopId) {
//        log.info("memberDTO {}", memberDTO);
//        log.info("shopId {}", shopId);
//        return ResponseEntity.ok(chatService.findChatRoom(memberDTO.getEmail(), shopId));
//    }

}
