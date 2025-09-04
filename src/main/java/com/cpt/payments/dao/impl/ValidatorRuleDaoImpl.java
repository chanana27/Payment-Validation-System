package com.cpt.payments.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cpt.payments.dao.ValidatorRuleDao;

@Repository
public class ValidatorRuleDaoImpl implements ValidatorRuleDao {
	
	  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	  
	  public ValidatorRuleDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		  this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	  }
	  

	@Override
	public List<String> loadActiveValidatorNames() {
		
		String sql = "SELECT validatorName FROM validations.validation_rules "
				+ "WHERE isActive=TRUE "
				+ "ORDER BY priority ASC"; 
		
		return namedParameterJdbcTemplate.queryForList(sql, new java.util.HashMap<>(), String.class);
		
	}
}
