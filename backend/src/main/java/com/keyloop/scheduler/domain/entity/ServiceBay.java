package com.keyloop.scheduler.domain.entity;

import java.util.UUID;

import com.keyloop.scheduler.domain.common.SoftDeletableAuditableEntity;

public class ServiceBay extends SoftDeletableAuditableEntity {

    private UUID dealershipId;
    private String bayCode;
    private boolean active;

    protected ServiceBay() {
    }

    private ServiceBay(Builder builder) {
        super(builder);
        this.dealershipId = builder.dealershipId;
        this.bayCode = builder.bayCode;
        this.active = builder.active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
            extends SoftDeletableAuditableEntity.Builder<Builder> {

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

        public ServiceBay build() {
            return new ServiceBay(this);
        }
    }

    public UUID getDealershipId() {
        return dealershipId;
    }

    public String getBayCode() {
        return bayCode;
    }

    public boolean getActive() {
        return active;
    }
}
