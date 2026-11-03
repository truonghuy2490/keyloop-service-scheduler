package com.keyloop.scheduler.application.abstraction.dataaccess;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.keyloop.scheduler.domain.entity.Technician;

public interface TechnicianRepository extends BaseRepository<Technician, UUID>   {

    List<TechnicianData> findDetailsByDealershipId(UUID dealershipId);
    
    public record TechnicianData(
            UUID dealershipId,
            String dealershipName,
            UUID technicianId,
            String fullName,
            boolean active,
            UUID serviceTypeId,
            String serviceName
    ) {
    }

    Optional<TechnicianDetailsData> findDetailsByTechnicianId(
            UUID technicianId
    );

    record TechnicianDetailsData(
            UUID id,
            String fullName,
            boolean active,
            UUID dealershipId,
            String dealershipName,
            Instant createdAt,
            Instant updatedAt,
            List<QualificationData> qualifications,
            List<WorkingHoursData> workingHours,
            List<DateOverrideData> dateOverrides
    ) {
    }

    record QualificationData(
            UUID serviceTypeId,
            String name,
            String description,
            Integer durationMinutes,
            Instant qualifiedAt
    ) {
    }

    record WorkingHoursData(
            DayOfWeek dayOfWeek,
            LocalTime startsAt,
            LocalTime endsAt
    ) {
    }

    record DateOverrideData(
            LocalDate workDate,
            boolean isUnavailable,
            LocalTime startsAt,
            LocalTime endsAt,
            String reason
    ) {
    }

    boolean isQualifiedForServiceType(
        UUID technicianId,
        UUID serviceTypeId
);

}
