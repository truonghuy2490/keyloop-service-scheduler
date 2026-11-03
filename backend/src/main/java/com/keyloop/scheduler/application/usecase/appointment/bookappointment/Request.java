package com.keyloop.scheduler.application.usecase.appointment.bookappointment;

import java.time.OffsetDateTime;

public record Request(
    String customerId,
    String vehicleId,
    String dealershipId,
    String serviceTypeId,
    String technicianId,
    String serviceBayId,
    OffsetDateTime startTime
) {
}
