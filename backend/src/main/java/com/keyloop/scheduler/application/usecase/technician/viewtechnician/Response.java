package com.keyloop.scheduler.application.usecase.technician.viewtechnician;

import java.util.List;
import java.util.UUID;

public record Response(
        UUID dealershipId,
        String dealershipName,
        List<TechnicianItem> items,
        long totalItems
) {

    public record TechnicianItem(
            UUID id,
            String fullName,
            boolean active,
            List<QualificationItem> qualifications
    ) {
    }

    public record QualificationItem(
            UUID serviceTypeId,
            String serviceName
    ) {
    }
}
