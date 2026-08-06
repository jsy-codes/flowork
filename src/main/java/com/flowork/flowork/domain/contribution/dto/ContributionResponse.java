package com.flowork.flowork.domain.contribution.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContributionResponse {
    private Long userId;
    private String username;
    private long messageCount;      // 메시지 수
    private long mentionCount;      // 멘션 받은 수
    private long taskCreatedCount;  // 생성한 Task 수
    private long taskCompletedCount;// 완료한 Task 수
    private Double avgCompletionMinutes; // 평균 Task 완료 소요 시간
}