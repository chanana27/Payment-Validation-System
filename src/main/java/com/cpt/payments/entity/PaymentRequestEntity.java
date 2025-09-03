package com.cpt.payments.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PaymentRequestEntity {
	
	private Integer id;
    private String endUserID;
    private String merchantTransactionReference;
    private String transactionRequest; // store raw JSON request
    private LocalDateTime creationDate;
		
}
