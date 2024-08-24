package com.example.Kirana.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

    private KiranaUser kiranaUser;

    public UserDetailsImpl(KiranaUser kiranaUser) {
        this.kiranaUser = kiranaUser;
    }

    @Override
    public String getUsername() {
        return kiranaUser.getEmail();
    }

    @Override
    public String getPassword() {
        return kiranaUser.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays
                .stream(kiranaUser.getRoles().split(","))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }

}
