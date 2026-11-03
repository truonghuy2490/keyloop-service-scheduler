
package com.keyloop.scheduler.application.usecase.dealership.viewdealershipservicetypes;

import java.util.ArrayList;
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

        List<Error> errors = new ArrayList<>();

        if (!isValidGuid(query.dealershipId())) {
            errors.add(Errors.INVALID_DEALERSHIP_ID);
        }

        if (query.pageNumber() < 1) {
            errors.add(Errors.INVALID_PAGE_NUMBER);
        }

        if (query.pageSize() < 1 || query.pageSize() > 100) {
            errors.add(Errors.INVALID_PAGE_SIZE);
        }

        return errors;
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

