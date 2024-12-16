package com.developkim.chatservice.repositories;

import com.developkim.chatservice.entitites.MemberChatroomMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberChatroomMappingRepository extends JpaRepository<MemberChatroomMapping, Long> {

}
