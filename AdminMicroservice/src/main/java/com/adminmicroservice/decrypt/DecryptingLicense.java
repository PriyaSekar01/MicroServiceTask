package com.adminmicroservice.decrypt;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.adminmicroservice.dto.Encryption;

@Component
public class DecryptingLicense {
	
	private static final String ALGORITHM = "AES";
	
	 private SecretKey generateSecretKey() throws NoSuchAlgorithmException {
	        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
	        keyGen.init(256); // AES key size can be 128, 192, or 256
	        return keyGen.generateKey();
	    }



	 public Encryption decrypt(String encryptedData, String base64EncodedKey) {
	        try {
	            byte[] decodedKey = Base64.getDecoder().decode(base64EncodedKey);
	            Cipher cipher = Cipher.getInstance(ALGORITHM);
	            SecretKeySpec keySpec = new SecretKeySpec(decodedKey, ALGORITHM);
	            cipher.init(Cipher.DECRYPT_MODE, keySpec);
	            byte[] decodedEncryptedData = Base64.getDecoder().decode(encryptedData);
	            byte[] decryptedData = cipher.doFinal(decodedEncryptedData);
	            String decryptedString = new String(decryptedData, StandardCharsets.UTF_8);
	            
	            // Split decrypted string into email and license
	            String[] parts = decryptedString.split(";");
	            if (parts.length == 2) {
	                String email = parts[0].trim();
	                String license = parts[1].trim();
	                return new Encryption(email, license);
	            } else {
	                // Handle invalid decrypted data format
	                return null;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }


}
