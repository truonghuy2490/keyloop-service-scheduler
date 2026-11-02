package com.keyloop.scheduler.persistence.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.keyloop.scheduler.persistence.entity.VehicleJpaEntity;

public interface VehicleJpaRepository
 extends JpaRepository<VehicleJpaEntity, UUID> {
    Page<VehicleJpaEntity> findAllByCustomerIdAndDeletedFalse(
            UUID customerId,
            Pageable pageable
    );
} 
