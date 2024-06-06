package com.miroservicescompany.controller;

import static org.hamcrest.CoreMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.miroservicescompany.dto.CompanyDto;
import com.miroservicescompany.entity.Company;
import com.miroservicescompany.response.Response;
import com.miroservicescompany.response.ResponseGenerator;
import com.miroservicescompany.response.TransactionContext;
import com.miroservicescompany.service.CompanyService;

public class CompanyControllerTest {
	
	 @Mock
	    private CompanyService companyService;

	    @Mock
	    private ResponseGenerator responseGenerator;

	    @InjectMocks
	    private CompanyController companyController;

	    private TransactionContext context;
	    private CompanyDto companyDto;
	    private Company createdCompany;

	    @BeforeEach
	    public void setUp() {
	        MockitoAnnotations.initMocks(this);
	        context = new TransactionContext();
	        companyDto = new CompanyDto();
	        companyDto.setCompanyName("Test Company");
	        companyDto.setEmail("test@example.com");
	        companyDto.setPassword("password");
	        companyDto.setAddress("Test Address");

	        createdCompany = Company.builder()
	                .companyName("Test Company")
	                .email("test@example.com")
	                .password("password")
	                .address("Test Address")
	                .build();
	    }

	    @Test
	    public void testCreateCompanySuccess() {
	        when(companyService.createCompany((CompanyDto) any(CompanyDto.class))).thenReturn(createdCompany);
	        when(responseGenerator.generateTransactionContext(null)).thenReturn(context);
	        when(responseGenerator.successResponse(context, createdCompany, HttpStatus.CREATED))
	                .thenReturn(new ResponseEntity<>(new Response(), HttpStatus.CREATED));

	        ResponseEntity<Response> response = companyController.createCompany(companyDto);

	        assertEquals(HttpStatus.CREATED, response.getStatusCode());
	    }

		@Test
	    public void testCreateCompanyFailure() {
	        doThrow(new RuntimeException("Exception")).when(companyService).createCompany((CompanyDto) any(CompanyDto.class));
	        when(responseGenerator.generateTransactionContext(null)).thenReturn(context);
	        when(responseGenerator.errorResponse(context, "Failed to create company", HttpStatus.INTERNAL_SERVER_ERROR))
	                .thenReturn(new ResponseEntity<>(new Response(), HttpStatus.INTERNAL_SERVER_ERROR));

	        ResponseEntity<Response> response = companyController.createCompany(companyDto);

	        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	    }

		private void assertEquals(HttpStatus internalServerError, HttpStatusCode statusCode) {
			// TODO Auto-generated method stub
			
		}


}
