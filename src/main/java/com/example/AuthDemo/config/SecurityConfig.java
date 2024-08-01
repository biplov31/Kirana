package com.example.AuthDemo.config;

import com.example.AuthDemo.repository.KiranaUserRepository;
import com.example.AuthDemo.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(c -> c.disable())
                .httpBasic(c -> c.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        c -> c.requestMatchers("/user").hasAnyRole("USER", "SUPER_ADMIN")
                                .requestMatchers("/ca").hasAnyRole("CA", "SUPER_ADMIN")
                                .requestMatchers("/super-admin").hasRole("SUPER_ADMIN")
                                .requestMatchers("/signup").permitAll()
                                .requestMatchers("/authenticate").permitAll()
                                .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsServiceImpl)
                .authenticationProvider(new KiranaAuthenticationProvider(userDetailsServiceImpl, passwordEncoder))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // need to expose AuthenticationManager bean for login
    // @Bean
    // public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    //     return config.getAuthenticationManager();
    // }

}
