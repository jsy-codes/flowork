package com.flowork.flowork.domain.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateTaskRequest {
    @NotBlank
    private String title;

    @NotNull
    private Long assigneeId;

    private Long messageId; //연결할 채팅 메시지 (잠정)
}
