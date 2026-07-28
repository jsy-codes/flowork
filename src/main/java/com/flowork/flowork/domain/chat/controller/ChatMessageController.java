package com.flowork.flowork.domain.chat.controller;

import com.flowork.flowork.domain.chat.dto.ChatMessageRequest;
import com.flowork.flowork.domain.chat.dto.ChatMessageResponse;
import com.flowork.flowork.domain.chat.service.MessageService;
import com.flowork.flowork.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatMessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    /**
     * 메시지 전송
     * 클라이언트: /pub/chat.send로 발행.
     * 서버 : DB 저장 후 /topic/room.{roomId}로 broadCast
     */
    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequest request,
                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("메시지 수신 - roomId: {}, sender: {}", request.getRoomId(),userDetails.getUserId());
        ChatMessageResponse response = messageService.saveMessage(
                request.getRoomId(),
                userDetails.getUserId(),
                request.getContent()
        );
        //읽음 처리 -발신자는 자신이 보낸 메시지 읽음으로 처리
        messageService.updateLastRead(request.getRoomId(),userDetails.getUserId(),response.getId());
        //채팅방 구독자 전체에게 broadcast
        messagingTemplate.convertAndSend("/topic/room." + request.getRoomId(), response);

    }
    /**
     * 채팅 내역 조회 - cursor 기반 무한 스크롤
     * GET /api/rooms/{roomId}/messages?cursor=&size=20
     */
    @GetMapping("/api/rooms/{roomId}/messages")
    public ResponseEntity<Slice<ChatMessageResponse>> getMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size
    )
    {
        return ResponseEntity.ok(messageService.getMessages(roomId,cursor,size));
    }
}
