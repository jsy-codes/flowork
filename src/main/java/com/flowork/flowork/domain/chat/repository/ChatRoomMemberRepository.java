package com.flowork.flowork.domain.chat.repository;

import com.flowork.flowork.domain.chat.entity.ChatRoom;
import com.flowork.flowork.domain.chat.entity.ChatRoomMember;
import com.flowork.flowork.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    //char member List.
    List<ChatRoomMember> findByChatRoom(ChatRoom chatRoom);
    // participating check
    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);
    //내가 참여한 채팅방 list.
    List<ChatRoomMember> findByUser(User user);
}
