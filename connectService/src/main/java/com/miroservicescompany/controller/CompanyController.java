package com.miroservicescompany.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miroservicescompany.dto.CompanyDto;
import com.miroservicescompany.dto.EncryptedData;
import com.miroservicescompany.dto.Encryption;
import com.miroservicescompany.entity.Company;
import com.miroservicescompany.exception.CompanyAccessException;
import com.miroservicescompany.exception.CompanyNotFoundException;
import com.miroservicescompany.exception.EncryptionException;
import com.miroservicescompany.response.Response;
import com.miroservicescompany.response.ResponseGenerator;
import com.miroservicescompany.response.TransactionContext;
import com.miroservicescompany.service.CompanyService;
import com.miroservicescompany.service.OtpService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {
	
	private final CompanyService companyService;
	
	private final ResponseGenerator  responseGenerator;
	
	private final OtpService otpService;
	
	@PostMapping("/create")
    public ResponseEntity<Response> createCompany(@RequestBody CompanyDto companyDto) {
        TransactionContext context = responseGenerator.generateTransactionContext(null);
        try {
            Company createdCompany = companyService.createCompany(companyDto);
            return responseGenerator.successResponse(context, createdCompany, HttpStatus.CREATED);
        } catch (Exception e) {
            return responseGenerator.errorResponse(context, "Failed to create company", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	@PostMapping("/login")
    public ResponseEntity<Response> login(@RequestParam String email, @RequestParam String password) {
        TransactionContext context = responseGenerator.generateTransactionContext(null);
        try {
            String result = companyService.login(email, password);
            if (result.equals("Login successfully")) {
                return responseGenerator.successResponse(context, result, HttpStatus.OK);
            } else {
                return responseGenerator.errorResponse(context, result, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return responseGenerator.errorResponse(context, "Failed to login", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	
	 @PostMapping("/forgot-password")
	    public ResponseEntity<Response> forgotPassword(@RequestParam String email) {
	        TransactionContext context = responseGenerator.generateTransactionContext(null);
	        try {
	            otpService.generateAndSendOtp(email);
	            return responseGenerator.successResponse(context, "OTP sent to email", HttpStatus.OK);
	        } catch (Exception e) {
	            return responseGenerator.errorResponse(context, "Failed to send OTP", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	 
	 
	 @PostMapping("/reset-password")
	    public ResponseEntity<Response> resetPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {
	        TransactionContext context = responseGenerator.generateTransactionContext(null);
	        try {
	            boolean isValid = otpService.validateOtp(email, otp);
	            if (isValid) {
	                companyService.resetPassword(email, newPassword);
	                return responseGenerator.successResponse(context, "Password reset successfully", HttpStatus.OK);
	            } else {
	                if (otpService.isOtpExpired(email)) {
	                    return responseGenerator.errorResponse(context, "Expired OTP", HttpStatus.BAD_REQUEST);
	                } else {
	                    return responseGenerator.errorResponse(context, "Invalid OTP", HttpStatus.BAD_REQUEST);
	                }
	            }
	        } catch (Exception e) {
	            return responseGenerator.errorResponse(context, "Failed to reset password", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Response> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        TransactionContext context = responseGenerator.generateTransactionContext(null);
        try {
            boolean isValid = otpService.validateOtp(email, otp);
            if (isValid) {
                return responseGenerator.successResponse(context, "OTP verified successfully", HttpStatus.OK);
            } else {
                if (otpService.isOtpExpired(email)) {
                    return responseGenerator.errorResponse(context, "Expired OTP", HttpStatus.BAD_REQUEST);
                } else {
                    return responseGenerator.errorResponse(context, "Invalid OTP", HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception e) {
            return responseGenerator.errorResponse(context, "Failed to verify OTP", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	@PostMapping("/encryptEmailLicense")
	public ResponseEntity<Response> encryptEmailLicense(@RequestParam String companyName,
	                                                    @RequestParam String adminEmail,
	                                                    @RequestParam String subject) {
	    TransactionContext context = responseGenerator.generateTransactionContext(null);
	    try {
	        EncryptedData encryptedData = companyService.encryptEmailLicense(companyName, adminEmail, subject);
	        return responseGenerator.successResponse(context, encryptedData, HttpStatus.OK);
	    } catch (EncryptionException e) {
	        return responseGenerator.errorResponse(context, "Encryption failed", HttpStatus.INTERNAL_SERVER_ERROR);
	    } catch (CompanyNotFoundException e) {
	        return responseGenerator.errorResponse(context, "Company not found", HttpStatus.NOT_FOUND);
	    }
	}


	
	 @GetMapping("/license/{id}")
	    public ResponseEntity<Response> getLicenseById(@PathVariable Long id) {
	        TransactionContext context = responseGenerator.generateTransactionContext(null);
	        try {
	            CompanyDto company = companyService.getLicense(id);
	            if (company != null) {
	                return responseGenerator.successResponse(context, company, HttpStatus.OK);
	            } else {
	                return responseGenerator.errorResponse(context, "License not found", HttpStatus.NOT_FOUND);
	            }
	        } catch (Exception e) {
	            return responseGenerator.errorResponse(context, "Error retrieving license", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	 
	 
	 @GetMapping("/getAll")
	    public ResponseEntity<Response> getAllLicense() {
	        TransactionContext context = responseGenerator.generateTransactionContext(null);
	        try {
	            List<CompanyDto> companies = companyService.getAllLicense();
	            return responseGenerator.successResponse(context, companies, HttpStatus.OK);
	        } catch (CompanyAccessException e) {
	            return responseGenerator.errorResponse(context, "Error accessing company data", HttpStatus.INTERNAL_SERVER_ERROR);
	        } catch (Exception e) {
	            return responseGenerator.errorResponse(context, "Error retrieving all licenses", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	 
	 @PostMapping("/decryptForActivate")
	 public ResponseEntity<String> activate(@RequestBody Encryption encryption) {
	     try {
	         String result = companyService.decryptForActivate(encryption);
	         return ResponseEntity.ok(result);
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error activating company: " + e.getMessage());
	     }
	 }
}
