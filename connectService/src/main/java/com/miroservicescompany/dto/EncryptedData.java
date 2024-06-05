package com.miroservicescompany.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EncryptedData {
	
	 private String encryptedData;
	    private String secretKey;
		
}
