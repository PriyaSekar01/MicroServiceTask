package com.miroservicescompany.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.miroservicescompany.dto.OtpDetails;
import com.miroservicescompany.exception.OtpServiceException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

	private final Map<String, OtpDetails> otpStorage = new HashMap<>();
    private static final int OTP_EXPIRATION_MINUTES = 1;
    private final JavaMailSender mailSender;

    public int generateAndSendOtp(String email) {
        try {
            // Generate random 6-digit OTP
            int otp = ThreadLocalRandom.current().nextInt(100000, 999999);
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);
            otpStorage.put(email, new OtpDetails(String.valueOf(otp), expirationTime));

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Your OTP Code");
            message.setText("Your OTP code is: " + otp + ". It is valid for 1 minutes.");

            mailSender.send(message);

            return otp;
        } catch (Exception e) {
            throw new OtpServiceException("Failed to send OTP", e);
        }
    }

    public boolean validateOtp(String email, String otp) {
        OtpDetails otpDetails = otpStorage.get(email);
        if (otpDetails != null && otpDetails.getOtp().equals(otp) && !isOtpExpired(email)) {
            otpStorage.remove(email);
            return true; // Valid OTP
        }
        return false; // Invalid OTP
    }

    public boolean isOtpExpired(String email) {
        OtpDetails otpDetails = otpStorage.get(email);
        return otpDetails != null && LocalDateTime.now().isAfter(otpDetails.getExpirationTime());
    }
}