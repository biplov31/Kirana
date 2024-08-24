package com.example.Kirana.dto;

import com.example.Kirana.model.KiranaUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KiranaOtpDto {

    private KiranaUser kiranaUser;
    private String otpCode;

}
