package com.flowork.flowork.domain.activity.repository;


import com.flowork.flowork.domain.chat.entity.Mention;
import com.flowork.flowork.domain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentionRepository extends JpaRepository<Mention, Long> {
    List<Mention> findByMessage(Message message);
    List<Mention> findByMentioned_Id(Long mentionedId);
}
