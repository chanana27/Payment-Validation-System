package com.cpt.payments.dao;

import java.util.List;

public interface ValidatorRuleDao {
	List<String> loadActiveValidatorNames();
}
