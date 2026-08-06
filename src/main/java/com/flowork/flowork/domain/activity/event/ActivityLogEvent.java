package com.flowork.flowork.domain.activity.event;

import com.flowork.flowork.domain.activity.entity.ActivityType;
import lombok.Getter;

@Getter
public class ActivityLogEvent {
    private final Long userId;
    private final ActivityType type;
    private final Long referenceId;
    public ActivityLogEvent(Long userId, ActivityType type, Long referenceId){
        this.userId = userId;
        this.type = type;
        this.referenceId = referenceId;
    }
}
