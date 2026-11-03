package com.keyloop.scheduler.persistence.adapter;

import com.keyloop.scheduler.application.abstraction.dataaccess.AppointmentRepository;
import com.keyloop.scheduler.domain.entity.Appointment;
import com.keyloop.scheduler.persistence.entity.AppointmentJpaEntity;
import com.keyloop.scheduler.persistence.repository.AppointmentJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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
                endTime
        );
    }

    @Override
    public boolean existsServiceBayOverlap(
            UUID serviceBayId,
            Instant startTime,
            Instant endTime) {

        return appointmentRepository.existsServiceBayOverlap(
                serviceBayId,
                startTime,
                endTime
        );
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

}
