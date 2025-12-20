package com.cpt.payments.security;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cpt.payments.constants.Constants;
import com.cpt.payments.constants.ErrorCodeEnum;
import com.cpt.payments.exception.ValidationException;
import com.cpt.payments.service.impl.HmacVerificationImpl;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HmacFilter extends OncePerRequestFilter {
	
	public ApplicationContext context;
	public HmacFilter(ApplicationContext context) {
		this.context = context;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain)
			throws ServletException, IOException {
		
		log.info("HMAC filter invoked");
		
		// Wrapped the incoming request
		WrappedRequest wrappedRequest = new WrappedRequest(request);
		String data = getNormalizedJson( wrappedRequest.getBody());
		
		
		String receivedHmacSignature = request.getHeader(Constants.HMAC_HEADER);
		log.info("Received HMAC Signature {}", receivedHmacSignature);
		
		if(receivedHmacSignature == null || receivedHmacSignature.isBlank()) {
			throw new ValidationException(
					ErrorCodeEnum.HMAC_INVALID.getErrorCode(), 
					ErrorCodeEnum.HMAC_INVALID.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		
		boolean isHmacVerified = false;
		HmacVerificationImpl hmacVerification = context.getBean(HmacVerificationImpl.class);
		
		if(hmacVerification == null)
			log.error("Bean not found for HamcVerificationImpl");
		
		isHmacVerified = hmacVerification.isRequestValid(receivedHmacSignature, data);
		
		if(isHmacVerified) {
			log.info("HMAC Verified");
			markAsAuthenticated();
			filterChain.doFilter(wrappedRequest, response);
			return;
		}
		
		throw new ValidationException(
				ErrorCodeEnum.AUTHENTICATION_FAILED.getErrorCode(),
				ErrorCodeEnum.AUTHENTICATION_FAILED.getErrorMessage(),
				HttpStatus.UNAUTHORIZED);
		
	}

	private void markAsAuthenticated() {
		SecurityContext context = SecurityContextHolder.createEmptyContext(); 
		Authentication authentication =
		    new HmacAuthenticationToken("ECOMM", ""); 
		context.setAuthentication(authentication);

		SecurityContextHolder.setContext(context);
	}
	
	public String getNormalizedJson(String rawJson) {
	    Gson gson = new Gson();
	    // Parse the raw JSON string
	    JsonElement jsonElement = JsonParser.parseString(rawJson);
	    // Convert it back to a JSON string
	    return gson.toJson(jsonElement);
	}
		
}
