package com.cpt.payments.service.impl;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.cpt.payments.cache.ValidationRulesCache;
import com.cpt.payments.constants.PaymentStatus;
import com.cpt.payments.constants.ValidatorEnum;
import com.cpt.payments.dao.ValidationRuleDAO;
import com.cpt.payments.dto.PaymentRequestDTO;
import com.cpt.payments.dto.PaymentResponseDTO;
import com.cpt.payments.exception.ValidationException;
import com.cpt.payments.http.HttpRequest;
import com.cpt.payments.http.HttpServiceEngine;
import com.cpt.payments.pojo.InitiateRequest;
import com.cpt.payments.processing.CreatePaymentReq;
import com.cpt.payments.processing.TransactionRes;
import com.cpt.payments.processing.PaymentProcessingError;
import com.cpt.payments.service.interfaces.PaymentService;
import com.cpt.payments.service.interfaces.Validator;
import com.google.gson.Gson;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

	private ApplicationContext applicationContext;
	private ValidationRuleDAO validatorRuleDao;
	private ValidationRulesCache validationRulesCache;
	private HttpServiceEngine httpServiceEngine;
	private Gson gson;

	@Value("${processing.createPayment.url}")
	private String createPaymentURL;

	@Value("${processing.initiatepayment.url}")
	private String initiatePaymentURL;

	public PaymentServiceImpl(ApplicationContext applicationContext,
			ValidationRuleDAO validatorRuleDao,
			ValidationRulesCache validationRulesCache,
			HttpServiceEngine httpServiceEngine,
			Gson gson) {

		this.applicationContext = applicationContext;
		this.validatorRuleDao = validatorRuleDao;
		this.validationRulesCache = validationRulesCache;
		this.httpServiceEngine = httpServiceEngine;
		this.gson = gson;
	}

	@Override
	public PaymentResponseDTO validateAndInitiatePayment(PaymentRequestDTO paymentRequestDTO) {
		log.info("Received paymentRequestDTO as {}", paymentRequestDTO);

		validationRulesCache.getValidationRulesList().forEach(rule -> {
			triggerValidationRule(paymentRequestDTO, rule);
		}); 


		log.info("Payment Request Validated successfully. All rules passed");

		// Invoke Create Payment API of processing service

		TransactionRes transactionRes = createPayment(paymentRequestDTO);

		// Invoke Initiate Payment API of processing service

		if(transactionRes != null) {

			initiatePayment(paymentRequestDTO, transactionRes);

		}

		PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO(
				"TXN REF 123",
				"https://google.com");

		return paymentResponseDTO;
	}

	private void initiatePayment(PaymentRequestDTO paymentRequestDTO, TransactionRes transactionRes) {
		String txnReference = transactionRes.getTxnReference();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		InitiateRequest initiateRequest = InitiateRequest.builder()
				.firstname(paymentRequestDTO.getUser().getFirstname())
				.lastname(paymentRequestDTO.getUser().getLastname())
				.email(paymentRequestDTO.getUser().getEmail())
				.mobilePhone(paymentRequestDTO.getUser().getMobilePhone())
				.brandName(paymentRequestDTO.getPayment().getBrandName())
				.locale(paymentRequestDTO.getPayment().getLocale())
				.country(paymentRequestDTO.getPayment().getCountry())
				.returnUrl(paymentRequestDTO.getPayment().getReturnUrl())
				.cancelUrl(paymentRequestDTO.getPayment().getCancelUrl())
				.build();

		String requestBody = gson.toJson(initiateRequest);

		URI uri = UriComponentsBuilder
				.fromHttpUrl(initiatePaymentURL)
				.buildAndExpand(txnReference)
				.toUri();

		HttpRequest httpRequest = HttpRequest.builder()
				.url(uri.toString())
				.method(HttpMethod.POST)
				.headers(headers)
				.requestBody(requestBody)
				.build();

		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);

		if(httpResponse.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
			log.info("Initiate Payment Request Success| {}", httpResponse);

			transactionRes = gson.fromJson(httpResponse.getBody(), TransactionRes.class);

			log.info("Response from initiate transaction: {}", transactionRes);
		}else
		{
			log.error("Failure Response from processing service while initiate txn: {}", httpResponse.getBody());

			PaymentProcessingError paymentProcessingError = gson.fromJson(
					httpResponse.getBody(),
					PaymentProcessingError.class);

			throw new ValidationException(
					paymentProcessingError.getErrorCode(),
					paymentProcessingError.getErrorMessage(),
					HttpStatus.valueOf(httpResponse.getStatusCode().value()));
		}
	}

	private TransactionRes createPayment(PaymentRequestDTO paymentRequestDTO) {
		log.info("Create Payment method called");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		CreatePaymentReq paymentRequest = CreatePaymentReq.builder()
				.userId(paymentRequestDTO.getUser().getEndUserID())
				.paymentMethod(paymentRequestDTO.getPayment().getPaymentMethod())
				.provider(paymentRequestDTO.getPayment().getProvider())
				.paymentType(paymentRequestDTO.getPayment().getPaymentType())
				.amount(paymentRequestDTO.getPayment().getAmount())
				.currency(paymentRequestDTO.getPayment().getCurrency())
				.txnStatus(PaymentStatus.CREATED.getName())
				.merchantTransactionReference(paymentRequestDTO.getPayment().getMerchantTxnRef())
				.build();

		String requestBody = gson.toJson(paymentRequest);    

		HttpRequest httpRequest = HttpRequest.builder()
				.url(createPaymentURL)
				.method(HttpMethod.POST)
				.headers(headers)
				.requestBody(requestBody)
				.build();

		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);

		TransactionRes transactionRes = null;
		// handle success response
		if(httpResponse.getStatusCode().isSameCodeAs(HttpStatus.CREATED)) {
			transactionRes = gson.fromJson(httpResponse.getBody(), TransactionRes.class);
			log.info("Success Response from processing service: {}", transactionRes);
		}
		else {
			log.error("Failure Response from processing service: {}", httpResponse);
			PaymentProcessingError paymentProcessingError = gson.fromJson(
					httpResponse.getBody(),
					PaymentProcessingError.class);

			throw new ValidationException(
					paymentProcessingError.getErrorCode(),
					paymentProcessingError.getErrorMessage(),
					HttpStatus.valueOf(httpResponse.getStatusCode().value()));
		}
		return transactionRes;
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
