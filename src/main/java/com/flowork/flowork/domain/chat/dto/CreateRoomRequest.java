package com.flowork.flowork.domain.chat.dto;

import com.flowork.flowork.domain.chat.entity.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateRoomRequest {
    @NotBlank
    private String name;
    @NotNull
    private RoomType roomType;
}
