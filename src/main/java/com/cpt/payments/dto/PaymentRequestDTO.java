package com.cpt.payments.dto;

import lombok.Data;

@Data
public class PaymentRequestDTO {
	String currency;
	Double amount;
	
}
