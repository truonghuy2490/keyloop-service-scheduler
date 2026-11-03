package com.keyloop.scheduler.persistence.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.keyloop.scheduler.persistence.entity.AppointmentJpaEntity;

public interface AppointmentJpaRepository
        extends JpaRepository<AppointmentJpaEntity, UUID> {

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM AppointmentJpaEntity a
        WHERE a.technicianId = :technicianId
          AND a.startTime < :endTime
          AND a.endTime > :startTime
          AND a.deleted = false
          AND a.status IN ('CONFIRMED', 'IN_PROGRESS')
        """)
    boolean existsTechnicianOverlap(
            @Param("technicianId") UUID technicianId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime
    );

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM AppointmentJpaEntity a
        WHERE a.serviceBayId = :serviceBayId
          AND a.startTime < :endTime
          AND a.endTime > :startTime
          AND a.deleted = false
          AND a.status IN ('CONFIRMED', 'IN_PROGRESS')
        """)
    boolean existsServiceBayOverlap(
            @Param("serviceBayId") UUID serviceBayId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime
    );

    @Query("""
        SELECT a
        FROM AppointmentJpaEntity a
        WHERE a.dealershipId = :dealershipId
          AND a.startTime < :endTime
          AND a.endTime > :startTime
          AND a.deleted = false
          AND a.status IN ('CONFIRMED', 'IN_PROGRESS')
        ORDER BY a.startTime
        """)
    List<AppointmentJpaEntity> findActiveAppointmentsByDealershipAndTimeRange(
            @Param("dealershipId") UUID dealershipId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime
    );
}
