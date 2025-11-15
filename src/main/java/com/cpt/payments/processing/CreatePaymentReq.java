package com.cpt.payments.processing;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePaymentReq {
    private String userId;

    private String paymentMethod;
    private String provider;
    private String paymentType;

    private Double amount;
    private String currency;

    private String txnStatus;

    private String merchantTransactionReference;
}
