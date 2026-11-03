
package com.keyloop.scheduler.application.usecase.appointment.viewappointmentavailability;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.keyloop.scheduler.application.common.result.Error;

class ValidatorsTest {

    private Validators validators;

    private final String dealershipId = UUID.randomUUID().toString();
    private final String serviceTypeId = UUID.randomUUID().toString();
    private final String technicianId = UUID.randomUUID().toString();
    private final LocalDate date = LocalDate.of(2026, 11, 4);

    @BeforeEach
    void setUp() {
        validators = new Validators();
    }

    @Test
    void shouldReturnInvalidRequestWhenQueryIsNull() {
        // Act
        List<Error> errors = validators.validate(null);

        // Assert
        assertEquals(List.of(Errors.INVALID_REQUEST), errors);
    }

    @Test
    void shouldReturnInvalidDealershipIdWhenNull() {
        Query query = new Query(
                null, serviceTypeId, date, null
        );

        List<Error> errors = validators.validate(query);

        assertEquals(List.of(Errors.INVALID_DEALERSHIP_ID), errors);
    }

    @Test
    void shouldReturnInvalidDealershipIdWhenMalformed() {
        Query query = new Query(
                "invalid-uuid", serviceTypeId, date, null
        );

        List<Error> errors = validators.validate(query);

        assertEquals(List.of(Errors.INVALID_DEALERSHIP_ID), errors);
    }

    @Test
    void shouldReturnInvalidServiceTypeIdWhenNull() {
        Query query = new Query(
                dealershipId, null, date, null
        );

        List<Error> errors = validators.validate(query);

        assertEquals(List.of(Errors.INVALID_SERVICE_TYPE_ID), errors);
    }

    @Test
    void shouldReturnInvalidServiceTypeIdWhenMalformed() {
        Query query = new Query(
                dealershipId, "invalid-uuid", date, null
        );

        List<Error> errors = validators.validate(query);

        assertEquals(List.of(Errors.INVALID_SERVICE_TYPE_ID), errors);
    }

    @Test
    void shouldReturnInvalidDateWhenNull() {
        Query query = new Query(
                dealershipId, serviceTypeId, null, null
        );

        List<Error> errors = validators.validate(query);

        assertEquals(List.of(Errors.INVALID_DATE), errors);
    }

    @Test
    void shouldReturnInvalidTechnicianIdWhenMalformed() {
        Query query = new Query(
                dealershipId, serviceTypeId, date, "invalid-uuid"
        );

        List<Error> errors = validators.validate(query);

        assertEquals(List.of(Errors.INVALID_TECHNICIAN_ID), errors);
    }

    @Test
    void shouldReturnSuccessWhenTechnicianIdIsNull() {
        Query query = new Query(
                dealershipId, serviceTypeId, date, null
        );

        List<Error> errors = validators.validate(query);

        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldReturnSuccessWhenTechnicianIdIsBlank() {
        Query query = new Query(
                dealershipId, serviceTypeId, date, " "
        );

        List<Error> errors = validators.validate(query);

        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldReturnSuccessWhenAllFieldsAreValid() {
        Query query = new Query(
                dealershipId, serviceTypeId, date, technicianId
        );

        List<Error> errors = validators.validate(query);

        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldReturnOnlyFirstValidationError() {
        Query query = new Query(
                "invalid", "invalid", null, "invalid"
        );

        List<Error> errors = validators.validate(query);

        assertEquals(1, errors.size());
        assertEquals(Errors.INVALID_DEALERSHIP_ID, errors.get(0));
    }
}

