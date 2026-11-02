package com.keyloop.scheduler.persistence.entity;

import com.keyloop.scheduler.persistence.entity.common.SoftDeletedAuditableJpaEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "vehicle")
public class VehicleJpaEntity extends SoftDeletedAuditableJpaEntity {

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "registration_number", nullable = false, length = 32)
    private String registrationNumber;

    @Column(name = "make", nullable = false, length = 100)
    private String make;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "model_year")
    private Short modelYear;

    protected VehicleJpaEntity() {
    }

    private VehicleJpaEntity(Builder builder) {
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
            extends SoftDeletedAuditableJpaEntity.Builder<Builder> {

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

        public VehicleJpaEntity build() {
            return new VehicleJpaEntity(this);
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
