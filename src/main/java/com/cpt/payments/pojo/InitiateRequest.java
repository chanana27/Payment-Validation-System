package com.cpt.payments.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InitiateRequest {
	private String firstname;
	private String lastname;
	private String email;
	private String mobilePhone;
	private String brandName;
	private String locale;
	private String country;
	private String returnUrl;
	private String cancelUrl;
}
