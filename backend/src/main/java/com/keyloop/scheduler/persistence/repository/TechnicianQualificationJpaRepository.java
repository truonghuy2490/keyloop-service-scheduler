package com.keyloop.scheduler.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keyloop.scheduler.persistence.entity.TechnicianQualificationId;
import com.keyloop.scheduler.persistence.entity.TechnicianQualificationJpaEntity;

public interface TechnicianQualificationJpaRepository 
    extends JpaRepository<TechnicianQualificationJpaEntity, TechnicianQualificationId> {
    
}
