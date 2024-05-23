package com.miroservicescompany.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncryptedData {
	
	 private String encryptedData;
	    private String secretKey;
}
