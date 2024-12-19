package com.developkim.chatservice.vos;

import com.developkim.chatservice.entitites.Member;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails extends CustomOAuth2User implements UserDetails {

    public CustomUserDetails(Member member, Map<String, Object> attributes) {
        super(member, attributes);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(getMember().getRole()));
    }

    @Override
    public String getPassword() {
        return this.getMember().getPassword();
    }

    @Override
    public String getUsername() {
        return this.getMember().getName();
    }
}
