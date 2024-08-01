package com.example.AuthDemo.dto;

import com.example.AuthDemo.model.KiranaUser;
import com.example.AuthDemo.model.KiranaUserRole;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KiranaUserDto {

    private KiranaUser kiranaUser;
    private String username;
    private String email;
    private String password;
    private String roles;

}
