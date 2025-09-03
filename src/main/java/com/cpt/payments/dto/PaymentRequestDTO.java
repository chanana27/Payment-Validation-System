package com.cpt.payments.dto;

import com.cpt.payments.pojo.Payment;
import com.cpt.payments.pojo.User;

import lombok.Data;

@Data
public class PaymentRequestDTO {
	private User user;
    private Payment payment;
	
}
