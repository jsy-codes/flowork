package com.flowork.flowork.domain.activity.repository;

import com.flowork.flowork.domain.activity.entity.ActivityLog;
import com.flowork.flowork.domain.activity.entity.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog,Long> {
    List<ActivityLog> findByUserIdAndType(Long userId, ActivityType type);
    List<ActivityLog> findByUserId(Long userId);
}
