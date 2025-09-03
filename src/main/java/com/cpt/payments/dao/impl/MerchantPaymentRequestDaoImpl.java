package com.cpt.payments.dao.impl;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cpt.payments.constants.ErrorCodeEnum;
import com.cpt.payments.dao.MerchantPaymentRequestDao;
import com.cpt.payments.entity.PaymentRequestEntity;
import com.cpt.payments.exception.ValidationException;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class MerchantPaymentRequestDaoImpl implements MerchantPaymentRequestDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	public MerchantPaymentRequestDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
	
	@Override
	public int insertPaymentIntoDB(PaymentRequestEntity entity) {
		
		 String sql = "INSERT INTO merchant_payment_request " +
                 "(endUserID, merchantTransactionReference, transactionRequest, creationDate) " +
                 "VALUES (:endUserID, :merchantTransactionReference, :transactionRequest, :creationDate)";	
		 
		 MapSqlParameterSource params = new MapSqlParameterSource()
	                .addValue("endUserID", entity.getEndUserID())
	                .addValue("merchantTransactionReference", entity.getMerchantTransactionReference())
	                .addValue("transactionRequest", entity.getTransactionRequest())
	                .addValue("creationDate", entity.getCreationDate());
		 
		 int result;
		 try {
			 result = jdbcTemplate.update(sql, params); 
		 }catch (Exception e) {
			 throw new ValidationException(
						ErrorCodeEnum.DUPLICATE_TXN.getErrorCode(), 
						ErrorCodeEnum.DUPLICATE_TXN.getErrorMessage(),
						HttpStatus.BAD_REQUEST);
		 }
		 
		 log.info("Payment created in DB");
		 return result;
	}

}
