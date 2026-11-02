package com.keyloop.scheduler.persistence.entity;

import java.time.LocalTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.keyloop.scheduler.persistence.entity.common.SoftDeletedCreatedJpaEntity;

@Entity
@Table(name = "technician_working_hours")
public class TechnicianWorkingHoursJpaEntity
        extends SoftDeletedCreatedJpaEntity {

    @Column(name = "technician_id", nullable = false)
    private UUID technicianId;

    @Column(name = "day_of_week", nullable = false)
    private Short dayOfWeek;

    @Column(name = "starts_at", nullable = false)
    private LocalTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalTime endsAt;

    protected TechnicianWorkingHoursJpaEntity() {
    }

    private TechnicianWorkingHoursJpaEntity(Builder builder) {
        super(builder);
        this.technicianId = builder.technicianId;
        this.dayOfWeek = builder.dayOfWeek;
        this.startsAt = builder.startsAt;
        this.endsAt = builder.endsAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
            extends SoftDeletedCreatedJpaEntity.Builder<Builder> {

        private UUID technicianId;
        private Short dayOfWeek;
        private LocalTime startsAt;
        private LocalTime endsAt;

        public Builder technicianId(UUID technicianId) {
            this.technicianId = technicianId;
            return this;
        }

        public Builder dayOfWeek(Short dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
            return this;
        }

        public Builder startsAt(LocalTime startsAt) {
            this.startsAt = startsAt;
            return this;
        }

        public Builder endsAt(LocalTime endsAt) {
            this.endsAt = endsAt;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public TechnicianWorkingHoursJpaEntity build() {
            return new TechnicianWorkingHoursJpaEntity(this);
        }
    }

    public UUID getTechnicianId() {
        return technicianId;
    }

    public Short getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartsAt() {
        return startsAt;
    }

    public LocalTime getEndsAt() {
        return endsAt;
    }
}
