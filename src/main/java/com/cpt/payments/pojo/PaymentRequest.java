package com.cpt.payments.pojo;

import lombok.Data;

@Data
public class PaymentRequest {
	String currency;
	Double amount;
}
