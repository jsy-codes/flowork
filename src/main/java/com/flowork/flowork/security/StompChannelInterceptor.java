package com.flowork.flowork.security;

import com.flowork.flowork.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        // CONNECT 시 JWT 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = extractToken(accessor);
            log.info("WS CONNECT 시도 - token: {}", token != null ? "있음" : "없음");

            if (token == null || !jwtTokenProvider.validateToken(token)) {
                throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
            }

            String email = jwtTokenProvider.getEmail(token);
            Long userId = jwtTokenProvider.getUserId(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // SecurityContext에 인증 정보 등록
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
            accessor.setUser(authentication);

            // Redis에 온라인 상태 기록 — TTL 30분 (접속 끊기면 자동 만료)
            String redisKey = "users:online:" + userId;
            redisTemplate.opsForValue().set(redisKey, "true", 30, TimeUnit.MINUTES);
            log.info("유저 온라인 상태 기록 - userId: {}", userId);
        }

        // DISCONNECT 시 온라인 상태 제거
        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            if (accessor.getUser() instanceof UsernamePasswordAuthenticationToken auth) {
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                String redisKey = "users:online:" + userDetails.getUserId();
                redisTemplate.delete(redisKey);
                log.info("유저 오프라인 처리 - userId: {}", userDetails.getUserId());
            }
        }

        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        String bearer = accessor.getFirstNativeHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}