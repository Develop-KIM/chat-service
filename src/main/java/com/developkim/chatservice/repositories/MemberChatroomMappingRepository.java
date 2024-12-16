package com.developkim.chatservice.repositories;

import com.developkim.chatservice.entitites.MemberChatroomMapping;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberChatroomMappingRepository extends JpaRepository<MemberChatroomMapping, Long> {

    boolean existsByMemberIdAndChatroomId(Long memberId, Long chatroomId);

    void deleteByMemberIdAndChatroomId(Long memberId, Long chatroomId);

    List<MemberChatroomMapping> findAllByMemberId(Long memberId);
}
