package com.flowork.flowork.domain.chat.service;

import com.flowork.flowork.domain.activity.entity.ActivityType;
import com.flowork.flowork.domain.activity.event.ActivityLogEvent;
import com.flowork.flowork.domain.chat.repository.MentionRepository;
import com.flowork.flowork.domain.activity.service.ActivityLogService;
import com.flowork.flowork.domain.chat.entity.Mention;
import com.flowork.flowork.domain.chat.entity.Message;
import com.flowork.flowork.domain.user.entity.User;
import com.flowork.flowork.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentionService {
    private final MentionRepository mentionRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;

    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+)");

    /**
     * message save 후 호출 - @username 파싱 => mention 저장 => 알림발송 -> activityLog
     */
    @Transactional
    public void processMentions(Message message, User mentioner){
        List<String> usernames = parseMentionUsernames(message.getContent());
        if(usernames.isEmpty()) return;
        List<Mention> mentions = new ArrayList<>();
        for(String username : usernames){
            userRepository.findByUsername(username).ifPresent(mentioned ->{
                //자기 자신 멘션 무시
                if(mentioned.getId().equals(mentioner.getId())) return;
                Mention mention = Mention.builder()
                        .message(message)
                        .mentioner(mentioner)
                        .mentioned(mentioned)
                        .build();
                mentions.add(mention);
                //개인 알림 채널로 STOMP push - /topic/user.{mentionedId}
                messagingTemplate.convertAndSend(
                        "/topic/user." + mentioned.getId(),
                        buildNotification(mentioner.getUsername(),message)
                );
                log.info("멘션 알림 발송 - to userId: {}",mentioned.getId());
                //Activity Log 비동기 기록
                eventPublisher.publishEvent(
                        new ActivityLogEvent(mentioned.getId(), ActivityType.MENTION_RECEIVED, message.getId()));
            });

        }
        if(!mentions.isEmpty()){
            mentionRepository.saveAll(mentions);
        }
    }

    private List<String> parseMentionUsernames(String content) {
        List<String> usernames = new ArrayList<>();
        Matcher matcher = MENTION_PATTERN.matcher(content);
        while(matcher.find()){
            usernames.add(matcher.group(1));
        }
        return usernames;
    }
    private String buildNotification(String mentionerUsername, Message message){
        return String.format("%s님이 채팅방에서 당신을 멘션했습니다 : %s",
                mentionerUsername,message.getContent());
    }


}
