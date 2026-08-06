package com.flowork.flowork.domain.task.service;

import com.flowork.flowork.domain.activity.entity.ActivityType;
import com.flowork.flowork.domain.activity.event.ActivityLogEvent;
import com.flowork.flowork.domain.activity.service.ActivityLogService;
import com.flowork.flowork.domain.chat.entity.ChatRoom;
import com.flowork.flowork.domain.chat.entity.Message;
import com.flowork.flowork.domain.chat.repository.ChatRoomRepository;
import com.flowork.flowork.domain.chat.repository.MessageRepository;
import com.flowork.flowork.domain.task.dto.CreateTaskRequest;
import com.flowork.flowork.domain.task.dto.TaskResponse;
import com.flowork.flowork.domain.task.entity.Task;
import com.flowork.flowork.domain.task.entity.TaskStatus;
import com.flowork.flowork.domain.task.repository.TaskRepository;
import com.flowork.flowork.domain.user.entity.User;
import com.flowork.flowork.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;


    //task 생성
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request,Long creatorId){
        User assignee = userService.findById(request.getAssigneeId());
        Message message = null;
        if(request.getMessageId() != null){
            message = messageRepository.findById(request.getMessageId())
                    .orElseThrow(()->new IllegalArgumentException("Message not found"));

        }
        Task task = Task.builder()
                .title(request.getTitle())
                .assignee(assignee)
                .message(message)
                .build();
        taskRepository.save(task);

        //TASK_CREATED ActivityLog 비동기 기록.
        eventPublisher.publishEvent(
                new ActivityLogEvent(creatorId, ActivityType.TASK_CREATED, task.getId()));
        return TaskResponse.from(task);

    }
    //Task 상태 변경
    @Transactional
    public TaskResponse updateStatus(Long taskId, TaskStatus status,Long userId){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()->new IllegalArgumentException("Task not found"));

        task.updateStatus(status);
        //완료시 TASK_COMPLETED Activitylog 비동기적 기록
        if(status == TaskStatus.COMPLETED){
            eventPublisher.publishEvent(
                    new ActivityLogEvent(userId, ActivityType.TASK_COMPLETED, task.getId()));
        }
        return TaskResponse.from(task);
    }
    //채팅방 Task list
    public List<TaskResponse> getTasksByRoom(Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()->new IllegalArgumentException("Room not found"));
        return taskRepository.findByChatRoom(chatRoom).stream()
                .map(TaskResponse::from)
                .toList();
    }
    // my Task list
    public List<TaskResponse> getMyTasks(Long userId){
        User user = userService.findById(userId);
        return taskRepository.findByAssignee(user).stream()
                .map(TaskResponse::from)
                .toList();
    }
}
