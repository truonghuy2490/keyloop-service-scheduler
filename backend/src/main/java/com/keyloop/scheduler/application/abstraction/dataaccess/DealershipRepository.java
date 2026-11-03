package com.keyloop.scheduler.application.abstraction.dataaccess;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.keyloop.scheduler.domain.entity.Dealership;

public interface DealershipRepository
        extends BaseRepository<Dealership, UUID> {

    Optional<DealershipDetailsData> findDetailsById(UUID dealershipId);

    record DealershipDetailsData(
            UUID id,
            String name,
            String timeZone,
            List<WorkingHoursData> workingHours,
            List<DateOverrideData> dateOverrides,
            ResourceSummaryData resourceSummary
    ) {}

    record WorkingHoursData(
            DayOfWeek dayOfWeek,
            boolean isClosed,
            LocalTime opensAt,
            LocalTime closesAt
    ) {}

    record DateOverrideData(
            LocalDate businessDate,
            boolean isClosed,
            LocalTime opensAt,
            LocalTime closesAt,
            String reason
    ) {}

    record ResourceSummaryData(
            long serviceBayCount,
            long technicianCount,
            long serviceCount
    ) {}

    List<ServiceTypeData> findActiveByDealershipId(
            UUID dealershipId
    );

    record ServiceTypeData(
            UUID id,
            String name,
            String description,
            Integer durationMinutes
    ) {
    }
}