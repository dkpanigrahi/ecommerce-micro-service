package com.example.service;

import com.example.entity.OtpToken;
import com.example.enums.OtpPurpose;
import com.example.repository.OtpTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final Random random = new Random();

    public String generateOtp() {
        // Generate 6-digit OTP
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }


    public void sendOtp(String phoneNumber, String otp, OtpPurpose purpose) {
        // Clean up any existing unused OTPs for this phone number
        otpTokenRepository.deleteByPhoneNumberAndIsUsedFalse(phoneNumber);

        // Create new OTP token
        OtpToken otpToken = new OtpToken(phoneNumber, otp, purpose);
        otpTokenRepository.save(otpToken);

        // Send SMS
        String message = purpose == OtpPurpose.LOGIN ?
                "Your login OTP is: " + otp + ". Valid for 5 minutes." :
                "Your registration OTP is: " + otp + ". Valid for 5 minutes.";

//        smsService.sendSms(phoneNumber, message);
    }

    public boolean validateOtp(String phoneNumber, String otp) {
        Optional<OtpToken> otpTokenOpt = otpTokenRepository.findByPhoneNumberAndOtpCodeAndIsUsedFalse(phoneNumber, otp);

        if (otpTokenOpt.isEmpty()) {
            return false;
        }

        OtpToken otpToken = otpTokenOpt.get();
        return !otpToken.isExpired();
    }

    public void invalidateOtp(String phoneNumber, String otp) {
        Optional<OtpToken> otpTokenOpt = otpTokenRepository
                .findByPhoneNumberAndOtpCodeAndIsUsedFalse(phoneNumber, otp);

        if (otpTokenOpt.isPresent()) {
            OtpToken otpToken = otpTokenOpt.get();
            otpToken.setUsed(true);
            otpTokenRepository.save(otpToken);
        }
    }

    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void cleanupExpiredOtps() {
        List<OtpToken> expiredTokens = otpTokenRepository.findByPhoneNumberAndIsUsedFalse("")
                .stream()
                .filter(OtpToken::isExpired)
                .collect(Collectors.toList());

        otpTokenRepository.deleteAll(expiredTokens);
    }
}
