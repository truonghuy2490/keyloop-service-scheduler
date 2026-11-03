package com.keyloop.scheduler.application.abstraction.dataaccess;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.keyloop.scheduler.domain.entity.Appointment;

public interface AppointmentRepository
        extends BaseRepository<Appointment, UUID>{

    boolean existsTechnicianOverlap(
            UUID technicianId,
            Instant startTime,
            Instant endTime
    );

    boolean existsServiceBayOverlap(
            UUID serviceBayId,
            Instant startTime,
            Instant endTime
    );

    // Get all appointments occupying resources during a period.
    List<Appointment> findActiveAppointmentsByDealershipAndTimeRange(
            UUID dealershipId,
            Instant startTime,
            Instant endTime
    );
}