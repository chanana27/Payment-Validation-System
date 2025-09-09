package com.cpt.payments.cache;

import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.cpt.payments.dao.ValidationRuleDAO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ValidationRulesCache {
	
	private final RedisTemplate<String, String> redisTemplate;
	
	private final ListOperations<String, String> listOperations;
	private final HashOperations<String, String, String> hashOperations;

	private ValidationRuleDAO validationRuleDAO;

	private static final String VALIDATION_RULES_KEY = "validationRules";
	private static final String VALIDATION_RULES_PARAM_KEY_PREFIX = "validationRulesParams:";

	public ValidationRulesCache(RedisTemplate<String, String> redisTemplate, 
			ValidationRuleDAO validationRuleDAO) {
		this.redisTemplate = redisTemplate;
		this.listOperations = redisTemplate.opsForList();
		this.hashOperations = redisTemplate.opsForHash();
		this.validationRuleDAO = validationRuleDAO;
	}
	
	public List<String> getValidationRulesList(){
		
		//1 check for data in redis
		List<String> validationRules = listOperations.range(VALIDATION_RULES_KEY, 0, -1);
		
		//2 if found return it
		if(validationRules != null && !validationRules.isEmpty()) {
			return validationRules;
		}
		
		//3 if not found get the data from DB
		validationRules = validationRuleDAO.loadActiveValidatorNames();
		
		//4 add the rules in redis and return them
		if(validationRules != null && !validationRules.isEmpty()) {
			addValidationRulesList(validationRules);
		}
		return validationRules;
	}  
	
	public Map<String, String> getValidationRulesParams(String validationRule){
		
		 Map<String, String> validationRuleParams = hashOperations.entries(
				 VALIDATION_RULES_PARAM_KEY_PREFIX + validationRule);
		 
		 if(validationRuleParams != null && !validationRuleParams.isEmpty()) {
			 return validationRuleParams;
		 }
		 
		 validationRuleParams = validationRuleDAO.loadValidatorRuleParams(validationRule);
		 
		 if(validationRuleParams != null && !validationRuleParams.isEmpty()) {
			 addValidationRulesParams(validationRule, validationRuleParams);
		 }
		 
		 return validationRuleParams;
		 
	}

	public void addValidationRulesList(List<String> rules) {
		
		listOperations.rightPushAll(VALIDATION_RULES_KEY, rules);
	}
	
	public void addValidationRulesParams(String validatorRuleName,
			Map<String, String> params) {
		
		hashOperations.putAll(VALIDATION_RULES_PARAM_KEY_PREFIX + validatorRuleName, params);
	}
	
	public void loadValidatorRulesAndParams() {
		List<String> validationRulesList = getValidationRulesList();
		log.info("Loaded Validator Rules {} ", validationRulesList);
		
		validationRulesList.forEach(
				rule -> {
					Map<String, String> params = getValidationRulesParams(rule);
					log.info("Loaded Rule {} | params {} ", rule, params);
				}); 
	}
	
	public void resetValidationRulesAndParams() {
		redisTemplate.delete(VALIDATION_RULES_KEY);
		
		redisTemplate.keys(VALIDATION_RULES_PARAM_KEY_PREFIX + "*")
			.forEach(key -> redisTemplate.delete(key));
	}
	
}
