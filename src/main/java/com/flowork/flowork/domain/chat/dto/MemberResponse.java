package com.flowork.flowork.domain.chat.dto;

import com.flowork.flowork.domain.chat.entity.ChatRoomMember;
import com.flowork.flowork.domain.chat.entity.RoomRole;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private String username;
    private RoomRole role;
    private LocalDateTime createdAt;

    public static MemberResponse from(ChatRoomMember member) {
        return new MemberResponse(
                member.getUser().getId(),
                member.getUser().getUsername(),
                member.getRole(),
                member.getJoinedAt()
        );
    }
}
