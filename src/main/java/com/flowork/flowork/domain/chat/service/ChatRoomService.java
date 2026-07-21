package com.flowork.flowork.domain.chat.service;

import com.flowork.flowork.domain.chat.dto.ChatRoomResponse;
import com.flowork.flowork.domain.chat.dto.MemberResponse;
import com.flowork.flowork.domain.chat.entity.ChatRoom;
import com.flowork.flowork.domain.chat.entity.ChatRoomMember;
import com.flowork.flowork.domain.chat.entity.RoomRole;
import com.flowork.flowork.domain.chat.entity.RoomType;
import com.flowork.flowork.domain.chat.repository.ChatRoomMemberRepository;
import com.flowork.flowork.domain.chat.repository.ChatRoomRepository;
import com.flowork.flowork.domain.user.entity.User;
import com.flowork.flowork.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserService userService;
    // 채팅방 생성 - 만든 사람은 host로 자동참가
    @Transactional
    public ChatRoomResponse createRoom(String name, RoomType roomType,Long userId) {
        User user = userService.findById(userId);
        ChatRoom chatRoom = ChatRoom.builder()
                .name(name)
                .type(roomType)
                .build();
        chatRoomRepository.save(chatRoom);
        ChatRoomMember host = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .user(user)
                .role(RoomRole.HOST)
                .build();
        chatRoomMemberRepository.save(host);
        return ChatRoomResponse.from(chatRoom);
    }
    //채팅방 참가
    @Transactional
    public void joinRoom(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new IllegalArgumentException("Room Not Found"));
        User user = userService.findById(userId);
        if(chatRoomMemberRepository.existsByChatRoomAndUser(room,user)){
            throw new IllegalArgumentException("Room Already Exists");
        }
        ChatRoomMember member = ChatRoomMember.builder()
                .chatRoom(room)
                .user(user)
                .role(RoomRole.MEMBER)
                .build();
        chatRoomMemberRepository.save(member);
    }
    /*내가 참가한 채팅방 List*/
    public List<ChatRoomResponse> getMyRooms(Long userId) {
        User user = userService.findById(userId);
        return chatRoomMemberRepository.findByUser(user).stream()
                .map(m->ChatRoomResponse.from(m.getChatRoom()))
                .toList();
    }
    //채팅방 member List
    public List<MemberResponse> getMembers(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new IllegalArgumentException("Room Not Found"));
        return chatRoomMemberRepository.findByChatRoom(room).stream()
                .map(MemberResponse::from)
                .toList();
    }
}
