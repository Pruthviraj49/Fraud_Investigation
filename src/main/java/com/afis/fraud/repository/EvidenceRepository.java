package com.afis.fraud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.afis.fraud.entity.Evidence;

import java.util.List;

public interface EvidenceRepository
        extends JpaRepository<Evidence, Long> {

    List<Evidence> findByInvestigationId(Long investigationId);
}