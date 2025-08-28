package com.cpt.payments.constants;

public enum ErrorCodeEnum {

	 	GENERIC_ERROR("10000", "Something went wrong. Please try again later!"),
	    INVALID_AMOUNT("10001", "Amount cannot be negative. Please correct and try again!");

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
