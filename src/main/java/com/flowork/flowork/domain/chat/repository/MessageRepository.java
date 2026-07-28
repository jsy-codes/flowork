package com.flowork.flowork.domain.chat.repository;

import com.flowork.flowork.domain.chat.entity.ChatRoom;
import com.flowork.flowork.domain.chat.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MessageRepository extends JpaRepository<Message, Long> {
    //최신순 조회- 첫페이지.
    Slice<Message> findByChatRoomOrderByIdDesc(ChatRoom chatRoom, Pageable pageable);
    //cursor 기반 - 특정 messageId 이전 메세지
    Slice<Message> findByChatRoomAndIdLessThanOrderByIdDesc(ChatRoom chatRoom, Long id, Pageable pageable);
}
