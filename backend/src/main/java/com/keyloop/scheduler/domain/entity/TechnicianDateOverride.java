package com.keyloop.scheduler.domain.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.keyloop.scheduler.domain.common.SoftDeletableAuditableEntity;

public class TechnicianDateOverride extends SoftDeletableAuditableEntity {

    private UUID technicianId;
    private LocalDate workDate;
    private boolean isUnavailable;
    private LocalTime startsAt;
    private LocalTime endsAt;
    private String reason;

    protected TechnicianDateOverride() {
    }

    private TechnicianDateOverride(Builder builder) {
        super(builder);
        this.technicianId = builder.technicianId;
        this.workDate = builder.workDate;
        this.isUnavailable = builder.isUnavailable;
        this.startsAt = builder.startsAt;
        this.endsAt = builder.endsAt;
        this.reason = builder.reason;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
            extends SoftDeletableAuditableEntity.Builder<Builder> {

        private UUID technicianId;
        private LocalDate workDate;
        private boolean isUnavailable;
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

        public Builder isUnavailable(boolean isUnavailable) {
            this.isUnavailable = isUnavailable;
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

        public TechnicianDateOverride build() {
            return new TechnicianDateOverride(this);
        }
    }

    public UUID getTechnicianId() {
        return technicianId;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public boolean getIsUnavailable() {
        return isUnavailable;
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
