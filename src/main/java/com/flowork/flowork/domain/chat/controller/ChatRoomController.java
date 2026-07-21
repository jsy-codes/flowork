package com.flowork.flowork.domain.chat.controller;

import com.flowork.flowork.domain.chat.dto.ChatRoomResponse;
import com.flowork.flowork.domain.chat.dto.CreateRoomRequest;
import com.flowork.flowork.domain.chat.dto.MemberResponse;
import com.flowork.flowork.domain.chat.service.ChatRoomService;
import com.flowork.flowork.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    //my chat List
    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getMyRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatRoomService.getMyRooms(userDetails.getUserId()));
    }

    //create Chat
    @PostMapping
    public ResponseEntity<ChatRoomResponse> createRoom(
            @Valid @RequestBody CreateRoomRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        ChatRoomResponse response = chatRoomService.createRoom(
                request.getName(),request.getType(),userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    //participate Chat
    @PostMapping("/{roomId}/join")
    public ResponseEntity<Void> joinRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        chatRoomService.joinRoom(roomId, userDetails.getUserId());
        return ResponseEntity.ok().build();//응답은 존재, body없음. Void
    }



    //chat member List
    @GetMapping("/{roomId}/members")
    public ResponseEntity<List<MemberResponse>> getMembers(@PathVariable Long roomId){
        return ResponseEntity.ok(chatRoomService.getMembers(roomId));
    }
}
