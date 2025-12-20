package com.cpt.payments.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cpt.payments.pojo.PaymentRequest;
import com.cpt.payments.service.interfaces.HmacVerification;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HmacVerificationImpl implements HmacVerification {
	
	@Value("${topsecretkey}")
	private String secret;
	private final String HMAC_ALGO = "HmacSHA256";
    
	public Boolean isRequestValid(String hmac, String data) {
		
		// HMAC was not received
		if(hmac.trim().isEmpty() || hmac == null)
			return false;
		
		log.info("data to be used for hmac generation is {}", data);
		
		String generatedHMAC = generateHmacSHA256(secret, data);
		log.info("Generated hmac {}", generatedHMAC);
		
		if(generatedHMAC != null && generatedHMAC.equals(hmac)) {
			log.info("HMAC-SHA256 Signature is valid");
			return true;
		}
		
		return false;
	}
	
	public String generateHmacSHA256(String secret, String data) {
		try {
			
		  Mac sha256_HMAC = Mac.getInstance(HMAC_ALGO);
          SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGO);
          sha256_HMAC.init(secretKey);

    
          byte[] hashBytes = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
	
          // Encode HMAC to Base64
          return Base64.getEncoder().encodeToString(hashBytes);

    } catch (Exception e) {
        throw new RuntimeException("Failed to generate HMAC", e);
	}
}
}
