package com.cpt.payments.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpt.payments.constants.EndPoints;
import com.cpt.payments.dto.PaymentRequestDTO;
import com.cpt.payments.dto.PaymentResponseDTO;
import com.cpt.payments.pojo.PaymentRequest;
import com.cpt.payments.pojo.PaymentResponse;
import com.cpt.payments.service.interfaces.PaymentService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndPoints.V1_PAYMENTS)
@Slf4j
public class PaymentController {
	
	private PaymentService paymentService;
	private ModelMapper modelMapper;
	
	public PaymentController(PaymentService paymentService, ModelMapper modelMapper) {
		this.paymentService = paymentService;
		this.modelMapper = modelMapper;
	}
	

	@PostMapping("/add")
	public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest) {
		
		log.info("Received Create Payment Request as {}", paymentRequest);
		
		PaymentRequestDTO paymentRequestDTO = modelMapper.map(paymentRequest, PaymentRequestDTO.class);
		
		log.info("Converted paymentRequest to paymentRequestDTO {}", paymentRequestDTO);
		
		PaymentResponseDTO result = paymentService.validateAndInitiatePayment(paymentRequestDTO);
		
		log.info("Received result from service as {}", result);
		
		PaymentResponse paymentRes = modelMapper.map(result, PaymentResponse.class);
		
		log.info("Returning response from controller as {}", paymentRes);
		
		return new ResponseEntity<>(paymentRes, HttpStatus.CREATED);
	}
}
