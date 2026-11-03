package com.keyloop.scheduler.application.usecase.dealership.viewdealershipservicetypes;

import java.util.UUID;

public record Response(
        UUID id,
        String name,
        String description,
        Integer durationMinutes
) {
}
