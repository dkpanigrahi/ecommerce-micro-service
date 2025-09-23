package com.example.repository;

import com.example.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findByPhoneNumberAndOtpCodeAndIsUsedFalse(String phoneNumber, String otpCode);

    void deleteByPhoneNumberAndIsUsedFalse(String phoneNumber);

    List<OtpToken> findByPhoneNumberAndIsUsedFalse(String phoneNumber);
}
