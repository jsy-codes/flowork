package com.flowork.flowork.domain.activity.service;

import com.flowork.flowork.domain.activity.entity.ActivityLog;
import com.flowork.flowork.domain.activity.entity.ActivityType;
import com.flowork.flowork.domain.activity.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogService {
    private final ActivityLogRepository activityLogRepository;

    @Async
    public void log(Long userId, ActivityType type, Long referenceId){
        try{
            ActivityLog logEntry = ActivityLog.builder()
                    .userId(userId)
                    .type(type)
                    .referenceId(referenceId)
                    .build();
            activityLogRepository.save(logEntry);
            log.info("ActivityLog record - userId: {},type: {},refId: {}", userId, type, referenceId);
        }catch(Exception e){
            log.error("ActivityLog record Failed -  userId: {}, type: {}", userId, type, e);
        }
    }
}
