package com.keyloop.scheduler.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.keyloop.scheduler.persistence.entity.common.SoftDeletedAuditableJpaEntity;

@Entity
@Table(name = "service_bay")
public class ServiceBayJpaEntity extends SoftDeletedAuditableJpaEntity {

    @Column(name = "dealership_id", nullable = false)
    private UUID dealershipId;

    @Column(name = "bay_code", nullable = false, length = 40)
    private String bayCode;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected ServiceBayJpaEntity() {
    }

    private ServiceBayJpaEntity(Builder builder) {
        super(builder);
        this.dealershipId = builder.dealershipId;
        this.bayCode = builder.bayCode;
        this.active = builder.active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
            extends SoftDeletedAuditableJpaEntity.Builder<Builder> {

        private UUID dealershipId;
        private String bayCode;
        private boolean active;

        public Builder dealershipId(UUID dealershipId) {
            this.dealershipId = dealershipId;
            return this;
        }

        public Builder bayCode(String bayCode) {
            this.bayCode = bayCode;
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

        public ServiceBayJpaEntity build() {
            return new ServiceBayJpaEntity(this);
        }
    }

    public UUID getDealershipId() {
        return dealershipId;
    }

    public String getBayCode() {
        return bayCode;
    }

    public boolean isActive() {
        return active;
    }
}
