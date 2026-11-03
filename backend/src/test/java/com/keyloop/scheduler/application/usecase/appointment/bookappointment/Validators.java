
package com.keyloop.scheduler.application.usecase.appointment.bookappointment;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidatorsTest {

    private Validators validators;

    private static final String VALID_UUID = "550e8400-e29b-41d4-a716-446655440000";

    private static final OffsetDateTime VALID_START_TIME = OffsetDateTime.parse("2026-11-05T09:00:00+07:00");

    @BeforeEach
    void setUp() {
        validators = new Validators();
    }

    @Test
    void shouldPassWhenRequestIsValid() {
        var errors = validators.validate(validRequest());

        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldFailWhenRequestIsNull() {
        var errors = validators.validate(null);

        assertEquals(1, errors.size());
        assertEquals(Errors.INVALID_REQUEST, errors.get(0));
    }

    @Test
    void shouldFailWhenCustomerIdIsNull() {
        Request request = requestWith(
                null, VALID_UUID, VALID_UUID,
                VALID_UUID, VALID_UUID, VALID_START_TIME);

        assertValidationError(request, Errors.INVALID_CUSTOMER_ID);
    }

    @Test
    void shouldFailWhenCustomerIdIsInvalid() {
        Request request = requestWith(
                "invalid-uuid", VALID_UUID, VALID_UUID,
                VALID_UUID, VALID_UUID, VALID_START_TIME);

        assertValidationError(request, Errors.INVALID_CUSTOMER_ID);
    }

    @Test
    void shouldFailWhenCustomerIdIsBlank() {
        Request request = requestWith(
                " ", VALID_UUID, VALID_UUID,
                VALID_UUID, VALID_UUID, VALID_START_TIME);

        assertValidationError(request, Errors.INVALID_CUSTOMER_ID);
    }

    @Test
    void shouldFailWhenVehicleIdIsInvalid() {
        Request request = requestWith(
                VALID_UUID, "invalid-uuid", VALID_UUID,
                VALID_UUID, VALID_UUID, VALID_START_TIME);

        assertValidationError(request, Errors.INVALID_VEHICLE_ID);
    }

    @Test
    void shouldFailWhenDealershipIdIsInvalid() {
        Request request = requestWith(
                VALID_UUID, VALID_UUID, "invalid-uuid",
                VALID_UUID, VALID_UUID, VALID_START_TIME);

        assertValidationError(request, Errors.INVALID_DEALERSHIP_ID);
    }

    @Test
    void shouldFailWhenServiceTypeIdIsInvalid() {
        Request request = requestWith(
                VALID_UUID, VALID_UUID, VALID_UUID,
                "invalid-uuid", VALID_UUID, VALID_START_TIME);

        assertValidationError(request, Errors.INVALID_SERVICE_TYPE_ID);
    }

    @Test
    void shouldFailWhenTechnicianIdIsInvalid() {
        Request request = requestWith(
                VALID_UUID, VALID_UUID, VALID_UUID,
                VALID_UUID, "invalid-uuid", VALID_START_TIME);

        assertValidationError(request, Errors.INVALID_TECHNICIAN_ID);
    }

    @Test
    void shouldFailWhenStartTimeIsNull() {
        Request request = requestWith(
                VALID_UUID, VALID_UUID, VALID_UUID,
                VALID_UUID, VALID_UUID, null);

        assertValidationError(request, Errors.INVALID_START_TIME);
    }

    @Test
    void shouldPassWhenUUIDContainsUppercaseLetters() {
        Request request = requestWith(
                VALID_UUID.toUpperCase(),
                VALID_UUID, VALID_UUID,
                VALID_UUID, VALID_UUID, VALID_START_TIME);

        assertTrue(validators.validate(request).isEmpty());
    }

    @Test
    void shouldFailWhenUUIDHasInvalidFormat() {
        Request request = requestWith(
                "550e8400-e29b-41d4-a716-44665544000",
                VALID_UUID, VALID_UUID,
                VALID_UUID, VALID_UUID, VALID_START_TIME);

        assertValidationError(request, Errors.INVALID_CUSTOMER_ID);
    }

    @Test
    void shouldReturnOnlyFirstValidationError() {
        Request request = requestWith(
                "invalid-customer",
                "invalid-vehicle",
                VALID_UUID,
                VALID_UUID,
                VALID_UUID,
                null);

        var errors = validators.validate(request);

        assertEquals(1, errors.size());
        assertEquals(Errors.INVALID_CUSTOMER_ID, errors.get(0));
    }

    private void assertValidationError(
            Request request,
            com.keyloop.scheduler.application.common.result.Error expected) {
        var errors = validators.validate(request);

        assertEquals(1, errors.size());
        assertEquals(expected, errors.get(0));
    }

    private Request validRequest() {
        return requestWith(
                VALID_UUID, VALID_UUID, VALID_UUID,
                VALID_UUID, VALID_UUID, VALID_START_TIME);
    }

    private Request requestWith(
            String customerId,
            String vehicleId,
            String dealershipId,
            String serviceTypeId,
            String technicianId,
            OffsetDateTime startTime) {
        return new Request(
                customerId,
                vehicleId,
                dealershipId,
                serviceTypeId,
                technicianId,
                startTime);
    }
}
