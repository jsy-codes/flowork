package com.flowork.flowork.domain;

import com.flowork.flowork.domain.chat.RoomType;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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

}
