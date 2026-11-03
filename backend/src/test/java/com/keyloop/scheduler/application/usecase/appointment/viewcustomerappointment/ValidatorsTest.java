package com.keyloop.scheduler.application.usecase.appointment.viewcustomerappointment;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidatorsTest {

    private Validators validators;

    @BeforeEach
    void setUp() {
        validators = new Validators();
    }

    @Test
    void shouldPassWhenCustomerIdIsValidUUID() {
        // Arrange
        Query query = new Query(
                "550e8400-e29b-41d4-a716-446655440000",
                1,
                10
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldPassWhenCustomerIdContainsUppercaseUUID() {
        // Arrange
        Query query = new Query(
                "550E8400-E29B-41D4-A716-446655440000",
                1,
                10
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldFailWhenCustomerIdIsInvalidUUID() {
        // Arrange
        Query query = new Query("invalid-uuid", 1, 10);

        // Act
        var errors = validators.validate(query);

        // Assert
        assertEquals(1, errors.size());
        assertEquals(Errors.INVALID_CUSTOMER_ID, errors.get(0));
    }

    @Test
    void shouldFailWhenCustomerIdIsNull() {
        // Arrange
        Query query = new Query(null, 1, 10);

        // Act
        var errors = validators.validate(query);

        // Assert
        assertEquals(1, errors.size());
        assertEquals(Errors.INVALID_CUSTOMER_ID, errors.get(0));
    }

    @Test
    void shouldFailWhenCustomerIdIsBlank() {
        // Arrange
        Query query = new Query(" ", 1, 10);

        // Act
        var errors = validators.validate(query);

        // Assert
        assertEquals(1, errors.size());
        assertEquals(Errors.INVALID_CUSTOMER_ID, errors.get(0));
    }

    @Test
    void shouldFailWhenQueryIsNull() {
        // Arrange
        Query query = null;

        // Act
        var errors = validators.validate(query);

        // Assert
        assertEquals(1, errors.size());
        assertEquals(Errors.INVALID_CUSTOMER_ID, errors.get(0));
    }

    @Test
    void shouldFailWhenCustomerIdHasInvalidUUIDFormat() {
        // Arrange
        Query query = new Query(
                "550e8400-e29b-41d4-a716-44665544000",
                1,
                10
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertFalse(errors.isEmpty());
        assertEquals(Errors.INVALID_CUSTOMER_ID, errors.get(0));
    }
}
