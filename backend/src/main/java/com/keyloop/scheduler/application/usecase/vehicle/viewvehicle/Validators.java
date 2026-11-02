package com.keyloop.scheduler.application.usecase.vehicle.viewvehicle;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.keyloop.scheduler.application.common.result.Error;

@Component("viewVehicleValidator")
public class Validators {

    public List<Error> validate(Query query) {

        if (query == null || !isValidGuid(query.customerId())) {
            return List.of(Errors.INVALID_CUSTOMER_ID);
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
