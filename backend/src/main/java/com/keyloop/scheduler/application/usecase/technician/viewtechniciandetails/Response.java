
package com.keyloop.scheduler.application.usecase.technician.viewtechniciandetails;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record Response(
        UUID id,
        String fullName,
        boolean active,
        DealershipItem dealership,
        List<QualificationItem> qualifications,
        List<WorkingHoursItem> workingHours,
        List<DateOverrideItem> dateOverrides,
        Instant createdAt,
        Instant updatedAt
) {

    public record DealershipItem(
            UUID id,
            String name
    ) {
    }

    public record QualificationItem(
            UUID serviceTypeId,
            String name,
            String description,
            Integer durationMinutes,
            Instant qualifiedAt
    ) {
    }

    public record WorkingHoursItem(
            DayOfWeek dayOfWeek,
            List<WorkingInterval> intervals
    ) {
    }

    public record WorkingInterval(
            LocalTime startsAt,
            LocalTime endsAt
    ) {
    }

    public record DateOverrideItem(
            LocalDate workDate,
            boolean isUnavailable,
            LocalTime startsAt,
            LocalTime endsAt,
            String reason
    ) {
    }
}
