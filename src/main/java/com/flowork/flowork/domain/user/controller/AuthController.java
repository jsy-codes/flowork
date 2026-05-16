package com.flowork.flowork.domain.user.controller;

import com.flowork.flowork.domain.user.dto.auth.AuthResponse;
import com.flowork.flowork.domain.user.dto.auth.LoginRequest;
import com.flowork.flowork.domain.user.dto.auth.SignupRequest;
import com.flowork.flowork.domain.user.entity.User;
import com.flowork.flowork.domain.user.service.UserService;
import com.flowork.flowork.security.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    //register
    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignupRequest request){
        userService.register(request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
    @PostMapping("/login") //login ==> providing access + refresh token
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        User user = userService.findByEmail(request.getEmail());
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) //401
                    .build();
        }

        String accessToken  = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }
    //Refresh Token으로 Access Token 재발급
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String bearerToken){
        String refreshToken = bearerToken.replace("Bearer ", "");
        if(!jwtTokenProvider.validateToken(refreshToken)){
            return ResponseEntity.
                    status(HttpStatus.UNAUTHORIZED)//401
                    .build();
        }
        Long userId = jwtTokenProvider.getUserId(refreshToken);
        String email = jwtTokenProvider.getEmail(refreshToken);

        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, email);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, email);

        return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
    }

}
