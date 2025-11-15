package com.cpt.payments.http;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.cpt.payments.constants.ErrorCodeEnum;
import com.cpt.payments.exception.ValidationException;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HttpServiceEngine {

    private RestTemplate restTemplate;
    private Gson gson;

    public HttpServiceEngine(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    public ResponseEntity<String> makeHttpCall(HttpRequest httpRequest){
    	
    	log.info("HttpRequest in httpServiceEngine as: {}", httpRequest);
        try {
        	HttpEntity<String> requestEntity = new HttpEntity<>(httpRequest.getRequestBody(), httpRequest.getHeaders());
        	
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    httpRequest.getUrl(),
                    httpRequest.getMethod(),
                    requestEntity,
                    String.class);
            	
            log.info("Response from processing service: {}", responseEntity);
            return responseEntity;

        } catch(HttpClientErrorException | HttpServerErrorException e){
        	
            log.error("Error occurred while making HTTP Call:{}", e);
            
           return ResponseEntity.status(e.getStatusCode())
    		.body(e.getResponseBodyAsString());
        }
        catch(Exception e) {
        	log.error("Error occurred while making HTTP call: {}", e);
        	throw new ValidationException(
        			ErrorCodeEnum.SERVICE_UNAVAILABLE.getErrorCode(),
        			ErrorCodeEnum.SERVICE_UNAVAILABLE.getErrorMessage(),
        			HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}

