
package com.keyloop.scheduler.application.usecase.dealership.viewdealershipservicetypes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.keyloop.scheduler.application.common.result.Error;

class ValidatorsTest {

    private Validators validators;

    private static final String VALID_UUID =
            "550e8400-e29b-41d4-a716-446655440000";

    @BeforeEach
    void setUp() {
        validators = new Validators();
    }

    @Test
    void shouldPassWhenDealershipIdIsValidUUID() {

        // Arrange
        Query query = new Query(VALID_UUID);

        // Act
        var errors = validators.validate(query);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldFailWhenQueryIsNull() {

        // Arrange
        Query query = null;

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(
                errors,
                Errors.INVALID_QUERY
        );
    }

    @Test
    void shouldFailWhenDealershipIdIsInvalidUUID() {

        // Arrange
        Query query = new Query("invalid-uuid");

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(
                errors,
                Errors.INVALID_DEALERSHIP_ID
        );
    }

    @Test
    void shouldFailWhenDealershipIdIsNull() {

        // Arrange
        Query query = new Query(null);

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(
                errors,
                Errors.INVALID_DEALERSHIP_ID
        );
    }

    @Test
    void shouldFailWhenDealershipIdIsBlank() {

        // Arrange
        Query query = new Query(" ");

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(
                errors,
                Errors.INVALID_DEALERSHIP_ID
        );
    }

    @Test
    void shouldFailWhenDealershipIdIsEmpty() {

        // Arrange
        Query query = new Query("");

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(
                errors,
                Errors.INVALID_DEALERSHIP_ID
        );
    }

    @Test
    void shouldPassWhenDealershipIdContainsUppercaseUUID() {

        // Arrange
        Query query = new Query(
                VALID_UUID.toUpperCase()
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldFailWhenDealershipIdHasInvalidUUIDFormat() {

        // Arrange
        Query query = new Query(
                "550e8400-e29b-41d4-a716-44665544000"
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(
                errors,
                Errors.INVALID_DEALERSHIP_ID
        );
    }

    @Test
    void shouldFailWhenDealershipIdHasExtraCharacters() {

        // Arrange
        Query query = new Query(
                VALID_UUID + "-extra"
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(
                errors,
                Errors.INVALID_DEALERSHIP_ID
        );
    }

    private void assertValidationError(
            java.util.List<Error> errors,
            Error expected
    ) {
        assertEquals(1, errors.size());
        assertEquals(expected, errors.get(0));
    }
}

