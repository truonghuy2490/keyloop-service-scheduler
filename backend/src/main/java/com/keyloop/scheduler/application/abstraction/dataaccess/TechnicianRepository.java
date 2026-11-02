package com.keyloop.scheduler.application.abstraction.dataaccess;

import java.util.List;
import java.util.UUID;

public interface TechnicianRepository {

    List<TechnicianDetailsData> findDetailsByDealershipId(UUID dealershipId);
    
    public record TechnicianDetailsData(
            UUID dealershipId,
            String dealershipName,
            UUID technicianId,
            String fullName,
            boolean active,
            UUID serviceTypeId,
            String serviceName
    ) {
    }
}
