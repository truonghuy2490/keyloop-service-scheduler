package com.keyloop.scheduler.application.usecase.appointment.viewappointmentavailability;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record Response(
        UUID dealershipId,
        UUID serviceTypeId,
        LocalDate date,
        String timeZone,
        Integer durationMinutes,
        List<TechnicianAvailabilityItem> technicians
) {

    public record TechnicianAvailabilityItem(
            UUID id,
            String fullName,
            boolean active,
            List<AvailabilitySlot> availableSlots
    ) {
    }

    public record AvailabilitySlot(
            OffsetDateTime startTime,
            OffsetDateTime endTime,
            boolean available
    ) {
    }
}
