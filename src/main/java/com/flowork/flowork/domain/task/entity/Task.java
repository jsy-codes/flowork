package com.flowork.flowork.domain.task.entity;

import com.flowork.flowork.domain.chat.entity.Message;
import com.flowork.flowork.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    private Message message;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @PrePersist
    public void init() {
        this.createdAt = LocalDateTime.now();
        this.status = TaskStatus.PENDING;
    }
    public void updateStatus(TaskStatus status) {
        this.status = status;
        if(status == TaskStatus.COMPLETED) {
            this.completedAt = LocalDateTime.now();
        }

    }
    //소요 시간 계산(분 단위).
    public Long getDurationMinutes(){
        if(createdAt == null || completedAt == null) return null;
        return java.time.Duration.between(createdAt,completedAt).toMinutes();
    }

}
