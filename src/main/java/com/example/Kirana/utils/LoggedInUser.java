package com.example.Kirana.utils;

import com.example.Kirana.model.KiranaUser;
import com.example.Kirana.repository.KiranaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoggedInUser {

    private final KiranaUserRepository userRepository;

    public KiranaUser getLoggedInUserEntity() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = authentication.getName();

        KiranaUser loggedInUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        // return loggedInUser.getId();
        return loggedInUser;
    }
}
