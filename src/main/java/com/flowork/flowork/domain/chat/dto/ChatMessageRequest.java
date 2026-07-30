package com.flowork.flowork.domain.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class ChatMessageRequest {
    private Long roomId;
    private String content;
}
