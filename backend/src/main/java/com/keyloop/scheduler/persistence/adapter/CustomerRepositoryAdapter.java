package com.keyloop.scheduler.persistence.adapter;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.keyloop.scheduler.domain.entity.Customer;
import com.keyloop.scheduler.persistence.entity.CustomerJpaEntity;
import com.keyloop.scheduler.persistence.repository.CustomerJpaRepository;

@Repository
public class CustomerRepositoryAdapter
        extends BaseRepositoryAdapter<Customer, CustomerJpaEntity, UUID> {

    public CustomerRepositoryAdapter(CustomerJpaRepository customerRepository) {
        super(customerRepository);
    }

    @Override
    protected Customer toDomain(CustomerJpaEntity source) {
        if (source == null) {
            return null;
        }

        return Customer.builder()
                .id(source.getId())
                .isDeleted(source.isDeleted())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .fullName(source.getFullName())
                .email(source.getEmail())
                .phone(source.getPhone())
                .build();
    }

    @Override
    protected CustomerJpaEntity toPersistence(Customer source) {
        if (source == null) {
            return null;
        }

        return CustomerJpaEntity.builder()
                .id(source.getId())
                .deleted(source.getIsDeleted())
                .fullName(source.getFullName())
                .email(source.getEmail())
                .phone(source.getPhone())
                .build();
    }
}
