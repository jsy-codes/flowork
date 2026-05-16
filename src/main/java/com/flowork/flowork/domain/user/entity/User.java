package com.flowork.flowork.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt;

    @PrePersist
    public void init(){
        this.createdAt = LocalDateTime.now();
    }

    // 암호화 로직을 Entity 도메인 메서드로 — Service가 직접 BCrypt 몰라도 됨
    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(this.password);
    }
}
