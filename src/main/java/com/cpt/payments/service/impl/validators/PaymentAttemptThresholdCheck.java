package com.cpt.payments.service.impl.validators;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cpt.payments.constants.ErrorCodeEnum;
import com.cpt.payments.dao.MerchantPaymentRequestDao;
import com.cpt.payments.dto.PaymentRequestDTO;
import com.cpt.payments.exception.ValidationException;
import com.cpt.payments.service.interfaces.Validator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentAttemptThresholdCheck implements Validator{
	
	private MerchantPaymentRequestDao merchantPaymentRequestDao;
	
	public PaymentAttemptThresholdCheck(MerchantPaymentRequestDao merchantPaymentRequestDao) {
		this.merchantPaymentRequestDao = merchantPaymentRequestDao;
	}

	int threshold = 5;
	@Override
	public void validate(PaymentRequestDTO paymentRequestDTO) {
		
		log.info("Validating Payment Request {}" , paymentRequestDTO);
		
		String endUserId = paymentRequestDTO.getUser().getEndUserID();
		int minutes = 4;
		
		int count = merchantPaymentRequestDao.getCountOfPaymentsInLastXMinutes(endUserId, minutes);
		
		if(count > threshold) {
			throw new ValidationException(
					ErrorCodeEnum.PAYMENT_ATTEMPT_LIMIT_EXCEED.getErrorCode(),
					ErrorCodeEnum.PAYMENT_ATTEMPT_LIMIT_EXCEED.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		
		log.info("PaymentAttemptThresholdCheck| Rule passed successfully");
		
	}
	
}
