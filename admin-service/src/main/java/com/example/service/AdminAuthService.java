package com.example.service;

import DTO.UserDTO;
import com.example.DTO.AdminLoginRequest;
import com.example.DTO.AdminLoginResponse;
import com.example.entity.Admin;
import com.example.external.AuthServiceClient;
import com.example.external.UserServiceClient;
import com.example.repository.AdminJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminJpaRepository adminJpaRepository;
    private final AuthServiceClient authServiceClient;

    private final PasswordEncoder encoder;

    public ResponseEntity<AdminLoginResponse> AdminLogin(AdminLoginRequest request) {
        try {
            Admin admin = adminJpaRepository.findByEmailAndPassword(request.getEmail(), request.getPassword());
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AdminLoginResponse(false, "Invalid Credentials", null, null));
            }

            admin.setLastLogin(new Date());
            adminJpaRepository.save(admin);

            UserDTO userDTO = new UserDTO();
            userDTO.setIsActive(true);
            userDTO.setUuid(admin.getUuid());
            userDTO.setRole(admin.getRole());
            userDTO.setPhoneNumber(admin.getPhoneNumber());
            userDTO.setEmail(admin.getEmail());

            String token = authServiceClient.getToken(userDTO);

            return ResponseEntity.ok(new AdminLoginResponse(true, "Login Successful", token, null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AdminLoginResponse(false, "Internal Server Error", null, null));
        }
    }

}
