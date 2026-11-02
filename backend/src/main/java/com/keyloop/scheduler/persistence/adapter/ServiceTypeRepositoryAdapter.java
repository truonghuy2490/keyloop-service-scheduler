package com.keyloop.scheduler.persistence.adapter;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.keyloop.scheduler.domain.entity.ServiceType;
import com.keyloop.scheduler.persistence.entity.ServiceTypeJpaEntity;
import com.keyloop.scheduler.persistence.repository.ServiceTypeJpaRepository;

@Repository
public class ServiceTypeRepositoryAdapter
        extends BaseRepositoryAdapter<ServiceType, ServiceTypeJpaEntity, UUID> {

    public ServiceTypeRepositoryAdapter(ServiceTypeJpaRepository serviceTypeRepository) {
        super(serviceTypeRepository);
    }

    @Override
    protected ServiceType toDomain(ServiceTypeJpaEntity source) {
        if (source == null) {
            return null;
        }

        return ServiceType.builder()
                .id(source.getId())
                .isDeleted(source.isDeleted())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .name(source.getName())
                .description(source.getDescription())
                .durationMinutes(source.getDurationMinutes())
                .build();
    }

    @Override
    protected ServiceTypeJpaEntity toPersistence(ServiceType source) {
        if (source == null) {
            return null;
        }

        return ServiceTypeJpaEntity.builder()
                .id(source.getId())
                .deleted(source.getIsDeleted())
                .name(source.getName())
                .description(source.getDescription())
                .durationMinutes(source.getDurationMinutes())
                .build();
    }
}
