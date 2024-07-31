package com.example.AuthDemo.service;

import com.example.AuthDemo.model.KiranaUser;
import com.example.AuthDemo.repository.KiranaUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final KiranaUserRepository userRepository;

    public UserDetailsServiceImpl(KiranaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        KiranaUser kiranaUser = userRepository.findByUsername(username);
        if (kiranaUser == null) throw new UsernameNotFoundException("User doesn't exist.");

        return User.withUsername(kiranaUser.getUsername())
                .password(kiranaUser.getPassword())
                .roles(kiranaUser.getRole().toString())
                .build();
    }

}
