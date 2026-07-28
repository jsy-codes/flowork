package com.flowork.flowork.common.controller;


import com.flowork.flowork.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<String> test(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok("인증 성공 - " + userDetails.getUsername());
    }
}