package com.keyloop.scheduler.persistence.adapter;

import com.keyloop.scheduler.application.abstraction.dataaccess.AppointmentRepository;
import com.keyloop.scheduler.domain.entity.Appointment;
import com.keyloop.scheduler.persistence.entity.AppointmentJpaEntity;
import com.keyloop.scheduler.persistence.repository.AppointmentJpaRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class AppointmentRepositoryAdapter
        extends BaseRepositoryAdapter<Appointment, AppointmentJpaEntity, UUID>
        implements AppointmentRepository {

    private final AppointmentJpaRepository appointmentRepository;

    public AppointmentRepositoryAdapter(
            AppointmentJpaRepository appointmentRepository) {

        super(appointmentRepository);
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public boolean existsTechnicianOverlap(
            UUID technicianId,
            Instant startTime,
            Instant endTime) {

        return appointmentRepository.existsTechnicianOverlap(
                technicianId,
                startTime,
                endTime);
    }

    @Override
    public boolean existsServiceBayOverlap(
            UUID serviceBayId,
            Instant startTime,
            Instant endTime) {

        return appointmentRepository.existsServiceBayOverlap(
                serviceBayId,
                startTime,
                endTime);
    }

    @Override
    protected Appointment toDomain(AppointmentJpaEntity source) {
        if (source == null) {
            return null;
        }

        return Appointment.builder()
                // Common fields
                .id(source.getId())
                .isDeleted(source.isDeleted())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())

                // Appointment fields
                .customerId(source.getCustomerId())
                .vehicleId(source.getVehicleId())
                .dealershipId(source.getDealershipId())
                .serviceTypeId(source.getServiceTypeId())
                .technicianId(source.getTechnicianId())
                .serviceBayId(source.getServiceBayId())
                .startTime(source.getStartTime())
                .endTime(source.getEndTime())
                .status(source.getStatus())
                .version(source.getVersion())
                .cancellationReason(source.getCancellationReason())
                .build();
    }

    @Override
    protected AppointmentJpaEntity toPersistence(Appointment source) {
        if (source == null) {
            return null;
        }

        return AppointmentJpaEntity.builder()
                // Common fields
                .id(source.getId())
                .deleted(source.getIsDeleted())

                // Appointment fields
                .customerId(source.getCustomerId())
                .vehicleId(source.getVehicleId())
                .dealershipId(source.getDealershipId())
                .serviceTypeId(source.getServiceTypeId())
                .technicianId(source.getTechnicianId())
                .serviceBayId(source.getServiceBayId())
                .startTime(source.getStartTime())
                .endTime(source.getEndTime())
                .status(source.getStatus())
                .version(source.getVersion())
                .cancellationReason(source.getCancellationReason())
                .build();
    }

    @Override
    public List<Appointment> findActiveAppointmentsByDealershipAndTimeRange(
            UUID dealershipId,
            Instant startTime,
            Instant endTime) {
        return appointmentRepository
                .findActiveAppointmentsByDealershipAndTimeRange(
                        dealershipId,
                        startTime,
                        endTime)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public Appointment save(Appointment source) {

        if (source == null) {
            throw new IllegalArgumentException(
                    "Appointment cannot be null");
        }

        // New appointment: Hibernate generates ID and version.
        if (source.getVersion() == null) {

            AppointmentJpaEntity entity = AppointmentJpaEntity.builder()
                    .deleted(source.getIsDeleted())
                    .customerId(source.getCustomerId())
                    .vehicleId(source.getVehicleId())
                    .dealershipId(source.getDealershipId())
                    .serviceTypeId(source.getServiceTypeId())
                    .technicianId(source.getTechnicianId())
                    .serviceBayId(source.getServiceBayId())
                    .startTime(source.getStartTime())
                    .endTime(source.getEndTime())
                    .status(source.getStatus())
                    .cancellationReason(
                            source.getCancellationReason())
                    .build();

            AppointmentJpaEntity saved = appointmentRepository.saveAndFlush(entity);

            return toDomain(saved);
        }

        // Existing appointment: retain ID and version.
        return super.save(source);
    }

}
