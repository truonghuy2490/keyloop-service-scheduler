package com.keyloop.scheduler.application.usecase.appointment.bookappointment;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public record Response(
        UUID id,
        String status,
        CustomerItem customer,
        VehicleItem vehicle,
        DealershipItem dealership,
        ServiceItem service,
        TechnicianItem technician,
        ServiceBayItem serviceBay,
        ScheduleItem schedule,
        Instant createdAt,
        Instant updatedAt) {
    public record CustomerItem(
            UUID id,
            String fullName) {
    }

    public record VehicleItem(
            UUID id,
            String registrationNumber,
            String make,
            String model,
            Integer modelYear) {
    }

    public record DealershipItem(
            UUID id,
            String name,
            String timeZone) {
    }

    public record ServiceItem(
            UUID id,
            String name,
            String description,
            Integer durationMinutes) {
    }

    public record TechnicianItem(
            UUID id,
            String fullName) {
    }

    public record ServiceBayItem(
            UUID id,
            String bayCode) {
    }

    public record ScheduleItem(
            OffsetDateTime startTime,
            OffsetDateTime endTime,
            Integer durationMinutes,
            String timeZone) {
    }
}
