package com.developkim.chatservice.repositories;

import com.developkim.chatservice.entitites.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

}
