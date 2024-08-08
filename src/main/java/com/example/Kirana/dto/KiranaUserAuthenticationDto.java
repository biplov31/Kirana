package com.example.Kirana.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KiranaUserAuthenticationDto {

    private String email;
    private String password;

}
