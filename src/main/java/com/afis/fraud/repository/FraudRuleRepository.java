package com.afis.fraud.repository;

import com.afis.fraud.entity.FraudRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FraudRuleRepository extends JpaRepository<FraudRule, Long> {

    List<FraudRule> findByEnabledTrue();
}