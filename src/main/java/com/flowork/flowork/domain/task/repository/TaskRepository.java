package com.flowork.flowork.domain.task.repository;

import com.flowork.flowork.domain.chat.entity.ChatRoom;
import com.flowork.flowork.domain.task.entity.Task;
import com.flowork.flowork.domain.task.entity.TaskStatus;
import com.flowork.flowork.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    //채팅방 기준 task 목록-Message를 통해 ChatRoom 조인함
    @Query("SELECT t from Task t WHERE t.message.chatRoom = :chatRoom")
    List<Task> findByChatRoom(@Param("chatRoom") ChatRoom chatRoom);

    List<Task> findByAssignee(User assignee);

    List<Task> findByAssigneeAndStatus(User assignee, TaskStatus status);

}
