package com.flowork.flowork.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpMs;
    private final long refreshTokenExpMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-ms}") long accessTokenExpMs,
            @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpMs = accessTokenExpMs;
        this.refreshTokenExpMs = refreshTokenExpMs;
    }

    public String generateAccessToken(Long userId, String email) {
        return buildToken(userId, email, accessTokenExpMs);
    }

    public String generateRefreshToken(Long userId, String email) {
        return buildToken(userId, email, refreshTokenExpMs);
    }

    private String buildToken(Long userId, String email, long expMs) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))   // 0.12.x: setSubject =>subject
                .claim("email", email)
                .issuedAt(now)                     // 0.12.x: setIssuedAt =>issuedAt
                .expiration(new Date(now.getTime() + expMs)) // 0.12.x: setExpiration=> expiration
                .signWith(key)                     // 0.12.x: key만 넘기면 알고리즘 자동
                .compact();
    }

    public Long getUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    public String getEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("유효하지 않은 JWT: {}", e.getMessage());
        }
        return false;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()                       // 0.12.x:  parserBuilder() =>parser()
                .verifyWith(key)                   // 0.12.x: setSigningKey => verifyWith
                .build()
                .parseSignedClaims(token)          // 0.12.x: parseClaimsJws => parseSignedClaims
                .getPayload();                     // 0.12.x: getBody() => getPayload()
    }
}