package com.keyloop.scheduler.domain.entity;

import com.keyloop.scheduler.domain.common.SoftDeletableAuditableEntity;

import java.util.UUID;

public class Vehicle extends SoftDeletableAuditableEntity {

    private UUID customerId;
    private String registrationNumber;
    private String make;
    private String model;
    private Short modelYear;

    protected Vehicle() {
    }

    private Vehicle(Builder builder) {
        super(builder);

        this.customerId = builder.customerId;
        this.registrationNumber = builder.registrationNumber;
        this.make = builder.make;
        this.model = builder.model;
        this.modelYear = builder.modelYear;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
            extends SoftDeletableAuditableEntity.Builder<Builder> {

        private UUID customerId;
        private String registrationNumber;
        private String make;
        private String model;
        private Short modelYear;

        public Builder customerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder registrationNumber(String registrationNumber) {
            this.registrationNumber = registrationNumber;
            return this;
        }

        public Builder make(String make) {
            this.make = make;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder modelYear(Short modelYear) {
            this.modelYear = modelYear;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Vehicle build() {
            return new Vehicle(this);
        }
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public Short getModelYear() {
        return modelYear;
    }
}
