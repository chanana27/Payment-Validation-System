package com.cpt.payments.service.impl.validators;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cpt.payments.constants.ErrorCodeEnum;
import com.cpt.payments.dto.PaymentRequestDTO;
import com.cpt.payments.exception.ValidationException;
import com.cpt.payments.service.interfaces.Validator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ValidatorRule1 implements Validator {

	@Override
	public void validate(PaymentRequestDTO paymentRequestDTO) {
		log.info("Validating payment request: {}", paymentRequestDTO);
		
		if(paymentRequestDTO.getAmount() < 0) {
			log.error("ValidatorRule1| Payment request validation failed. Amount cannot be negative");
			
			ValidationException validationException = new ValidationException(
				ErrorCodeEnum.INVALID_AMOUNT.getErrorCode(), 
				ErrorCodeEnum.INVALID_AMOUNT.getErrorMessage(),
				HttpStatus.BAD_REQUEST);
			
			log.error("Raising Validation Exception {}", validationException);
			
			throw validationException;
			
		}
		
		log.info("ValidationRule1 | Payment request validated successfully");
		
	}

}
