package com.keyloop.scheduler.persistence.adapter;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.keyloop.scheduler.domain.entity.TechnicianWorkingHours;
import com.keyloop.scheduler.persistence.entity.TechnicianWorkingHoursJpaEntity;
import com.keyloop.scheduler.persistence.repository.TechnicianWorkingHoursJpaRepository;

@Repository
public class TechnicianWorkingHoursRepositoryAdapter
        extends BaseRepositoryAdapter<
                TechnicianWorkingHours,
                TechnicianWorkingHoursJpaEntity,
                UUID> {

    public TechnicianWorkingHoursRepositoryAdapter(
            TechnicianWorkingHoursJpaRepository repository) {
        super(repository);
    }

    @Override
    protected TechnicianWorkingHours toDomain(
            TechnicianWorkingHoursJpaEntity source) {

        if (source == null) {
            return null;
        }

        return TechnicianWorkingHours.builder()
                .id(source.getId())
                .createdAt(source.getCreatedAt())
                .isDeleted(source.isDeleted())
                .technicianId(source.getTechnicianId())
                .dayOfWeek(source.getDayOfWeek())
                .startsAt(source.getStartsAt())
                .endsAt(source.getEndsAt())
                .build();
    }

    @Override
    protected TechnicianWorkingHoursJpaEntity toPersistence(
            TechnicianWorkingHours source) {

        if (source == null) {
            return null;
        }

        return TechnicianWorkingHoursJpaEntity.builder()
                .id(source.getId())
                .deleted(source.getIsDeleted())
                .technicianId(source.getTechnicianId())
                .dayOfWeek(source.getDayOfWeek())
                .startsAt(source.getStartsAt())
                .endsAt(source.getEndsAt())
                .build();
    }
}
