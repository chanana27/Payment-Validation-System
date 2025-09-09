package com.cpt.payments.service.impl.validators;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cpt.payments.cache.ValidationRulesCache;
import com.cpt.payments.constants.ErrorCodeEnum;
import com.cpt.payments.constants.ValidatorEnum;
import com.cpt.payments.dao.MerchantPaymentRequestDao;
import com.cpt.payments.dto.PaymentRequestDTO;
import com.cpt.payments.exception.ValidationException;
import com.cpt.payments.service.interfaces.Validator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentAttemptThresholdCheck implements Validator{
	
	private MerchantPaymentRequestDao merchantPaymentRequestDao;
	private ValidationRulesCache validationRulesCache;
	
	public PaymentAttemptThresholdCheck(
			MerchantPaymentRequestDao merchantPaymentRequestDao,
			ValidationRulesCache validationRulesCache) {
		this.merchantPaymentRequestDao = merchantPaymentRequestDao;
		this.validationRulesCache = validationRulesCache;
	}

	private static final String DURATION_IN_MINS = "durationInMins";
	private static final String MAX_PAYMENT_THRESHOLD = "maxPaymentThreshold";
	
	@Override
	public void validate(PaymentRequestDTO paymentRequestDTO) {
		
		log.info("Validating Payment Request {}" , paymentRequestDTO);
		
		Map<String, String> params = validationRulesCache.getValidationRulesParams(
				ValidatorEnum.PAYMENT_ATTEMPT_THRESHOLD_CHECK.name());
		
		//TODO null or empty check for params. And throw ValidationException
		
		int durationInMins = Integer.parseInt(params.get(DURATION_IN_MINS));
		int maxPaymentThreshold = Integer.parseInt(params.get(MAX_PAYMENT_THRESHOLD));
		
		String endUserId = paymentRequestDTO.getUser().getEndUserID();
		
		int count = merchantPaymentRequestDao.getCountOfPaymentsInLastXMinutes(endUserId, durationInMins);
		

		log.info("Payment attempts in last {} minutes: {} | endUserId:{}", durationInMins, count, endUserId);
		
		if(count > maxPaymentThreshold) {
			throw new ValidationException(
					ErrorCodeEnum.PAYMENT_ATTEMPT_LIMIT_EXCEED.getErrorCode(),
					ErrorCodeEnum.PAYMENT_ATTEMPT_LIMIT_EXCEED.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		
		log.info("PaymentAttemptThresholdCheck passed for endUserId: {}", endUserId);
		
	}
	
}
