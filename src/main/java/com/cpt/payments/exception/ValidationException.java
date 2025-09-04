package com.cpt.payments.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException{
	
	private static final long serialVersionUID = 805448315017754210L;
	private final String errorCode;
	private final String errorMessage;
	private final HttpStatus httpStatus;
	
	public ValidationException(String errorCode, String errorMessage, HttpStatus httpStatus) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.httpStatus = httpStatus;
	} 
	
}
