package com.keyloop.scheduler.domain.entity;

import java.time.Instant;
import java.util.UUID;

public class TechnicianQualification {

    private UUID technicianId;
    private UUID serviceTypeId;
    private Instant qualifiedAt;

    protected TechnicianQualification() {
    }

    private TechnicianQualification(Builder builder) {
        this.technicianId = builder.technicianId;
        this.serviceTypeId = builder.serviceTypeId;
        this.qualifiedAt = builder.qualifiedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private UUID technicianId;
        private UUID serviceTypeId;
        private Instant qualifiedAt;

        public Builder technicianId(UUID technicianId) {
            this.technicianId = technicianId;
            return this;
        }

        public Builder serviceTypeId(UUID serviceTypeId) {
            this.serviceTypeId = serviceTypeId;
            return this;
        }

        public Builder qualifiedAt(Instant qualifiedAt) {
            this.qualifiedAt = qualifiedAt;
            return this;
        }

        public TechnicianQualification build() {
            return new TechnicianQualification(this);
        }
    }

    public UUID getTechnicianId() {
        return technicianId;
    }

    public UUID getServiceTypeId() {
        return serviceTypeId;
    }

    public Instant getQualifiedAt() {
        return qualifiedAt;
    }
}
