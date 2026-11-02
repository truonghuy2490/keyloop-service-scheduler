package com.keyloop.scheduler.domain.entity;

import java.time.LocalTime;
import java.util.UUID;

import com.keyloop.scheduler.domain.common.SoftDeletableCreatedEntity;

public class TechnicianWorkingHours extends SoftDeletableCreatedEntity {

    private UUID technicianId;
    private Short dayOfWeek;
    private LocalTime startsAt;
    private LocalTime endsAt;

    protected TechnicianWorkingHours() {
    }

    private TechnicianWorkingHours(Builder builder) {
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
            extends SoftDeletableCreatedEntity.Builder<Builder> {

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

        public TechnicianWorkingHours build() {
            return new TechnicianWorkingHours(this);
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
