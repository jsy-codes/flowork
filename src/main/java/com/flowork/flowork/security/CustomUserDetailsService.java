package com.flowork.flowork.security;

import com.jsy_codes.book_lecture_shop.domain.User;

import com.jsy_codes.book_lecture_shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 이메일로 사용자 조회
        User domainUser = userService.findByEmail(username);
        if (domainUser == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        // 주문 수에 따라 쿠폰 지급
        if (domainUser.getOrderCount() >= 2) {
           // domainUser.setRO(Grade.VIP);
        }

//        // Spring Security UserDetails 객체 생성 및 반환
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(domainUser.getEmail())          // 인증 키로 이메일 사용
//                .password(domainUser.getPassword())      // 이미 암호화된 비밀번호
//                .roles(domainUser.getRole().name())      // 권한 설정
//                .build();
        return new CustomUserDetails(domainUser);
    }



}
