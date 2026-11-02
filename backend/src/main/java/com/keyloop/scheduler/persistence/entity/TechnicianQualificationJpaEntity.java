package com.keyloop.scheduler.persistence.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "technician_qualification")
public class TechnicianQualificationJpaEntity {

    @EmbeddedId
    private TechnicianQualificationId id;

    @Column(
            name = "qualified_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Instant qualifiedAt;

    protected TechnicianQualificationJpaEntity() {
    }

    private TechnicianQualificationJpaEntity(Builder builder) {
        this.id = builder.id;
        this.qualifiedAt = builder.qualifiedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private TechnicianQualificationId id;
        private Instant qualifiedAt;

        public Builder id(TechnicianQualificationId id) {
            this.id = id;
            return this;
        }

        public Builder qualifiedAt(Instant qualifiedAt) {
            this.qualifiedAt = qualifiedAt;
            return this;
        }

        public TechnicianQualificationJpaEntity build() {
            return new TechnicianQualificationJpaEntity(this);
        }
    }

    public TechnicianQualificationId getId() {
        return id;
    }

    public Instant getQualifiedAt() {
        return qualifiedAt;
    }
}
