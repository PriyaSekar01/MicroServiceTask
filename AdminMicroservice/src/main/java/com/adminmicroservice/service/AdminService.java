package com.adminmicroservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.adminmicroservice.dto.Encryption;
import com.adminmicroservice.response.Response;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

//	private final ResponseGenerator responseGenerator;
//	
//	private final DecryptingLicense secretKeyGenerator;

	@Value("${company.baseUrl}")
	private String companyBaseUrl;

	private final RestTemplate restTemplate;

	public ResponseEntity<Response> getEncryptedData(String companyName) {
		String url = companyBaseUrl + "/encryptEmailLicense?companyName=" + companyName;
		ResponseEntity<Response> response = restTemplate.exchange(url, HttpMethod.GET, null, Response.class);
		return response;
	}

	public ResponseEntity<Response> activateCompany(Encryption encryption, HttpHeaders headers) {
	    String url = companyBaseUrl + "/company/decryptForActivate";
	    HttpEntity<Encryption> requestEntity = new HttpEntity<>(encryption, headers);
	    return restTemplate.exchange(url, HttpMethod.POST, requestEntity, Response.class);
	}

}
