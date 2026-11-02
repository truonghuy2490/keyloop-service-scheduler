package com.keyloop.scheduler.persistence.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TechnicianQualificationId implements Serializable {

    @Column(name = "technician_id", nullable = false)
    private UUID technicianId;

    @Column(name = "service_type_id", nullable = false)
    private UUID serviceTypeId;

    protected TechnicianQualificationId() {
    }

    private TechnicianQualificationId(Builder builder) {
        this.technicianId = builder.technicianId;
        this.serviceTypeId = builder.serviceTypeId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private UUID technicianId;
        private UUID serviceTypeId;

        public Builder technicianId(UUID technicianId) {
            this.technicianId = technicianId;
            return this;
        }

        public Builder serviceTypeId(UUID serviceTypeId) {
            this.serviceTypeId = serviceTypeId;
            return this;
        }

        public TechnicianQualificationId build() {
            return new TechnicianQualificationId(this);
        }
    }

    public UUID getTechnicianId() {
        return technicianId;
    }

    public UUID getServiceTypeId() {
        return serviceTypeId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof TechnicianQualificationId that)) {
            return false;
        }

        return Objects.equals(technicianId, that.technicianId)
                && Objects.equals(serviceTypeId, that.serviceTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(technicianId, serviceTypeId);
    }
}
