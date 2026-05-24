package com.flowork.flowork.domain.chat.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    private LocalDateTime createdAt;

    @PrePersist
    public void init()
    {
        createdAt = LocalDateTime.now();
    }

    public void updateName(String name){
        this.name = name;
    }
}
