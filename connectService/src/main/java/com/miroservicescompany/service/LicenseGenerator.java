package com.miroservicescompany.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.miroservicescompany.entity.Company;

@Service
public class LicenseGenerator {
	
	public static String generateLicense(Company company) {
        String concatenatedString = company.getId() + "#" + company.getCompanyName() + "#" + company.getEmail();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(concatenatedString.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating license", e);
        }
    }

	
	public static String decryptLicense(String encryptedLicense) {
	    // Split the encrypted license into its components
	    String[] parts = encryptedLicense.split("#");
	    if (parts.length != 3) {
	        throw new IllegalArgumentException("Invalid encrypted license format");
	    }
	    
	    // Reconstruct the concatenated string
	    String concatenatedString = parts[0] + "#" + parts[1] + "#" + parts[2];
	    
	    // Return the concatenated string as the decrypted license
	    return concatenatedString;
	}
}

