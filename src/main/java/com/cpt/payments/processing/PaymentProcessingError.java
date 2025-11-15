package com.cpt.payments.processing;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentProcessingError {
	
	String errorCode;
	String errorMessage;
}
