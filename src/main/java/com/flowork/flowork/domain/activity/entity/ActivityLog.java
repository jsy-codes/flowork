package com.flowork.flowork.domain.activity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private ActivityType type;

    private Long referenceId;// 이벤트 타겟 ID(messageId,taskId 등)

    private LocalDateTime createdAt;

    @PrePersist
    public void init(){
        this.createdAt = LocalDateTime.now();
    }


}
