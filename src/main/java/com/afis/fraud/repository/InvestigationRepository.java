package com.afis.fraud.repository;

import com.afis.fraud.entity.Investigation;
import com.afis.fraud.entity.InvestigationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestigationRepository extends JpaRepository<Investigation, Long> {

    List<Investigation> findByStatus(InvestigationStatus status);

    boolean existsByTransactionId(Long transactionId);
}



