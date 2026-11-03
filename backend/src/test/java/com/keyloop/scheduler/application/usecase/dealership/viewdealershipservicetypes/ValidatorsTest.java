
package com.keyloop.scheduler.application.usecase.dealership.viewdealershipservicetypes;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.keyloop.scheduler.application.common.result.Error;

class ValidatorsTest {

    private Validators validators;

    private static final String VALID_UUID =
            "550e8400-e29b-41d4-a716-446655440000";

    private static final int PAGE_NUMBER = 1;
    private static final int PAGE_SIZE = 10;

    @BeforeEach
    void setUp() {
        validators = new Validators();
    }

    @Test
    void shouldPassWhenQueryIsValid() {

        // Arrange
        Query query = new Query(
                VALID_UUID,
                PAGE_NUMBER,
                PAGE_SIZE
        );

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
        assertValidationError(errors, Errors.INVALID_QUERY);
    }

    @Test
    void shouldFailWhenDealershipIdIsInvalidUUID() {

        // Arrange
        Query query = new Query(
                "invalid-uuid",
                PAGE_NUMBER,
                PAGE_SIZE
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(errors, Errors.INVALID_DEALERSHIP_ID);
    }

    @Test
    void shouldFailWhenDealershipIdIsNull() {

        // Arrange
        Query query = new Query(
                null,
                PAGE_NUMBER,
                PAGE_SIZE
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(errors, Errors.INVALID_DEALERSHIP_ID);
    }

    @Test
    void shouldFailWhenDealershipIdIsBlank() {

        // Arrange
        Query query = new Query(
                " ",
                PAGE_NUMBER,
                PAGE_SIZE
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(errors, Errors.INVALID_DEALERSHIP_ID);
    }

    @Test
    void shouldFailWhenDealershipIdIsEmpty() {

        // Arrange
        Query query = new Query(
                "",
                PAGE_NUMBER,
                PAGE_SIZE
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(errors, Errors.INVALID_DEALERSHIP_ID);
    }

    @Test
    void shouldPassWhenDealershipIdContainsUppercaseUUID() {

        // Arrange
        Query query = new Query(
                VALID_UUID.toUpperCase(),
                PAGE_NUMBER,
                PAGE_SIZE
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
                "550e8400-e29b-41d4-a716-44665544000",
                PAGE_NUMBER,
                PAGE_SIZE
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(errors, Errors.INVALID_DEALERSHIP_ID);
    }

    @Test
    void shouldFailWhenDealershipIdHasExtraCharacters() {

        // Arrange
        Query query = new Query(
                VALID_UUID + "-extra",
                PAGE_NUMBER,
                PAGE_SIZE
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(errors, Errors.INVALID_DEALERSHIP_ID);
    }

    @Test
    void shouldFailWhenPageNumberIsZero() {

        // Arrange
        Query query = new Query(VALID_UUID, 0, PAGE_SIZE);

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(errors, Errors.INVALID_PAGE_NUMBER);
    }

    @Test
    void shouldFailWhenPageNumberIsNegative() {

        // Arrange
        Query query = new Query(VALID_UUID, -1, PAGE_SIZE);

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(errors, Errors.INVALID_PAGE_NUMBER);
    }

    @Test
    void shouldFailWhenPageSizeIsZero() {

        // Arrange
        Query query = new Query(VALID_UUID, PAGE_NUMBER, 0);

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(errors, Errors.INVALID_PAGE_SIZE);
    }

    @Test
    void shouldFailWhenPageSizeIsNegative() {

        // Arrange
        Query query = new Query(VALID_UUID, PAGE_NUMBER, -1);

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(errors, Errors.INVALID_PAGE_SIZE);
    }

    @Test
    void shouldFailWhenPageSizeExceedsMaximum() {

        // Arrange
        Query query = new Query(VALID_UUID, PAGE_NUMBER, 101);

        // Act
        var errors = validators.validate(query);

        // Assert
        assertValidationError(errors, Errors.INVALID_PAGE_SIZE);
    }

    @Test
    void shouldPassWhenPageSizeIsMaximumAllowed() {

        // Arrange
        Query query = new Query(VALID_UUID, PAGE_NUMBER, 100);

        // Act
        var errors = validators.validate(query);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldPassWhenPageSizeIsMinimumAllowed() {

        // Arrange
        Query query = new Query(VALID_UUID, PAGE_NUMBER, 1);

        // Act
        var errors = validators.validate(query);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldReturnMultipleErrorsWhenQueryHasMultipleInvalidFields() {

        // Arrange
        Query query = new Query(
                "invalid-uuid",
                0,
                101
        );

        // Act
        var errors = validators.validate(query);

        // Assert
        assertEquals(3, errors.size());

        assertEquals(
                Errors.INVALID_DEALERSHIP_ID,
                errors.get(0)
        );

        assertEquals(
                Errors.INVALID_PAGE_NUMBER,
                errors.get(1)
        );

        assertEquals(
                Errors.INVALID_PAGE_SIZE,
                errors.get(2)
        );
    }

    private void assertValidationError(
            List<Error> errors,
            Error expected
    ) {
        assertEquals(1, errors.size());
        assertEquals(expected, errors.get(0));
    }
}
