package com.flowork.flowork.domain.contribution.service;

import com.flowork.flowork.domain.activity.entity.ActivityType;
import com.flowork.flowork.domain.activity.repository.ActivityLogRepository;
import com.flowork.flowork.domain.chat.entity.ChatRoom;
import com.flowork.flowork.domain.chat.repository.ChatRoomMemberRepository;
import com.flowork.flowork.domain.chat.repository.ChatRoomRepository;
import com.flowork.flowork.domain.contribution.dto.ContributionResponse;
import com.flowork.flowork.domain.contribution.dto.RoomHealthResponse;
import com.flowork.flowork.domain.task.entity.Task;
import com.flowork.flowork.domain.task.entity.TaskStatus;
import com.flowork.flowork.domain.task.repository.TaskRepository;
import com.flowork.flowork.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.OptionalDouble;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContributionService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ActivityLogRepository activityLogRepository;
    private final TaskRepository taskRepository;

    /**
     * 채팅방 멤버별 기여 breakdown
     * Redis 캐싱 - contrib:summary:{roomId}, TTL 10 minutes.
     */
    @Cacheable(value = "contrib:summary", key = "#roomId")
    public List<ContributionResponse> getContributions(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        List<User> members = chatRoomMemberRepository.findByChatRoom(room).stream()
                .map(m -> m.getUser())
                .toList();

        return members.stream()
                .map(user -> buildContribution(user, room))
                .toList();
    }

    /**
     * 팀 건강도 집계
     */
    @Cacheable(value = "contrib:health", key = "#roomId")
    public RoomHealthResponse getRoomHealth(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

        List<User> members = chatRoomMemberRepository.findByChatRoom(room).stream()
                .map(m -> m.getUser())
                .toList();

        // 전체 메시지 수
        long totalMessages = members.stream()
                .mapToLong(user -> activityLogRepository
                        .countByUserIdAndType(user.getId(), ActivityType.MESSAGE_CREATED))
                .sum();

        // Task 통계
        List<Task> allTasks = taskRepository.findByChatRoom(room);
        List<Task> completedTasks = taskRepository.findCompletedByChatRoom(room);

        // 평균 완료 소요 시간
        OptionalDouble avgMinutes = completedTasks.stream()
                .filter(t -> t.getDurationMinutes() != null)
                .mapToLong(Task::getDurationMinutes)
                .average();

        // 활동한 멤버 수 — MESSAGE_CREATED 또는 TASK_COMPLETED 있는 멤버
        long activeMemberCount = members.stream()
                .filter(user ->
                        activityLogRepository.countByUserIdAndType(
                                user.getId(), ActivityType.MESSAGE_CREATED) > 0)
                .count();

        double completionRate = allTasks.isEmpty() ? 0.0
                : (completedTasks.size() * 100.0 / allTasks.size());

        return new RoomHealthResponse(
                roomId,
                totalMessages,
                allTasks.size(),
                completedTasks.size(),
                Math.round(completionRate * 10) / 10.0,
                avgMinutes.isPresent() ? avgMinutes.getAsDouble() : null,
                activeMemberCount
        );
    }

    /** 캐시 무효화 — 새 메시지/Task 발생 시 호출 */
    @CacheEvict(value = {"contrib:summary", "contrib:health"}, key = "#roomId")
    public void evictCache(Long roomId) {}

    private ContributionResponse buildContribution(User user, ChatRoom room) {
        Long userId = user.getId();

        long messageCount = activityLogRepository
                .countByUserIdAndType(userId, ActivityType.MESSAGE_CREATED);
        long mentionCount = activityLogRepository
                .countByUserIdAndType(userId, ActivityType.MENTION_RECEIVED);
        long taskCreatedCount = activityLogRepository
                .countByUserIdAndType(userId, ActivityType.TASK_CREATED);
        long taskCompletedCount = activityLogRepository
                .countByUserIdAndType(userId, ActivityType.TASK_COMPLETED);

        // 평균 완료 소요 시간
        List<Task> completedTasks = taskRepository.findByAssigneeAndStatus(user, TaskStatus.COMPLETED);
        OptionalDouble avg = completedTasks.stream()
                .filter(t -> t.getDurationMinutes() != null)
                .mapToLong(Task::getDurationMinutes)
                .average();

        return new ContributionResponse(
                userId,
                user.getUsername(),
                messageCount,
                mentionCount,
                taskCreatedCount,
                taskCompletedCount,
                avg.isPresent() ? avg.getAsDouble() : null
        );
    }
    //** CSV Export(FE 추가예정.) - 멤버별 기여 breakdown
    public byte[] exportContributionsCsv(Long roomId) {
        List<ContributionResponse> contributions = getContributions(roomId);

        StringBuilder csv = new StringBuilder();

        // 헤더
        csv.append("userId,username,messageCount,mentionCount,taskCreatedCount,taskCompletedCount,avgCompletionMinutes\n");

        // 데이터 행
        for (ContributionResponse c : contributions) {
            csv.append(c.getUserId()).append(",")
                    .append(c.getUsername()).append(",")
                    .append(c.getMessageCount()).append(",")
                    .append(c.getMentionCount()).append(",")
                    .append(c.getTaskCreatedCount()).append(",")
                    .append(c.getTaskCompletedCount()).append(",")
                    .append(c.getAvgCompletionMinutes() != null ? c.getAvgCompletionMinutes() : "").append("\n");
        }

        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}