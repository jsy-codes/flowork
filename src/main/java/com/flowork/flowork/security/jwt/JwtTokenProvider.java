package com.flowork.flowork.security.jwt;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessTokenExpMs;
    private final long refreshTokenExpMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-ms}") long accessTokenExpMs,
            @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpMs
    ){
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpMs = accessTokenExpMs;
        this.refreshTokenExpMs = refreshTokenExpMs;
    }
    public String generateAccessToken(Long userId,String email) {
        return buildToken(userId,email,accessTokenExpMs);
    }
    public String generateRefreshToken(Long userId, String email) {
        return buildToken(userId, email, refreshTokenExpMs);
    }
    private String buildToken(Long userId, String email, long expMs) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expMs))
                .signWith(key)
                .compact();

    }
    public Long getUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }
    public String getEmail(String token) {
        return getClaims(token).get("email",String.class);
    }
    public boolean validateToken(String token) {
        try{
            getClaims(token);
            return true;
        }catch(ExpiredJwtException e){
            log.warn("Expired JWT: {}", e.getMessage());
        }catch (JwtException | IllegalArgumentException e){
            log.warn("Invalid JWT: {}", e.getMessage());
        }
        return false;
    }
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
