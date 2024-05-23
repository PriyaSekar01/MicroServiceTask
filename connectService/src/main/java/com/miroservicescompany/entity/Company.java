package com.miroservicescompany.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;

import com.miroservicescompany.enumeration.GraceStatus;
import com.miroservicescompany.enumeration.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name="company")
public class Company {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    @Column(name="id")
	    private Long id;

	    @Column(name="company_name")
	    private String companyName;

	    @Column(name="email")
	    private String email;
	    
	    @Column(name="address")
	    private String address;

	    @Column(name="grace_period")
	    private LocalDate gracePeriod;
	    

	    @Column(name = "status")
	    @Enumerated(EnumType.STRING)
	    private Status status;
	    
	    @Column(name="grace_status")
	    @Enumerated(EnumType.STRING)
	    private GraceStatus graceStatus;
	    

	    @CreatedDate
	    @Column(name = "activation_date", updatable = true)
	    private LocalDate activationDate;

	    @Column(name="expire_date")
	    private LocalDate expireDate;

	    @Column(name="license")
	    private String license;
	    

}
