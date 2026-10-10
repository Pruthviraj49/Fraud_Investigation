package com.afis.fraud.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "investigation_evidence")
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "investigation_id", nullable = false)
    private Investigation investigation;

    @Enumerated(EnumType.STRING)
    @Column(name = "evidence_type", nullable = false)
    private EvidenceType evidenceType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected Evidence() {
    }

    private Evidence(
            Investigation investigation,
            EvidenceType evidenceType,
            String description) {

        this.investigation = investigation;
        this.evidenceType = evidenceType;
        this.description = description;
        this.createdAt = OffsetDateTime.now();
    }

    public static Evidence create(
            Investigation investigation,
            EvidenceType evidenceType,
            String description) {

        return new Evidence(
                investigation,
                evidenceType,
                description
        );
    }

    public Long getId() {
        return id;
    }

    public EvidenceType getEvidenceType() {
        return evidenceType;
    }

    public String getDescription() {
        return description;
    }
}