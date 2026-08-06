package com.flowork.flowork.domain.activity.controller;

import com.flowork.flowork.domain.activity.entity.ActivityType;
import com.flowork.flowork.domain.activity.service.ActivityLogService;
import com.flowork.flowork.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    /** 내 활동 집계. */
    @GetMapping("/activity/my")
    public ResponseEntity<Map<ActivityType, Long>> getMyActivity(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(activityLogService.getActivitySummary(userDetails.getUserId()));
    }

    /** 채팅방 멤버 전체 활동 집계 */
    @GetMapping("/rooms/{roomId}/activity")
    public ResponseEntity<Map<Long, Map<ActivityType, Long>>> getRoomActivity(
            @PathVariable Long roomId,
            @RequestParam List<Long> userIds) {
        return ResponseEntity.ok(activityLogService.getRoomActivitySummary(userIds));
    }
}