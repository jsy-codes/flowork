package com.flowork.flowork.domain.chat.entity;

import com.flowork.flowork.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoomMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private RoomRole role;

    private LocalDateTime joinedAt;

    @PrePersist
    public void init(){
        this.joinedAt = LocalDateTime.now();
    }
}
