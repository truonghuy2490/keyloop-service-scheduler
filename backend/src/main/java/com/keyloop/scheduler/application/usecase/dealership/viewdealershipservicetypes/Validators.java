
package com.keyloop.scheduler.application.usecase.dealership.viewdealershipservicetypes;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.keyloop.scheduler.application.common.result.Error;

@Component("viewDealershipServiceTypesValidator")
public class Validators {

    public List<Error> validate(Query query) {

        if (query == null) {
            return List.of(Errors.INVALID_QUERY);
        }

        if (!isValidGuid(query.dealershipId())) {
            return List.of(Errors.INVALID_DEALERSHIP_ID);
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
