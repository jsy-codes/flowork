package com.flowork.flowork.domain.chat.dto;

import com.flowork.flowork.domain.chat.entity.ChatRoom;
import com.flowork.flowork.domain.chat.entity.RoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomResponse {
    private Long id;
    private String name;
    private RoomType type;
    private LocalDateTime createdAt;

    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return new ChatRoomResponse(chatRoom.getId(),
                chatRoom.getName(),
                chatRoom.getType(),
                chatRoom.getCreatedAt());
    }
}
