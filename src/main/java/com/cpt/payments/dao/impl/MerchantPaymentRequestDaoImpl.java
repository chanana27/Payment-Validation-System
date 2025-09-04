package com.cpt.payments.dao.impl;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cpt.payments.constants.MerchantReqUpdate;
import com.cpt.payments.dao.MerchantPaymentRequestDao;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class MerchantPaymentRequestDaoImpl implements MerchantPaymentRequestDao {

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public MerchantPaymentRequestDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@Override
	public MerchantReqUpdate insertMerchantPaymentRequest(String endUserID, String merchantTransactionReference,
			String transactionRequest) {

		log.debug(
				"Inserting merchant payment request in DB endUserId:{}"
						+ "|merchantTransactionReference:{}"
						+ "|transactionRequest:{}",
						endUserID, merchantTransactionReference, transactionRequest);

		String sql = "INSERT INTO merchant_payment_request " +
				"(endUserID, merchantTransactionreference, transactionRequest) " +
				"VALUES (:endUserID, :merchantTransactionReference, :transactionRequest)";	

		log.info("Inserting merchant payment request in DB: {}", sql);

		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("endUserID", endUserID)
				.addValue("merchantTransactionReference", merchantTransactionReference)
				.addValue("transactionRequest", transactionRequest);

		try {
            int insertedRow = namedParameterJdbcTemplate.update(sql, params);

            log.info("Merchant payment request inserted in DB. Rows inserted: {}", insertedRow);
            return (insertedRow == 1 ? MerchantReqUpdate.SAVED : MerchantReqUpdate.ERROR);
        } catch (DuplicateKeyException e) {
            log.error("Error occurred while inserting merchant payment request in DB {}", e.getMessage());
            return MerchantReqUpdate.DUPLICATE;
		} catch (Exception e) {
			log.error("Error occurred while inserting merchant payment request in DB {}", e.getMessage());
			return MerchantReqUpdate.ERROR;
		}
	}

}
