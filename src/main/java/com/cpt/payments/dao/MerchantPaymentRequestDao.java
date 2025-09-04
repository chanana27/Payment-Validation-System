package com.cpt.payments.dao;

import com.cpt.payments.constants.MerchantReqUpdate;

public interface MerchantPaymentRequestDao {
	
	public MerchantReqUpdate insertMerchantPaymentRequest(String endUserID, 
            String merchantTransactionReference, 
            String transactionRequest);
}
