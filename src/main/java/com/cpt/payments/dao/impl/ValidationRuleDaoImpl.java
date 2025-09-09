package com.cpt.payments.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cpt.payments.dao.ValidationRuleDAO;

@Repository
public class ValidationRuleDaoImpl implements ValidationRuleDAO {
	
	  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	  
	  public ValidationRuleDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		  this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	  }

	@Override
	public List<String> loadActiveValidatorNames() {
		
		String sql = "SELECT validatorName FROM validations.validation_rules "
				+ "WHERE isActive=TRUE "
				+ "ORDER BY priority ASC"; 
		
		return namedParameterJdbcTemplate.queryForList(sql, new java.util.HashMap<>(), String.class);
		
	}
	
	@Override
	public Map<String, String> loadValidatorRuleParams(String validatorName) {
		String sql = "SELECT paramName, paramValue FROM validation_rules_params WHERE validatorName = :validatorName";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("validatorName", validatorName);

        return namedParameterJdbcTemplate.query(sql, params, rs -> {
            Map<String, String> resultMap = new java.util.HashMap<>();
            while (rs.next()) {
                resultMap.put(rs.getString("paramName"), rs.getString("paramValue"));
            }
            return resultMap;
        }); 
	}
}
