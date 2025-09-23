package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponse {

    private boolean status;
    private String message;
    private String accessToken;
    private String refreshToken;

}
