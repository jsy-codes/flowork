package com.flowork.flowork.domain.chat.dto;

import lombok.Getter;

@Getter
public class ChatMessageRequest {
    private Long roomId;
    private String content;
}
