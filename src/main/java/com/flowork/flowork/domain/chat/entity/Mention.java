package com.flowork.flowork.domain.chat.entity;

import com.flowork.flowork.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Mention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    private User mentioner;

    @ManyToOne(fetch = FetchType.LAZY)
    private User mentioned;
}
