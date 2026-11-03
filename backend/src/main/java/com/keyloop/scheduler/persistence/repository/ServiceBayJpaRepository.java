package com.keyloop.scheduler.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.keyloop.scheduler.persistence.entity.ServiceBayJpaEntity;

public interface ServiceBayJpaRepository
        extends BaseJpaRepository<ServiceBayJpaEntity, UUID> {

    Page<ServiceBayJpaEntity> findAllByDealershipIdAndDeletedFalse(
            UUID dealershipId,
            Pageable pageable
    );

    List<ServiceBayJpaEntity> findAllByDealershipIdAndActiveTrueAndDeletedFalse(
            UUID dealershipId
    );
}
