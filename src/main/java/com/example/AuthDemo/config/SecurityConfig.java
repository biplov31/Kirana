package com.example.AuthDemo.config;

import com.example.AuthDemo.repository.KiranaUserRepository;
import com.example.AuthDemo.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final KiranaAuthenticationProvider kiranaAuthenticationProvider;
    private final KiranaUserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl, KiranaAuthenticationProvider kiranaAuthenticationProvider, KiranaUserRepository userRepository) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.kiranaAuthenticationProvider = kiranaAuthenticationProvider;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        c -> c.requestMatchers("/").hasAnyRole("USER", "SUPER_ADMIN")
                                .requestMatchers("/ca").hasAnyRole("CA", "SUPER_ADMIN")
                                .requestMatchers("/super-admin").hasRole("SUPER_ADMIN")
                                .requestMatchers("/signup").permitAll()
                                .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsServiceImpl);
                // .authenticationProvider(kiranaAuthenticationProvider);

        return http.build();
    }

    // @Bean
    // public DaoAuthenticationProvider authenticationProvider() {
    //     DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    //     authProvider.setUserDetailsService(userDetailsServiceImpl);
    //     authProvider.setPasswordEncoder(passwordEncoder);
    //     return authProvider;
    // }


    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }

}
