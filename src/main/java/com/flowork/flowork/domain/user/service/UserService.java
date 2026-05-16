package com.flowork.flowork.domain.user.service;

import com.flowork.flowork.domain.user.dto.auth.AuthResponse;
import com.flowork.flowork.domain.user.entity.Role;
import com.flowork.flowork.domain.user.entity.User;
import com.flowork.flowork.domain.user.repository.UserRepository;
import com.flowork.flowork.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // BCryptPasswordEncoder 구현체 주입

    @Transactional
    public Long register(String username, String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용 중인 username입니다.");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(rawPassword)
                .role(Role.USER)
                .build();

        user.encodePassword(passwordEncoder); // 암호화는 Entity 메서드에 위임
        return userRepository.save(user).getId();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
    public AuthResponse login(String email, String password, JwtTokenProvider jwtTokenProvider) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalArgumentException("Incorrect email or password"));
       if(!passwordEncoder.matches(password, user.getPassword())) {
           throw new IllegalArgumentException("Incorrect email or password");
       }
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
       String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());
       return new AuthResponse(accessToken, refreshToken);
    }
}