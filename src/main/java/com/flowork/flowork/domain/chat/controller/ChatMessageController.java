package com.flowork.flowork.domain.chat.controller;

import com.flowork.flowork.domain.chat.dto.ChatMessageRequest;
import com.flowork.flowork.domain.chat.dto.ChatMessageResponse;
import com.flowork.flowork.domain.chat.entity.Message;
import com.flowork.flowork.domain.chat.service.MentionService;
import com.flowork.flowork.domain.chat.service.MessageService;
import com.flowork.flowork.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatMessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MentionService mentionService;
    /**
     * 메시지 전송
     * 클라이언트: /pub/chat.send로 발행.
     * 서버 : DB 저장 후 /topic/room.{roomId}로 broadCast
     */
    /**
     * @MessageMapping 메서드에서 @AuthenticationPrincipal이 WebSocket 컨텍스트에서 동작 안 해
     * */
    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequest request, Principal principal) {
        // principal.getName()은 UsernamePasswordAuthenticationToken.getName() → userId(String)
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getUserId();

        log.info("메시지 수신 - roomId: {}, sender: {}", request.getRoomId(), userId);

        ChatMessageResponse response = messageService.saveMessage(
                request.getRoomId(),
                userId,
                request.getContent()
        );

        //mention 처리 - 메시지 저장후
        Message message = messageService.findById(response.getId());
        mentionService.processMentions(message,userDetails.getUser());

        messageService.updateLastRead(request.getRoomId(), userId, response.getId());
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
