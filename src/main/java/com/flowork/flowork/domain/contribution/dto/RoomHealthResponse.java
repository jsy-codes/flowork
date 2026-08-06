package com.flowork.flowork.domain.contribution.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomHealthResponse {
    private Long roomId;
    private long totalMessages;      // 전체 메시지 수
    private long totalTasks;         // 전체 Task 수
    private long completedTasks;     // 완료 Task 수
    private Double taskCompletionRate; // 완료율 (%)
    private Double avgTaskMinutes;   // 평균 Task 소요 시간
    private long activeMemberCount;  // 활동한 멤버 수
}