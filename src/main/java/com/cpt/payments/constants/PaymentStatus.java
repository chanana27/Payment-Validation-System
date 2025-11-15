package com.cpt.payments.constants;

public enum PaymentStatus {

	CREATED("CREATED"),
	INITIATED("INITIATED");
	
	private final String name;
	
	private PaymentStatus(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
