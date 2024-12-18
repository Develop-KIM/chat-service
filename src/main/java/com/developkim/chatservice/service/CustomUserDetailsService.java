package com.developkim.chatservice.service;

import static com.developkim.chatservice.enums.Role.CONSULTANT;

import com.developkim.chatservice.dtos.MemberDto;
import com.developkim.chatservice.entitites.Member;
import com.developkim.chatservice.enums.Role;
import com.developkim.chatservice.repositories.MemberRepository;
import com.developkim.chatservice.vos.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByName(username).get();

        if (Role.fromCode(member.getRole()) != CONSULTANT) {
            throw new AccessDeniedException("상담사가 아닙니다.");
        }

        return new CustomUserDetails(member);
    }

    public MemberDto saveMember(MemberDto memberDto) {
        Member member = MemberDto.to(memberDto);
        member.updatePassword(memberDto.password(), memberDto.confirmedPassword(), passwordEncoder);

        return MemberDto.from(memberRepository.save(member));
    }
}
