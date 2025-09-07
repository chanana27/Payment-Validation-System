package com.cpt.payments.constants;

public enum ErrorCodeEnum {

	 	GENERIC_ERROR("10000", "Something went wrong. Please try again later!"),
	    INVALID_AMOUNT("10001", "Amount cannot be negative. Please correct and try again!"),
		MERCHANT_TXN_REF_EMPTY("10002", "Merchant transaction reference is null or empty."),
		DUPLICATE_MERCHANT_TXN_REF("10003", "Duplicate entry for merchant payment request"),
		PAYMENT_NOT_SAVED("10004", "Unable to save payment in DB, please try again later"),
		PAYMENT_ATTEMPT_LIMIT_EXCEED("10005", "Payment attempt limit reached. Please try again after some time!");

	    private final String errorCode;
	    private final String errorMessage;

	    ErrorCodeEnum(String errorCode, String errorMessage) {
	        this.errorCode = errorCode;
	        this.errorMessage = errorMessage;
	    }

	    public String getErrorCode() {
	        return errorCode;
	    }

	    public String getErrorMessage() {
	        return errorMessage;
	    }

}
