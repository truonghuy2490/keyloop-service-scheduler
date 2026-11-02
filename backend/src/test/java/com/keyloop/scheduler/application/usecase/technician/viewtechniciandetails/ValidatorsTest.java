package com.keyloop.scheduler.application.usecase.technician.viewtechniciandetails;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.keyloop.scheduler.application.common.result.Error;

class ValidatorsTest {

    private Validators validators;

    private final String validTechnicianId =
            "40000000-0000-0000-0000-000000000001";

    @BeforeEach
    void setUp() {
        validators = new Validators();
    }

    @Test
    void validate_shouldReturnNoErrors_whenTechnicianIdIsValid() {

        // Arrange
        Query query = new Query(validTechnicianId);

        // Act
        List<Error> errors = validators.validate(query);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void validate_shouldReturnNoErrors_whenTechnicianIdIsUppercase() {

        // Arrange
        Query query = new Query(validTechnicianId.toUpperCase());

        // Act
        List<Error> errors = validators.validate(query);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void validate_shouldReturnError_whenQueryIsNull() {

        // Act
        List<Error> errors = validators.validate(null);

        // Assert
        assertEquals(1, errors.size());
        assertEquals(Errors.INVALID_TECHNICIAN_ID, errors.get(0));
    }

    @Test
    void validate_shouldReturnError_whenTechnicianIdIsNull() {

        // Arrange
        Query query = new Query(null);

        // Act
        List<Error> errors = validators.validate(query);

        // Assert
        assertEquals(1, errors.size());
        assertEquals(Errors.INVALID_TECHNICIAN_ID, errors.get(0));
    }

    @Test
    void validate_shouldReturnError_whenTechnicianIdIsEmpty() {

        // Arrange
        Query query = new Query("");

        // Act
        List<Error> errors = validators.validate(query);

        // Assert
        assertEquals(1, errors.size());
        assertEquals(Errors.INVALID_TECHNICIAN_ID, errors.get(0));
    }

    @Test
    void validate_shouldReturnError_whenTechnicianIdIsBlank() {

        // Arrange
        Query query = new Query("   ");

        // Act
        List<Error> errors = validators.validate(query);

        // Assert
        assertEquals(1, errors.size());
        assertEquals(Errors.INVALID_TECHNICIAN_ID, errors.get(0));
    }

    @Test
    void validate_shouldReturnError_whenTechnicianIdHasInvalidFormat() {

        // Arrange
        Query query = new Query("invalid-uuid");

        // Act
        List<Error> errors = validators.validate(query);

        // Assert
        assertEquals(1, errors.size());
        assertEquals(Errors.INVALID_TECHNICIAN_ID, errors.get(0));
    }

    @Test
    void validate_shouldReturnError_whenTechnicianIdIsNotCanonicalUuid() {

        // Arrange
        Query query = new Query("40000000000000000000000000000001");

        // Act
        List<Error> errors = validators.validate(query);

        // Assert
        assertEquals(1, errors.size());
        assertEquals(Errors.INVALID_TECHNICIAN_ID, errors.get(0));
    }
}
