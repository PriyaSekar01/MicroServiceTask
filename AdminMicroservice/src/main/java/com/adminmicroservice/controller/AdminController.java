package com.adminmicroservice.controller;

import java.time.LocalDate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adminmicroservice.decrypt.DecryptingLicense;
import com.adminmicroservice.dto.EncryptedData;
import com.adminmicroservice.dto.Encryption;
import com.adminmicroservice.response.Response;
import com.adminmicroservice.response.ResponseGenerator;
import com.adminmicroservice.response.TransactionContext;
import com.adminmicroservice.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	private final ResponseGenerator responseGenerator;

	private final DecryptingLicense decryptingLicense;

	@GetMapping("/fetchEncryptedData")
	public ResponseEntity<Response> fetchEncryptedData(@RequestParam String companyName) {
		return adminService.getEncryptedData(companyName);
	}

	@PostMapping("/decryptData")
	public ResponseEntity<String> decryptData(@RequestBody EncryptedData request) {
	    try {
	        // Generate HttpHeaders
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        // Generate TransactionContext
	        TransactionContext context = responseGenerator.generateTransactionContext(headers);

	        // Decrypt data
	        Encryption decryptedData = decryptingLicense.decrypt(request.getEncryptedData(), request.getSecretKey());
	        if (decryptedData != null) {
	            // Call activateCompany with decryptedData and headers
	            ResponseEntity<Response> activationResponse = adminService.activateCompany(decryptedData, headers);
	            if (activationResponse.getStatusCode() == HttpStatus.OK) {
	                return ResponseEntity.ok("Company activated successfully");
	            } else {
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error activating company: " + activationResponse.getBody());
	            }
	        } else {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Decryption failed");
	        }
	    } catch (Exception e) {
	        String errorMessage = "Error decrypting data: " + e.getMessage();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
	    }
	}
}
