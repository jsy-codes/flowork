package com.flowork.flowork.domain.chat.repository;

import com.flowork.flowork.domain.chat.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

}
