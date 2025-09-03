 package com.cpt.payments.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cpt.payments.dto.PaymentRequestDTO;
import com.cpt.payments.entity.PaymentRequestEntity;

@Configuration
public class AppConfig {

    @Bean
    ModelMapper getModelMapper() {
		
    	ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(PaymentRequestDTO.class, PaymentRequestEntity.class)
            .addMapping(src -> src.getUser().getEndUserID(),
                        PaymentRequestEntity::setEndUserID)
            .addMapping(src -> src.getPayment().getMerchantTxnRef(),
                        PaymentRequestEntity::setMerchantTransactionReference);

        return modelMapper;
	}
}
