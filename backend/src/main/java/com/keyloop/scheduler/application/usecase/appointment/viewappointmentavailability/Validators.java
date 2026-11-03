package com.keyloop.scheduler.application.usecase.appointment.viewappointmentavailability;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.keyloop.scheduler.application.common.result.Error;

@Component("viewAppointmentAvailabilityValidator")
public class Validators {

    public List<Error> validate(Query query) {

        if (query == null) {
            return List.of(Errors.INVALID_REQUEST);
        }

        if (!isValidGuid(query.dealershipId())) {
            return List.of(Errors.INVALID_DEALERSHIP_ID);
        }

        if (!isValidGuid(query.serviceTypeId())) {
            return List.of(Errors.INVALID_SERVICE_TYPE_ID);
        }

        if (query.date() == null) {
            return List.of(Errors.INVALID_DATE);
        }

        if (query.technicianId() != null
                && !query.technicianId().isBlank()
                && !isValidGuid(query.technicianId())) {
            return List.of(Errors.INVALID_TECHNICIAN_ID);
        }

        return List.of();
    }

    private boolean isValidGuid(String value) {

        if (value == null || value.isBlank()) {
            return false;
        }

        try {
            UUID uuid = UUID.fromString(value);
            return uuid.toString().equalsIgnoreCase(value);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
