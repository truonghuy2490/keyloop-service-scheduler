package com.keyloop.scheduler.domain.entity;

import java.util.UUID;

import com.keyloop.scheduler.domain.common.SoftDeletableAuditableEntity;

public class Technician extends SoftDeletableAuditableEntity {

    private UUID dealershipId;
    private String fullName;
    private boolean active;

    protected Technician() {
    }

    private Technician(Builder builder) {
        super(builder);
        this.dealershipId = builder.dealershipId;
        this.fullName = builder.fullName;
        this.active = builder.active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
            extends SoftDeletableAuditableEntity.Builder<Builder> {

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

        public Technician build() {
            return new Technician(this);
        }
    }

    public UUID getDealershipId() {
        return dealershipId;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean getActive() {
        return active;
    }
}
