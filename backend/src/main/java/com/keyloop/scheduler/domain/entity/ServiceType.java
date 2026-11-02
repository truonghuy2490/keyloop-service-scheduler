package com.keyloop.scheduler.domain.entity;

import com.keyloop.scheduler.domain.common.SoftDeletableAuditableEntity;

public class ServiceType extends SoftDeletableAuditableEntity {

    private String name;
    private String description;
    private Integer durationMinutes;

    protected ServiceType() {
    }

    private ServiceType(Builder builder) {
        super(builder);
        this.name = builder.name;
        this.description = builder.description;
        this.durationMinutes = builder.durationMinutes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
            extends SoftDeletableAuditableEntity.Builder<Builder> {

        private String name;
        private String description;
        private Integer durationMinutes;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder durationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public ServiceType build() {
            return new ServiceType(this);
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }
}
