package com.keyloop.scheduler.persistence.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.keyloop.scheduler.application.abstraction.dataaccess.ServiceBayRepository;
import com.keyloop.scheduler.application.common.pagination.PagedList;
import com.keyloop.scheduler.domain.entity.ServiceBay;
import com.keyloop.scheduler.persistence.entity.ServiceBayJpaEntity;
import com.keyloop.scheduler.persistence.repository.ServiceBayJpaRepository;

@Repository
public class ServiceBayRepositoryAdapter
        extends BaseRepositoryAdapter<
            ServiceBay,
            ServiceBayJpaEntity,
            UUID>
        implements ServiceBayRepository {

    private final ServiceBayJpaRepository serviceBayRepository;

    public ServiceBayRepositoryAdapter(
            ServiceBayJpaRepository serviceBayRepository) {
        super(serviceBayRepository);
        this.serviceBayRepository = serviceBayRepository;
    }

    @Override
    protected ServiceBay toDomain(ServiceBayJpaEntity source) {

        if (source == null) {
            return null;
        }

        return ServiceBay.builder()
                .id(source.getId())
                .isDeleted(source.isDeleted())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .dealershipId(source.getDealershipId())
                .bayCode(source.getBayCode())
                .active(source.isActive())
                .build();
    }

    @Override
    protected ServiceBayJpaEntity toPersistence(ServiceBay source) {

        if (source == null) {
            return null;
        }

        return ServiceBayJpaEntity.builder()
                .id(source.getId())
                .deleted(source.getIsDeleted())
                .dealershipId(source.getDealershipId())
                .bayCode(source.getBayCode())
                .active(source.getActive())
                .build();
    }

    @Override
    public PagedList<ServiceBay> findAllByDealershipId(
            UUID dealershipId,
            int pageNumber,
            int pageSize) {

        Pageable pageable = PageRequest.of(
                pageNumber - 1,
                pageSize
        );

        Page<ServiceBayJpaEntity> serviceBayPage =
                serviceBayRepository.findAllByDealershipIdAndDeletedFalse(
                        dealershipId,
                        pageable
                );

        List<ServiceBay> serviceBays = serviceBayPage.getContent()
                .stream()
                .map(this::toDomain)
                .toList();

        return PagedList.create(
                serviceBays,
                serviceBayPage.getTotalElements(),
                pageNumber,
                pageSize
        );
    }
    
    
        @Override
        @Transactional(readOnly = true)
        public List<ServiceBay> findActiveByDealershipId(
                UUID dealershipId
        ) {
        return serviceBayRepository
                .findAllByDealershipIdAndActiveTrueAndDeletedFalse(
                        dealershipId
                )
                .stream()
                .map(this::toDomain)
                .toList();
        }

}
