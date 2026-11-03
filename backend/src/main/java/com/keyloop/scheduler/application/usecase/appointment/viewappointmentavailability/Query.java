package com.keyloop.scheduler.application.usecase.appointment.viewappointmentavailability;

import java.time.LocalDate;

public record Query(
        String dealershipId,
        String serviceTypeId,
        LocalDate date,
        String technicianId
) {
}
