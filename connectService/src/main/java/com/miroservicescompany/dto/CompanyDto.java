package com.miroservicescompany.dto;

import java.time.LocalDate;

import com.miroservicescompany.enumeration.GraceStatus;
import com.miroservicescompany.enumeration.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {

	private Long id;
	
	private String companyName;
	
	private String email;
	
	 private String address;
	 
	 private LocalDate gracePeriod;
	 
	 private Status status;
	 
	 private GraceStatus graceStatus;
	 
	 private LocalDate activationDate;
	 
	 private LocalDate expireDate;
	 
	  private String license;
}
