package com.developkim.chatservice.dtos;

import com.developkim.chatservice.entitites.Member;
import com.developkim.chatservice.enums.Gender;
import java.time.LocalDate;

public record MemberDto(
        Long id,
        String email,
        String name,
        String nickName,
        String password,
        String confirmedPassword,
        Gender gender,
        String phoneNumber,
        LocalDate birthday,
        String role
) {
    public static MemberDto from(Member member) {
        return new MemberDto(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getNickName(),
                null,
                null,
                member.getGender(),
                member.getPhoneNumber(),
                member.getBirthDay(),
                member.getRole()
        );
    }

    public static Member to(MemberDto memberDto) {
        return Member.builder()
                .id(memberDto.id())
                .email(memberDto.email())
                .name(memberDto.name())
                .nickName(memberDto.nickName())
                .gender(memberDto.gender())
                .phoneNumber(memberDto.phoneNumber())
                .birthDay(memberDto.birthday())
                .role(memberDto.role())
                .build();
    }
}
