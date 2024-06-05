package com.miroservicescompany.service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.miroservicescompany.entity.OtpDetails;
import com.miroservicescompany.exception.OtpServiceException;
import com.miroservicescompany.repository.OtpRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

	 private static final int OTP_EXPIRATION_MINUTES = 1;
	    private final JavaMailSender mailSender;
	    private final OtpRepository otpRepository;

	    public int generateAndSendOtp(String email) {
	        try {
	            // Generate random 6-digit OTP
	            int otp = ThreadLocalRandom.current().nextInt(100000, 999999);
	            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);
	            OtpDetails otpDetails = new OtpDetails(email, String.valueOf(otp), expirationTime);
	            otpRepository.save(otpDetails);

	            // Send OTP via email
	            sendOtpEmail(email, otp);

	            return otp;
	        } catch (Exception e) {
	            throw new OtpServiceException("Failed to send OTP", e);
	        }
	    }

	    private void sendOtpEmail(String email, int otp) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(email);
	        message.setSubject("Your OTP Code");
	        message.setText("Your OTP code is: " + otp + ". It is valid for 1 minute.");

	        mailSender.send(message);
	    }

	    public boolean validateOtp(String email, String otp) {
	        OtpDetails otpDetails = otpRepository.findByEmail(email);
	        if (otpDetails != null && otpDetails.getOtp().equals(otp) && !isOtpExpired(otpDetails)) {
	            return true; // Valid OTP
	        }
	        return false; // Invalid OTP or expired
	    }

	    public boolean isOtpExpired(String email) {
	        OtpDetails otpDetails = otpRepository.findByEmail(email);
	        return otpDetails == null || LocalDateTime.now().isAfter(otpDetails.getExpirationTime());
	    }

	    public boolean isOtpExpired(OtpDetails otpDetails) {
	        return LocalDateTime.now().isAfter(otpDetails.getExpirationTime());
	    }
	}