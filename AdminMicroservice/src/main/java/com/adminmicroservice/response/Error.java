package com.adminmicroservice.response;

import java.util.List;

import lombok.Data;

@Data
public class Error {
	
	 private String code;
	 private String reason;
	 private List<String> errorList;

}
