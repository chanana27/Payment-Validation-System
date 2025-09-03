package com.cpt.payments.dao;

import com.cpt.payments.entity.PaymentRequestEntity;

public interface MerchantPaymentRequestDao {
	
	public int insertPaymentIntoDB(PaymentRequestEntity entity);
}
