package com.flowork.flowork.domain.activity.repository;

import com.flowork.flowork.domain.activity.entity.ActivityLog;
import com.flowork.flowork.domain.activity.entity.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog,Long> {
    List<ActivityLog> findByUserIdAndType(Long userId, ActivityType type);
    List<ActivityLog> findByUserId(Long userId);
    // 기간별 집계용
    List<ActivityLog> findByUserIdAndCreatedAtBetween(
            Long userId, LocalDateTime from, LocalDateTime to);

    long countByUserIdAndType(Long userId, ActivityType type); // contribution 추가
}
