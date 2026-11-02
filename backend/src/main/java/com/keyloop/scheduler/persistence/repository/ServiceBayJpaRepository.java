package com.keyloop.scheduler.persistence.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.keyloop.scheduler.persistence.entity.ServiceBayJpaEntity;

public interface ServiceBayJpaRepository
    extends BaseJpaRepository<ServiceBayJpaEntity, java.util.UUID> {
    Page<ServiceBayJpaEntity> findAllByDealershipIdAndDeletedFalse(
            UUID dealershipId,
            Pageable pageable
    );
}
