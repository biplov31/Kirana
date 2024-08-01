package com.example.AuthDemo.dto;

import com.example.AuthDemo.model.KiranaUser;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KiranaUserRegistrationDto {

    private KiranaUser kiranaUser;
    private String username;
    private String email;
    private String password;
    private String roles;

}
