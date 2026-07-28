package com.flowork.flowork.domain.chat.dto;

import com.flowork.flowork.domain.chat.entity.ChatRoom;
import com.flowork.flowork.domain.chat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderUsername;
    private String content;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(Message message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getChatRoom().getId(),
                message.getSender().getId(),
                message.getSender().getUsername(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
