package com.keyloop.scheduler.application.usecase.dealership.viewdealershipservicetypes;

import java.util.List;
import java.util.UUID;

public record Response(
        List<ServiceTypeItem> items
) {
    public record ServiceTypeItem(
            UUID id,
            String name,
            String description,
            Integer durationMinutes
    ) {
    }
}
