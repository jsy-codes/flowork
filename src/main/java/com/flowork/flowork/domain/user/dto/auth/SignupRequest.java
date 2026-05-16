package com.flowork.flowork.domain.user.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String password;

}
