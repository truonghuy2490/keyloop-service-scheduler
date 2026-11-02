package com.keyloop.scheduler.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keyloop.scheduler.persistence.entity.TechnicianJpaEntity;

public interface TechnicianJpaRepository 
    extends JpaRepository<TechnicianJpaEntity, UUID> {
}
