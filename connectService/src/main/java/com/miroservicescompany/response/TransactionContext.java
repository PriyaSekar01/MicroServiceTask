package com.miroservicescompany.response;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionContext {
 
	 private String correlationId;
	 private String applicationLabel;
}
