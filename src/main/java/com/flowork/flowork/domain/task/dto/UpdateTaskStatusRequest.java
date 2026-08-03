package com.flowork.flowork.domain.task.dto;

import com.flowork.flowork.domain.task.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateTaskStatusRequest {
    @NotNull
    private TaskStatus status;
}
