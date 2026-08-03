package com.flowork.flowork.domain.task.dto;

import com.flowork.flowork.domain.task.entity.Task;
import com.flowork.flowork.domain.task.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private Long assigneeId;
    private String assigneeUsername;
    private Long messageId;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private Long durationMinutes;

    public static TaskResponse from(Task task){
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getAssignee().getId(),
                task.getAssignee().getUsername(),
                task.getMessage() != null ? task.getMessage().getId() : null,
                task.getStatus(),
                task.getCreatedAt(),
                task.getCompletedAt(),
                task.getDurationMinutes()
        );
    }
}
