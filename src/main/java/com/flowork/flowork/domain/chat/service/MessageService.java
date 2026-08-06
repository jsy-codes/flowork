package com.flowork.flowork.domain.chat.service;

import com.flowork.flowork.domain.activity.entity.ActivityType;
import com.flowork.flowork.domain.activity.event.ActivityLogEvent;
import com.flowork.flowork.domain.chat.dto.ChatMessageResponse;
import com.flowork.flowork.domain.chat.entity.ChatRoom;
import com.flowork.flowork.domain.chat.entity.Message;
import com.flowork.flowork.domain.chat.repository.ChatRoomRepository;
import com.flowork.flowork.domain.chat.repository.MessageRepository;
import com.flowork.flowork.domain.user.entity.User;
import com.flowork.flowork.domain.user.repository.UserRepository;
import com.flowork.flowork.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;

    //** 메시지 저장-WebSocket 핸들러에서 call**
    @Transactional
    public ChatMessageResponse saveMessage(Long roomId, Long senderId, String content) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        User sender = userService.findById(senderId);

        Message message = Message.builder()
                .chatRoom(room)
                .sender(sender)
                .content(content)
                .build();
        messageRepository.save(message);

        // MESSAGE_CREATED 이벤트 발행
        eventPublisher.publishEvent(
                new ActivityLogEvent(senderId, ActivityType.MESSAGE_CREATED, message.getId()));

        return ChatMessageResponse.from(message);
    }

    // ** 채팅 내역 - cursor 기반 페이지네이션
    public Slice<ChatMessageResponse> getMessages(Long roomId, Long cursor, int size) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 채팅방."));

        PageRequest pageable = PageRequest.of(0,size);

        Slice<Message> messages = (cursor == null)
                ? messageRepository.findByChatRoomOrderByIdDesc(room,pageable)
                : messageRepository.findByChatRoomAndIdLessThanOrderByIdDesc(room,cursor,pageable);
        return messages.map(ChatMessageResponse::from);

    }
    //읽음 처리 - Redis에 lstReadMessageId 저장.
    public void updateLastRead(Long roomId,Long userId,Long messageId){
        String key = "read:" + roomId+":"+ userId;
        redisTemplate.opsForValue().set(key,String.valueOf(messageId));
    }
    //읽음 위치 조회
    public Long getLastRead(Long roomId,Long userId){
        String key = "read:" + roomId+":"+ userId;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : null;
    }
    public Message findById(Long id){
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지")  );
    }
}
