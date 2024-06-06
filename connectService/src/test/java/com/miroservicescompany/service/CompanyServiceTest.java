package com.miroservicescompany.service;

import static org.hamcrest.CoreMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.miroservicescompany.dto.CompanyDto;
import com.miroservicescompany.entity.Company;
import com.miroservicescompany.exception.CompanyServiceException;
import com.miroservicescompany.repository.CompanyRepository;

public class CompanyServiceTest {
	
	 @Mock
	    private CompanyRepository companyRepository;

	    @Mock
	    private LicenseGenerator licenseGenerator;

	    @Mock
	    private OtpService otpService;

	    @InjectMocks
	    private CompanyService companyService;

	    private CompanyDto companyDto;
	    private Company company;

	    @BeforeEach
	    public void setUp() {
	        MockitoAnnotations.initMocks(this);
	        companyDto = new CompanyDto();
	        companyDto.setCompanyName("Test Company");
	        companyDto.setEmail("test@example.com");
	        companyDto.setPassword("password");
	        companyDto.setAddress("Test Address");

	        company = Company.builder()
	                .companyName("Test Company")
	                .email("test@example.com")
	                .password("password")
	                .address("Test Address")
	                .build();
	    }

	    @Test
	    public void testCreateCompanySuccess() {
	        when(licenseGenerator.generateLicense((Company) any(Company.class))).thenReturn("license123");
	        when(companyRepository.save(any(Company.class))).thenReturn(company);

	        Company createdCompany = companyService.createCompany(companyDto);

	        assertEquals("license123", createdCompany.getLicense());
	        verify(otpService, times(1)).generateAndSendOtp("test@example.com");
	    }

	    private void assertEquals(String string, String license) {
			// TODO Auto-generated method stub
			
		}

		@Test
	    public void testCreateCompanyFailure() {
	        when(companyRepository.save(any(Company.class))).thenThrow(new RuntimeException("Exception"));

	        try {
	            companyService.createCompany(companyDto);
	        } catch (CompanyServiceException e) {
	            assertEquals("Failed to create company", e.getMessage());
	        }
	    }

}
