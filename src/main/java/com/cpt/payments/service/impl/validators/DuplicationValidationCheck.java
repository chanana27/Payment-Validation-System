package com.cpt.payments.service.impl.validators;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cpt.payments.dao.impl.MerchantPaymentRequestDaoImpl;
import com.cpt.payments.dto.PaymentRequestDTO;
import com.cpt.payments.entity.PaymentRequestEntity;
import com.cpt.payments.service.interfaces.Validator;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DuplicationValidationCheck implements Validator {

	private MerchantPaymentRequestDaoImpl merchantPaymentRequestDaoImpl;
	private ModelMapper modelMapper;
	private Gson gson;
	
	public DuplicationValidationCheck(MerchantPaymentRequestDaoImpl merchantPaymentRequestDaoImpl,
			ModelMapper modelMapper, Gson gson) {
		this.merchantPaymentRequestDaoImpl = merchantPaymentRequestDaoImpl;
		this.modelMapper = modelMapper;
		this.gson = gson;
	}
	
	@Override
	public void validate(PaymentRequestDTO paymentRequestDTO) {
		log.info("Validating payment request: {}", paymentRequestDTO);
		
		PaymentRequestEntity entity = modelMapper.map(paymentRequestDTO, PaymentRequestEntity.class);
		entity.setCreationDate(LocalDateTime.now());
		entity.setTransactionRequest(gson.toJson(paymentRequestDTO));
	
		merchantPaymentRequestDaoImpl.insertPaymentIntoDB(entity);
		
		
		log.info("ValidationRule2 | Payment request validated successfully");
		
	}
}
