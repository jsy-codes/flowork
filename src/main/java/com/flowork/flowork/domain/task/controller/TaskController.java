package com.flowork.flowork.domain.task.controller;

import com.flowork.flowork.domain.task.dto.CreateTaskRequest;
import com.flowork.flowork.domain.task.dto.TaskResponse;
import com.flowork.flowork.domain.task.dto.UpdateTaskStatusRequest;
import com.flowork.flowork.domain.task.service.TaskService;

import com.flowork.flowork.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    /**
     * Task create
     */
    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                taskService.createTask(request,userDetails.getUserId()));
    }
    //Task 상태 변경.
    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(
                taskService.updateStatus(taskId,request.getStatus(),userDetails.getUserId()));
    }
    //채팅방 Task list
    @GetMapping("/rooms/{roomId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasksByRoom(@PathVariable Long roomId){
        return ResponseEntity.ok(taskService.getTasksByRoom(roomId));
    }

    @GetMapping("/tasks/my")
    public ResponseEntity<List<TaskResponse>> getMyTasks(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(taskService.getMyTasks(userDetails.getUserId()));
    }

}
