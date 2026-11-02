package com.keyloop.scheduler.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.keyloop.scheduler.persistence.entity.common.SoftDeletedAuditableJpaEntity;

@Entity
@Table(name = "service_type")
public class ServiceTypeJpaEntity extends SoftDeletedAuditableJpaEntity {

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    protected ServiceTypeJpaEntity() {
    }

    private ServiceTypeJpaEntity(Builder builder) {
        super(builder);
        this.name = builder.name;
        this.description = builder.description;
        this.durationMinutes = builder.durationMinutes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
            extends SoftDeletedAuditableJpaEntity.Builder<Builder> {

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

        public ServiceTypeJpaEntity build() {
            return new ServiceTypeJpaEntity(this);
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
