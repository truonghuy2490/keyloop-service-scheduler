package com.keyloop.scheduler.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.keyloop.scheduler.persistence.entity.common.SoftDeletedAuditableJpaEntity;

@Entity
@Table(name = "technician")
public class TechnicianJpaEntity extends SoftDeletedAuditableJpaEntity {

    @Column(name = "dealership_id", nullable = false)
    private UUID dealershipId;

    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected TechnicianJpaEntity() {
    }

    private TechnicianJpaEntity(Builder builder) {
        super(builder);
        this.dealershipId = builder.dealershipId;
        this.fullName = builder.fullName;
        this.active = builder.active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
            extends SoftDeletedAuditableJpaEntity.Builder<Builder> {

        private UUID dealershipId;
        private String fullName;
        private boolean active;

        public Builder dealershipId(UUID dealershipId) {
            this.dealershipId = dealershipId;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public TechnicianJpaEntity build() {
            return new TechnicianJpaEntity(this);
        }
    }

    public UUID getDealershipId() {
        return dealershipId;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean isActive() {
        return active;
    }
}
