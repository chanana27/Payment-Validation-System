package com.cpt.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Configuration;


@Configuration
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class PaymentValidationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentValidationServiceApplication.class, args);
	}
}
