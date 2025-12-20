package com.cpt.payments.service.interfaces;

public interface HmacVerification {
	
	public Boolean isRequestValid(String hmac, String data);
}
