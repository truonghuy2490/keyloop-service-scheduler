package com.keyloop.scheduler.application.abstraction.dataaccess;

import java.util.UUID;

import com.keyloop.scheduler.application.common.pagination.PagedList;
import com.keyloop.scheduler.domain.entity.ServiceBay;

public interface ServiceBayRepository
        extends BaseRepository<ServiceBay, UUID> {

    PagedList<ServiceBay> findAllByDealershipId(
            UUID dealershipId,
            int pageNumber,
            int pageSize
    );
}
