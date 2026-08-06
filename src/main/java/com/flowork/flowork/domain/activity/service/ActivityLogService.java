package com.flowork.flowork.domain.activity.service;

import com.flowork.flowork.domain.activity.entity.ActivityLog;
import com.flowork.flowork.domain.activity.entity.ActivityType;
import com.flowork.flowork.domain.activity.event.ActivityLogEvent;
import com.flowork.flowork.domain.activity.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogService {
    private final ActivityLogRepository activityLogRepository;
    //이벤트 리스너
    @Async
    @EventListener
    public void handleActivityLogEvent(ActivityLogEvent event){
        try{
            ActivityLog logEntry = ActivityLog.builder()
                    .userId(event.getUserId())
                    .type(event.getType())
                    .referenceId(event.getReferenceId())
                    .build();
            activityLogRepository.save(logEntry);
            log.info("ActivityLog 기록 - userId: {},type: {},referenceId: {}", logEntry.getUserId(), logEntry.getType(), logEntry.getReferenceId());

        }catch (Exception e){
            log.error("ActivityLog 기록 실패 - userId: {}, type: {}",
                    event.getUserId(), event.getType(), e);
        }
    }
    //유저별 활동 집계
    public Map<ActivityType,Long> getActivitySummary(Long userId){
        return activityLogRepository.findByUserId(userId).stream()
                .collect(Collectors.groupingBy(ActivityLog::getType, Collectors.counting()));
    }
    //chatRoom 멤버 전체 집계
    public Map<Long,Map<ActivityType,Long>> getRoomActivitySummary(List<Long> userIds){
        return userIds.stream()
                .collect(Collectors.toMap(
                        userId -> userId,
                        this::getActivitySummary
                ));
    }
}
