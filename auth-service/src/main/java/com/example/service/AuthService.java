package com.example.service;

import DTO.ResponseEntityObject;
import DTO.UserDTO;
import com.example.DTO.AuthResponse;
import com.example.enums.OtpPurpose;
import com.example.external.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OtpService otpService;

    private final UserServiceClient userService;

    private final JwtService jwtService;

    public ResponseEntity<AuthResponse> initiateLogin(String phoneNumber) {
        try{
            // Validate phone number format
            if (!isValidPhoneNumber(phoneNumber)) {
                throw new IllegalArgumentException("Invalid phone number format");
            }

            boolean userExists = userService.existsByPhoneNumber(phoneNumber);
            String otp = otpService.generateOtp();

            if (userExists) {
                // Existing user - send login OTP
                otpService.sendOtp(phoneNumber, otp, OtpPurpose.LOGIN);
                return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(
                        "OTP sent successfully for login",
                        null,
                        false,
                        true,
                        phoneNumber
                ));
            } else {
                // New user - create user and send registration OTP
                ResponseEntityObject response = userService.createUser(phoneNumber);
                if(response.isStatus()){
                    otpService.sendOtp(phoneNumber, otp, OtpPurpose.REGISTRATION);
                    return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(
                            "New user created. OTP sent for registration and login",
                            null,
                            true,
                            false,
                            phoneNumber
                    ));
                }else{
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse(
                            response.getMessage(),
                            null,
                            true,
                            false,
                            phoneNumber
                    ));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();;
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(e.getMessage(), null, false, false, phoneNumber));
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^\\d{10}$");
    }

    public ResponseEntity<AuthResponse> verifyOtpAndLogin(String phoneNumber, String otp) {
        try{
            // Validate OTP
            if (!otpService.validateOtp(phoneNumber, otp)) {
                throw new IllegalArgumentException("Invalid or expired OTP");
            }

            UserDTO userOpt = userService.findByPhoneNumber(phoneNumber);
            if (userOpt == null) {
                throw new IllegalArgumentException("User not found");
            }

            // Mark user as verified if not already
            if (!userOpt.isVerified()) {
                Boolean status = userService.markUserAsVerified(phoneNumber);
                if(status){
                    userOpt.setVerified(true);
                }
            }

            // Invalidate the OTP
            otpService.invalidateOtp(phoneNumber, otp);

            // Generate JWT token
            String token = generateJwtToken(userOpt);

            return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(
                    "Login successful",
                    token,
                    !userOpt.isVerified(),
                    true,
                    phoneNumber
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(e.getMessage(), null, false, false, phoneNumber));
        }
    }

    public String generateJwtToken(UserDTO user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userUuid", user.getUuid());
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("isVerified", user.isVerified());
        claims.put("role",user.getRole());

        return jwtService.generateToken(claims, user.getPhoneNumber());
    }

    public ResponseEntity<Map<String, Object>> validateToken(String token) {
        try{
            boolean isValid = jwtService.validateToken(token);
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("error", "Invalid token");
            return ResponseEntity.badRequest().body(response);
        }
    }

}
