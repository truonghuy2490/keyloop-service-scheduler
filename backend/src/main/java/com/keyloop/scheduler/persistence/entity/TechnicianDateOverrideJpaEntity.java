package com.keyloop.scheduler.persistence.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.keyloop.scheduler.persistence.entity.common.SoftDeletedAuditableJpaEntity;

@Entity
@Table(name = "technician_date_override")
public class TechnicianDateOverrideJpaEntity
        extends SoftDeletedAuditableJpaEntity {

    @Column(name = "technician_id", nullable = false)
    private UUID technicianId;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "is_unavailable", nullable = false)
    private boolean unavailable;

    @Column(name = "starts_at")
    private LocalTime startsAt;

    @Column(name = "ends_at")
    private LocalTime endsAt;

    @Column(name = "reason", length = 300)
    private String reason;

    protected TechnicianDateOverrideJpaEntity() {
    }

    private TechnicianDateOverrideJpaEntity(Builder builder) {
        super(builder);
        this.technicianId = builder.technicianId;
        this.workDate = builder.workDate;
        this.unavailable = builder.unavailable;
        this.startsAt = builder.startsAt;
        this.endsAt = builder.endsAt;
        this.reason = builder.reason;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
            extends SoftDeletedAuditableJpaEntity.Builder<Builder> {

        private UUID technicianId;
        private LocalDate workDate;
        private boolean unavailable;
        private LocalTime startsAt;
        private LocalTime endsAt;
        private String reason;

        public Builder technicianId(UUID technicianId) {
            this.technicianId = technicianId;
            return this;
        }

        public Builder workDate(LocalDate workDate) {
            this.workDate = workDate;
            return this;
        }

        public Builder unavailable(boolean unavailable) {
            this.unavailable = unavailable;
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

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public TechnicianDateOverrideJpaEntity build() {
            return new TechnicianDateOverrideJpaEntity(this);
        }
    }

    public UUID getTechnicianId() {
        return technicianId;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public boolean isUnavailable() {
        return unavailable;
    }

    public LocalTime getStartsAt() {
        return startsAt;
    }

    public LocalTime getEndsAt() {
        return endsAt;
    }

    public String getReason() {
        return reason;
    }
}
