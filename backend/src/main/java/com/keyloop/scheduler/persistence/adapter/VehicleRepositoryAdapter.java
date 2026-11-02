package com.keyloop.scheduler.persistence.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.keyloop.scheduler.application.abstraction.dataaccess.VehicleRepository;
import com.keyloop.scheduler.application.common.pagination.PagedList;
import com.keyloop.scheduler.domain.entity.Vehicle;
import com.keyloop.scheduler.persistence.entity.VehicleJpaEntity;
import com.keyloop.scheduler.persistence.repository.VehicleJpaRepository;

@Repository
public class VehicleRepositoryAdapter
        extends BaseRepositoryAdapter<
            Vehicle,
            VehicleJpaEntity,
            UUID>
        implements VehicleRepository {

    private final VehicleJpaRepository vehicleRepository;

    public VehicleRepositoryAdapter(
            VehicleJpaRepository vehicleRepository) {
        super(vehicleRepository);
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    protected Vehicle toDomain(VehicleJpaEntity source) {
        if (source == null) {
            return null;
        }

        return Vehicle.builder()
                .id(source.getId())
                .isDeleted(source.isDeleted())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .customerId(source.getCustomerId())
                .registrationNumber(source.getRegistrationNumber())
                .make(source.getMake())
                .model(source.getModel())
                .modelYear(source.getModelYear())
                .build();
    }

    @Override
    protected VehicleJpaEntity toPersistence(Vehicle source) {
        if (source == null) {
            return null;
        }

        return VehicleJpaEntity.builder()
                .id(source.getId())
                .deleted(source.getIsDeleted())
                .customerId(source.getCustomerId())
                .registrationNumber(source.getRegistrationNumber())
                .make(source.getMake())
                .model(source.getModel())
                .modelYear(source.getModelYear())
                .build();
    }

    @Override
    public PagedList<Vehicle> findAllByCustomerId(
            UUID customerId,
            int pageNumber,
            int pageSize) {

        Pageable pageable = PageRequest.of(
                pageNumber - 1,
                pageSize
        );

        Page<VehicleJpaEntity> vehiclePage =
        vehicleRepository.findAllByCustomerIdAndDeletedFalse(
                customerId,
                pageable
        );

        List<Vehicle> vehicles = vehiclePage.getContent()
                .stream()
                .map(this::toDomain)
                .toList();

        return PagedList.create(
                vehicles,
                vehiclePage.getTotalElements(),
                pageNumber,
                pageSize
        );
    }
}
