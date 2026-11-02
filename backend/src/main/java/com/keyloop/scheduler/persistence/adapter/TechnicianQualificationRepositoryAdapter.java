package com.keyloop.scheduler.persistence.adapter;

import org.springframework.stereotype.Repository;

import com.keyloop.scheduler.domain.entity.TechnicianQualification;
import com.keyloop.scheduler.persistence.entity.TechnicianQualificationId;
import com.keyloop.scheduler.persistence.entity.TechnicianQualificationJpaEntity;
import com.keyloop.scheduler.persistence.repository.TechnicianQualificationJpaRepository;

@Repository
public class TechnicianQualificationRepositoryAdapter
        extends BaseRepositoryAdapter<
                TechnicianQualification,
                TechnicianQualificationJpaEntity,
                TechnicianQualificationId> {

    public TechnicianQualificationRepositoryAdapter(
            TechnicianQualificationJpaRepository repository) {
        super(repository);
    }

    @Override
    protected TechnicianQualification toDomain(
            TechnicianQualificationJpaEntity source) {

        if (source == null) {
            return null;
        }

        return TechnicianQualification.builder()
                .technicianId(source.getId().getTechnicianId())
                .serviceTypeId(source.getId().getServiceTypeId())
                .qualifiedAt(source.getQualifiedAt())
                .build();
    }

    @Override
    protected TechnicianQualificationJpaEntity toPersistence(
            TechnicianQualification source) {

        if (source == null) {
            return null;
        }

        TechnicianQualificationId id = TechnicianQualificationId.builder()
                .technicianId(source.getTechnicianId())
                .serviceTypeId(source.getServiceTypeId())
                .build();

        return TechnicianQualificationJpaEntity.builder()
                .id(id)
                .build();
    }
}
