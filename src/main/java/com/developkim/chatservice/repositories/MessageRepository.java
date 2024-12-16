package com.developkim.chatservice.repositories;

import com.developkim.chatservice.entitites.Message;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByChatroomId(Long chatroomId);
}
