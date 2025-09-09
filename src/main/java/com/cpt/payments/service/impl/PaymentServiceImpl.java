package com.cpt.payments.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.cpt.payments.cache.ValidationRulesCache;
import com.cpt.payments.constants.ValidatorEnum;
import com.cpt.payments.dao.ValidationRuleDAO;
import com.cpt.payments.dto.PaymentRequestDTO;
import com.cpt.payments.dto.PaymentResponseDTO;
import com.cpt.payments.service.interfaces.PaymentService;
import com.cpt.payments.service.interfaces.Validator;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
	
	@Value("${validator.rules}")
	String validatorRules;
	
	private ApplicationContext applicationContext;
	private ValidationRuleDAO validatorRuleDao;
	private ValidationRulesCache validationRulesCache;
	
	
	public PaymentServiceImpl(ApplicationContext applicationContext,
			ValidationRuleDAO validatorRuleDao,
			ValidationRulesCache validationRulesCache) {
		this.applicationContext = applicationContext;
		this.validatorRuleDao = validatorRuleDao;
		this.validationRulesCache = validationRulesCache;
	}
	
	@Override
	public PaymentResponseDTO validateAndInitiatePayment(PaymentRequestDTO paymentRequestDTO) {
		log.info("Received paymentRequestDTO as {}", paymentRequestDTO);
		
		validationRulesCache.getValidationRulesList().forEach(rule -> {
			triggerValidationRule(paymentRequestDTO, rule);
		}); 
		
		
		log.info("Payment Request Validated successfully. All rules passed");
		
		//TODO: invoke processing service for further processing payment request
		// expect some txnId and redirectURL from processing service. 
		
		String txnId = "TX123";
		String redirectUrl = "https://google.com";
		
		PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO(txnId, redirectUrl);
		
		return paymentResponseDTO;
	}

	private String triggerValidationRule(PaymentRequestDTO paymentRequestDTO, String rule) {
		rule = rule.trim();
		log.info("Validating payment request using rule {}", rule);
		
		Validator validator = null;
		
		Class<? extends Validator> validatorClass = ValidatorEnum.getClassByName(rule);
		
		if(validatorClass!=null) {
			validator = applicationContext.getBean(validatorClass);
			
			if(validator != null) {
				log.info("Calling validator rule {}", rule);
				validator.validate(paymentRequestDTO);
			} 
		}
		
		if(validatorClass == null || validator == null) {
			log.error("Either ValidatorClass not found or Validator Instance not found"
					+ " for rule " + "rule: {} validatorClass: {} validator: {}", rule,
					validatorClass, validator);
		}
		return rule;
	} 
	
	@PostConstruct
	private void loadActiveValidationRules(){
		validationRulesCache.loadValidatorRulesAndParams();
		log.info("Loaded validator rules from cache");
	}
}
