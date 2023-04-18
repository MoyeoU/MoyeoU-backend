package com.moyeou.moyeoubackend.auth;

import com.moyeou.moyeoubackend.member.domain.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    private String username;

    private String password;

    private List<GrantedAuthority> authorities = Collections.emptyList();

    public CustomUserDetails(Member member) {
        this(member.getId().toString(), member.getPassword());
    }

    public CustomUserDetails(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public CustomUserDetails(String username, String password, String... roles) {
        this.username = username;
        this.password = password;
        this.authorities = Arrays.stream(roles)
                .map(role -> (GrantedAuthority) () -> role)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public static void main(String[] args) {
        Member member = new Member("a", "a", 1, "a", "a");
        UserDetails userDetails = new CustomUserDetails(member);
        new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }
}
