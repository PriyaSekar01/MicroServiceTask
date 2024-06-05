package com.miroservicescompany.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miroservicescompany.entity.OtpDetails;

@Repository
public interface OtpRepository extends JpaRepository<OtpDetails, Long>{

	OtpDetails findByEmail(String email);

}
