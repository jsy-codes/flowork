package com.flowork.flowork.security;



import com.flowork.flowork.domain.user.entity.User;
import com.flowork.flowork.domain.user.entity.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
@Getter
@Setter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }
    public User getUser() {
        return this.user;
    }

    public Long getUserId() {
        return user.getId();
    }
    public Role getRole() {
        return user.getRole();
    }

    // UserDetails 메서드

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ROLE_ 접두사 붙여서 권한 생성
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // 필요시 구현
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // 필요시 구현
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 필요시 구현
    }

    @Override
    public boolean isEnabled() {
        return true;  // 필요시 구현
    }
}
