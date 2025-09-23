package com.example.controller;

import DTO.UserDTO;
import DTO.VerifyOtpRequest;
import com.example.DTO.AuthResponse;
import com.example.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login/initiate")
    public ResponseEntity<AuthResponse> initiateLogin(@RequestParam("phoneNumber") String phoneNumber) {

            return authService.initiateLogin(phoneNumber);
    }

    @PostMapping("/login/verify")
    public ResponseEntity<AuthResponse> verifyOtpAndLogin(@RequestBody VerifyOtpRequest request) {

            return authService.verifyOtpAndLogin(request.getPhoneNumber(), request.getOtp());
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestParam("token") String token) {

            return authService.validateToken(token);
    }

    @PostMapping("/generate-token")
    public String validateToken(@RequestBody UserDTO userDTO) {

        return authService.generateJwtToken(userDTO);
    }
}
