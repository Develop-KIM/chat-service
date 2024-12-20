package com.developkim.chatservice.service;

import static com.developkim.chatservice.enums.Role.CONSULTANT;

import com.developkim.chatservice.dtos.ChatroomDto;
import com.developkim.chatservice.dtos.MemberDto;
import com.developkim.chatservice.entitites.Member;
import com.developkim.chatservice.enums.Role;
import com.developkim.chatservice.repositories.ChatroomRepository;
import com.developkim.chatservice.repositories.MemberRepository;
import com.developkim.chatservice.vos.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConsultantService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ChatroomRepository chatroomRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByName(username).get();

        if (Role.fromCode(member.getRole()) != CONSULTANT) {
            throw new AccessDeniedException("상담사가 아닙니다.");
        }

        return new CustomUserDetails(member, null);
    }

    public MemberDto saveMember(MemberDto memberDto) {
        Member member = MemberDto.to(memberDto);
        member.updatePassword(memberDto.password(), memberDto.confirmedPassword(), passwordEncoder);

        return MemberDto.from(memberRepository.save(member));
    }

    public Page<ChatroomDto> getChatroomPage(Pageable pageable) {
        return chatroomRepository
                .findAll(pageable)
                .map(ChatroomDto::from);
    }
}
