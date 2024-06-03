package com.miroservicescompany.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miroservicescompany.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>{

	Optional<Company> findByCompanyName(String companyName);

	Optional<Company> findByEmailAndLicense(String email, String license);

	

}
