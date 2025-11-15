package com.cpt.payments.http;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class HttpServiceEngineTest {

    @Test
    public void createTransactionTest(){
        log.info("Test case for calling to processing service ");
//        HttpServiceEngine engine = new HttpServiceEngine(new RestTemplate() ,new Gson());
//        engine.makeHttpCall("http://localhost:8082/v1/payments/create",
//                 HttpMethod.POST);
        log.info("Test case ran successfully");
    }
}
