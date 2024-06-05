package com.miroservicescompany.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.miroservicescompany.dto.CompanyDto;
import com.miroservicescompany.dto.EncryptedData;
import com.miroservicescompany.dto.Encryption;
import com.miroservicescompany.email.EmailSender;
import com.miroservicescompany.entity.Company;
import com.miroservicescompany.enumeration.GraceStatus;
import com.miroservicescompany.enumeration.Status;
import com.miroservicescompany.exception.CompanyAccessException;
import com.miroservicescompany.exception.CompanyNotFoundException;
import com.miroservicescompany.exception.CompanyServiceException;
import com.miroservicescompany.exception.EncryptionException;
import com.miroservicescompany.generator.SecretKeyGenerator;
import com.miroservicescompany.repository.CompanyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

	// private final ResponseGenerator responseGenerator;

	private final CompanyRepository companyRepository;

	private final LicenseGenerator licenseGenerator;

	private final EmailSender emailSender;

	private final SecretKeyGenerator encryptionService;

	private final OtpService otpService;

	
	//singup
	 @Transactional
	    public Company createCompany(CompanyDto companyDto) {
	        try {
	            // Build company entity from DTO
	            Company company = Company.builder()
	                    .companyName(companyDto.getCompanyName())
	                    .email(companyDto.getEmail())
	                    .password(companyDto.getPassword())
	                    .address(companyDto.getAddress())
	                    .build();

	            // Generate license
	            String license = licenseGenerator.generateLicense(company);
	            company.setLicense(license);
	            company.setStatus(Status.CREATE);

	            // Save company to get the ID
	            Company savedCompany = companyRepository.save(company);

	            // Generate and send OTP
	            otpService.generateAndSendOtp(savedCompany.getEmail());

	            return savedCompany;
	        } catch (Exception e) {
	            throw new CompanyServiceException("Failed to create company", e);
	        }
	    }
	 
	 //login 
	 
	 public String login(String email, String password) {
	        Company company = companyRepository.findByEmailAndPassword(email, password);

	        if (company != null && company.getEmail().equals(email) && company.getPassword().equals(password)) {
	            return "Login successfully";
	        } else {
	            return "Invalid email or password";
	        }
	    }
	 
	 
	 public void resetPassword(String email, String newPassword) {
	        Company company = companyRepository.findByEmail(email);
	        if (company != null) {
	            company.setPassword(newPassword);
	            companyRepository.save(company);
	        } else {
	            throw new CompanyServiceException("No company found with the provided email");
	        }
	    }

	public EncryptedData encryptEmailLicense(String companyName, String adminEmail, String subject) {
		Company company = companyRepository.findByCompanyName(companyName)
				.orElseThrow(() -> new CompanyNotFoundException("Company not found: " + companyName));
		try {
			// Encrypt email and license
			EncryptedData encryptedData = encryptionService.encrypt(company.getEmail() + ";" + company.getLicense());
			company.setStatus(Status.REQUEST);
			companyRepository.save(company);

			// Decrypt the email and license key for inclusion in the email content
			Encryption decryptedData = encryptionService.decrypt(encryptedData.getEncryptedData(),
					encryptedData.getSecretKey());
			if (decryptedData == null) {
				throw new EncryptionException("Decryption failed for company: " + companyName);
			}

			// Construct the email content
			String emailContent = String.format("Decrypted Data:\nEmail: %s\nLicense Key: %s", decryptedData.getEmail(),
					decryptedData.getLicense());

			emailSender.sendMail(adminEmail, subject, emailContent);
			return encryptedData;
		} catch (Exception e) {
			throw new EncryptionException("Encryption failed for company: " + companyName, e);
		}
	}

	public CompanyDto getLicense(Long id) {
		try {
			Optional<Company> company = companyRepository.findById(id);
			if (company.isPresent()) {
				Company companyObj = company.get();
				return CompanyDto.builder().id(companyObj.getId()).companyName(companyObj.getCompanyName())
						.email(companyObj.getEmail()).address(companyObj.getAddress()).license(companyObj.getLicense())
						.activationDate(companyObj.getActivationDate()).expireDate(companyObj.getExpireDate())
						.status(companyObj.getStatus()).gracePeriod(companyObj.getGracePeriod())
						.graceStatus(companyObj.getGraceStatus()).build();
			} else {
				throw new CompanyNotFoundException("Company not found with ID: " + id);
			}
		} catch (Exception e) {
			throw new CompanyAccessException("Error accessing company data", e);
		}
	}

	public List<CompanyDto> getAllLicense() {
		try {
			List<Company> companies = companyRepository.findAll();
			return companies.stream()
					.map(company -> CompanyDto.builder().id(company.getId()).companyName(company.getCompanyName())
							.email(company.getEmail()).address(company.getAddress()).license(company.getLicense())
							.activationDate(company.getActivationDate()).expireDate(company.getExpireDate())
							.status(company.getStatus()).gracePeriod(company.getGracePeriod())
							.graceStatus(company.getGraceStatus()).build())
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new CompanyAccessException("Error accessing company data", e);
		}
	}

	public String decryptForActivate(Encryption encryption) {
		try {
			if (encryption != null && encryption.getEmail() != null && encryption.getLicense() != null) {
				LocalDate activationDate = LocalDate.now();
				Optional<Company> companyOptional = companyRepository.findByEmailAndLicense(encryption.getEmail(),
						encryption.getLicense());

				if (companyOptional.isPresent()) {
					Company company = companyOptional.get();
					company.setActivationDate(activationDate);
					LocalDate expireDate = activationDate.plusDays(30); // Set expiration date 30 days from activation
					company.setExpireDate(expireDate);
					company.setGraceStatus(GraceStatus.ACTIVE);

					// Calculate grace period
					LocalDate gracePeriodStart = expireDate.plusDays(1);
					LocalDate gracePeriodEnd = gracePeriodStart.plusDays(1);
					company.setGracePeriod(gracePeriodEnd); // Set the end date of the grace period

					company.setStatus(Status.APPROVED);

					companyRepository.save(company);
					return "Company activated successfully";
				} else {
					return "Company not found";
				}
			} else {
				return "Invalid encryption data";
			}
		} catch (Exception e) {
			throw new RuntimeException("Error activating company", e);
		}
	}

}
