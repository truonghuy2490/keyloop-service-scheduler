package com.keyloop.scheduler.application.abstraction.dataaccess;

import java.util.UUID;

import com.keyloop.scheduler.application.common.pagination.PagedList;
import com.keyloop.scheduler.domain.entity.Vehicle;

public interface VehicleRepository
        extends BaseRepository<Vehicle, UUID> {

    PagedList<Vehicle> findAllByCustomerId(
            UUID customerId,
            int pageNumber,
            int pageSize
    );
}