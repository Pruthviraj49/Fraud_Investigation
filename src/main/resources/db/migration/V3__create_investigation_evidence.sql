CREATE TABLE investigation_evidence (
    id BIGSERIAL PRIMARY KEY,
    investigation_id BIGINT NOT NULL,
    evidence_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_investigation_evidence
        FOREIGN KEY (investigation_id)
        REFERENCES investigations(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_evidence_investigation
ON investigation_evidence(investigation_id);