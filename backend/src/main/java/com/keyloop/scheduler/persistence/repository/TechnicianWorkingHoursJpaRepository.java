package com.keyloop.scheduler.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keyloop.scheduler.persistence.entity.TechnicianWorkingHoursJpaEntity;

public interface TechnicianWorkingHoursJpaRepository 
    extends JpaRepository<TechnicianWorkingHoursJpaEntity, UUID> {
    
}
