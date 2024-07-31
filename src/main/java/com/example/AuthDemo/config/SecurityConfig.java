package com.example.AuthDemo.config;

import com.example.AuthDemo.repository.KiranaUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final KiranaUserRepository userRepository;

    public SecurityConfig(UserDetailsService userDetailsService, KiranaUserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    // @Bean
    // public UserDetailsService userDetailsService() {
    //     var service = new InMemoryUserDetailsManager();
    //
    //     var user = User.withUsername("John")
    //             .password("john123")
    //             .roles("CA")
    //             .build();
    //
    //     service.createUser(user);
    //     return service;
    // }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(
                        c -> c.requestMatchers("/").hasAnyRole("USER", "SUPER_ADMIN")
                                .requestMatchers("/ca").hasAnyRole("CA", "SUPER_ADMIN")
                                .requestMatchers("/super-admin").hasRole("SUPER_ADMIN")
                                .requestMatchers("/signup").permitAll()
                                .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // return new BCryptPasswordEncoder();
        return NoOpPasswordEncoder.getInstance();
    }

}
