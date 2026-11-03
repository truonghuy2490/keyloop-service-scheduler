
package com.keyloop.scheduler.application.usecase.appointment.bookappointment;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.keyloop.scheduler.application.common.result.Error;

@Component("bookAppointmentValidator")
public class Validators {

    public List<Error> validate(Request request) {

        if (request == null) {
            return List.of(Errors.INVALID_REQUEST);
        }

        if (!isValidGuid(request.customerId())) {
            return List.of(Errors.INVALID_CUSTOMER_ID);
        }

        if (!isValidGuid(request.vehicleId())) {
            return List.of(Errors.INVALID_VEHICLE_ID);
        }

        if (!isValidGuid(request.dealershipId())) {
            return List.of(Errors.INVALID_DEALERSHIP_ID);
        }

        if (!isValidGuid(request.serviceTypeId())) {
            return List.of(Errors.INVALID_SERVICE_TYPE_ID);
        }

        if (!isValidGuid(request.technicianId())) {
            return List.of(Errors.INVALID_TECHNICIAN_ID);
        }

        if (!isValidStartTime(request.startTime())) {
            return List.of(Errors.INVALID_START_TIME);
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

    private boolean isValidStartTime(OffsetDateTime value) {
        return value != null;
    }
}
