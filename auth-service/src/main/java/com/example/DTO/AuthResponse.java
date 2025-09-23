package com.example.DTO;

import lombok.Data;

@Data
public class AuthResponse {

    private String message;
    private String token;
    private boolean isNewUser;
    private boolean otpSent;
    private String phoneNumber;

    public AuthResponse(String message, String token, boolean isNewUser, boolean otpSent, String phoneNumber) {
        this.message = message;
        this.token = token;
        this.isNewUser = isNewUser;
        this.otpSent = otpSent;
        this.phoneNumber = phoneNumber;
    }
}
