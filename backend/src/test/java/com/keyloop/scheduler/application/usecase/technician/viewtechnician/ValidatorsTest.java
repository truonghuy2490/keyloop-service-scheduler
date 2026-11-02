
package com.keyloop.scheduler.application.usecase.technician.viewtechnician;

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
    void shouldPassWhenDealershipIdIsValidUUID() {
        // Arrange
        Query query = new Query(
                "10000000-0000-0000-0000-000000000001"
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldPassWhenDealershipIdContainsUppercaseUUID() {
        // Arrange
        Query query = new Query(
                "10000000-0000-0000-0000-000000000001".toUpperCase()
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldFailWhenDealershipIdIsInvalidUUID() {
        // Arrange
        Query query = new Query("invalid-uuid");

        // Act
        var errors = validators.validate(query);

        // Assert
        assertEquals(1, errors.size());
        assertEquals(
                Errors.INVALID_DEALERSHIP_ID,
                errors.get(0)
        );
    }

    @Test
    void shouldFailWhenDealershipIdIsNull() {
        // Arrange
        Query query = new Query(null);

        // Act
        var errors = validators.validate(query);

        // Assert
        assertEquals(1, errors.size());
        assertEquals(
                Errors.INVALID_DEALERSHIP_ID,
                errors.get(0)
        );
    }

    @Test
    void shouldFailWhenDealershipIdIsBlank() {
        // Arrange
        Query query = new Query(" ");

        // Act
        var errors = validators.validate(query);

        // Assert
        assertEquals(1, errors.size());
        assertEquals(
                Errors.INVALID_DEALERSHIP_ID,
                errors.get(0)
        );
    }

    @Test
    void shouldFailWhenQueryIsNull() {
        // Arrange
        Query query = null;

        // Act
        var errors = validators.validate(query);

        // Assert
        assertEquals(1, errors.size());
        assertEquals(
                Errors.INVALID_DEALERSHIP_ID,
                errors.get(0)
        );
    }

    @Test
    void shouldFailWhenDealershipIdHasInvalidUUIDFormat() {
        // Arrange
        Query query = new Query(
                "10000000-0000-0000-0000-00000000000"
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertFalse(errors.isEmpty());
        assertEquals(
                Errors.INVALID_DEALERSHIP_ID,
                errors.get(0)
        );
    }
}

