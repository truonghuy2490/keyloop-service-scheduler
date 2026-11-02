package com.keyloop.scheduler.application.usecase.technician.viewtechniciandetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository.DateOverrideData;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository.QualificationData;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository.TechnicianDetailsData;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository.WorkingHoursData;
import com.keyloop.scheduler.application.common.result.Result;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private TechnicianRepository technicianRepository;

    private Validators validators;
    private Handler handler;

    private final UUID technicianId =
            UUID.fromString("40000000-0000-0000-0000-000000000001");

    private final UUID dealershipId =
            UUID.fromString("10000000-0000-0000-0000-000000000001");

    @BeforeEach
    void setUp() {
        validators = new Validators();
        handler = new Handler(technicianRepository, validators);
    }

    @Test
    void handle_shouldReturnTechnicianDetails_whenTechnicianExists() {

        // Arrange
        Query query = new Query(technicianId.toString());

        TechnicianDetailsData technician =
                createTechnicianDetails();

        when(technicianRepository.findDetailsByTechnicianId(technicianId))
                .thenReturn(Optional.of(technician));

        // Act
        var result = handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        Response response =
                (Response) ((Result.Success<?>) result).value();

        assertEquals(technicianId, response.id());
        assertEquals("John Smith", response.fullName());
        assertTrue(response.active());

        assertEquals(dealershipId, response.dealership().id());
        assertEquals("Keyloop HCMC Central", response.dealership().name());

        // Qualifications
        assertEquals(2, response.qualifications().size());

        assertEquals(
                "Oil Change",
                response.qualifications().get(0).name()
        );

        assertEquals(
                "Tire Rotation",
                response.qualifications().get(1).name()
        );

        // Working hours
        assertEquals(2, response.workingHours().size());

        Response.WorkingHoursItem monday =
                response.workingHours().get(0);

        assertEquals(DayOfWeek.MONDAY, monday.dayOfWeek());
        assertEquals(2, monday.intervals().size());

        assertEquals(
                LocalTime.of(8, 0),
                monday.intervals().get(0).startsAt()
        );

        assertEquals(
                LocalTime.of(12, 0),
                monday.intervals().get(0).endsAt()
        );

        assertEquals(
                LocalTime.of(13, 0),
                monday.intervals().get(1).startsAt()
        );

        assertEquals(
                LocalTime.of(17, 0),
                monday.intervals().get(1).endsAt()
        );

        // Date overrides
        assertEquals(1, response.dateOverrides().size());

        Response.DateOverrideItem dateOverride =
                response.dateOverrides().get(0);

        assertEquals(
                LocalDate.of(2026, 11, 5),
                dateOverride.workDate()
        );

        assertTrue(dateOverride.isUnavailable());
        assertNull(dateOverride.startsAt());
        assertNull(dateOverride.endsAt());
        assertEquals("Personal leave", dateOverride.reason());

        // Audit fields
        assertEquals(
                Instant.parse("2026-09-01T08:00:00Z"),
                response.createdAt()
        );

        assertEquals(
                Instant.parse("2026-10-20T08:00:00Z"),
                response.updatedAt()
        );

        verify(technicianRepository)
                .findDetailsByTechnicianId(technicianId);

        verifyNoMoreInteractions(technicianRepository);
    }

    @Test
    void handle_shouldReturnEmptyCollections_whenTechnicianHasNoRelatedData() {

        // Arrange
        Query query = new Query(technicianId.toString());

        TechnicianDetailsData technician =
                new TechnicianDetailsData(
                        technicianId,
                        "John Smith",
                        true,
                        dealershipId,
                        "Keyloop HCMC Central",
                        Instant.parse("2026-09-01T08:00:00Z"),
                        Instant.parse("2026-10-20T08:00:00Z"),
                        List.of(),
                        List.of(),
                        List.of()
                );

        when(technicianRepository.findDetailsByTechnicianId(technicianId))
                .thenReturn(Optional.of(technician));

        // Act
        var result = handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        Response response =
                (Response) ((Result.Success<?>) result).value();

        assertTrue(response.qualifications().isEmpty());
        assertTrue(response.workingHours().isEmpty());
        assertTrue(response.dateOverrides().isEmpty());

        verify(technicianRepository)
                .findDetailsByTechnicianId(technicianId);

        verifyNoMoreInteractions(technicianRepository);
    }

    @Test
    void handle_shouldReturnNotFound_whenTechnicianDoesNotExist() {

        // Arrange
        Query query = new Query(technicianId.toString());

        when(technicianRepository.findDetailsByTechnicianId(technicianId))
                .thenReturn(Optional.empty());

        // Act
        var result = handler.handle(query).join();

        // Assert
        assertTrue(result.isFailure());

        var failure = (Result.Failure<?>) result;

        var error = failure.errorDetails().errors().get(0);

        assertEquals(
                Errors.TECHNICIAN_NOT_FOUND.code(),
                error.code()
        );

        verify(technicianRepository)
                .findDetailsByTechnicianId(technicianId);

        verifyNoMoreInteractions(technicianRepository);
    }

    @Test
    void handle_shouldReturnValidationError_whenTechnicianIdIsInvalid() {

        // Arrange
        Query query = new Query("invalid-uuid");

        // Act
        var result = handler.handle(query).join();

        // Assert
        assertTrue(result.isFailure());

        var failure = (Result.Failure<?>) result;

        var error = failure.errorDetails().errors().get(0);

        assertEquals(
                Errors.INVALID_TECHNICIAN_ID.code(),
                error.code()
        );

        verifyNoInteractions(technicianRepository);
    }

    @Test
    void handle_shouldReturnValidationError_whenRequestIsNull() {

        // Act
        var result = handler.handle(null).join();

        // Assert
        assertTrue(result.isFailure());

        var failure = (Result.Failure<?>) result;

        var error = failure.errorDetails().errors().get(0);

        assertEquals(
                Errors.INVALID_TECHNICIAN_ID.code(),
                error.code()
        );

        verifyNoInteractions(technicianRepository);
    }

    @Test
    void handle_shouldReturnFailure_whenDatabaseAccessFails() {

        // Arrange
        Query query = new Query(technicianId.toString());

        when(technicianRepository.findDetailsByTechnicianId(technicianId))
                .thenThrow(
                        new DataAccessResourceFailureException(
                                "Database connection failed"
                        )
                );

        // Act
        var result = handler.handle(query).join();

        // Assert
        assertTrue(result.isFailure());

        var failure = (Result.Failure<?>) result;

        var error = failure.errorDetails().errors().get(0);

        assertEquals(
                Errors.TECHNICIAN_DETAILS_RETRIEVAL_FAILED.code(),
                error.code()
        );

        verify(technicianRepository)
                .findDetailsByTechnicianId(technicianId);

        verifyNoMoreInteractions(technicianRepository);
    }

    private TechnicianDetailsData createTechnicianDetails() {

        List<QualificationData> qualifications = List.of(
                new QualificationData(
                        UUID.fromString(
                                "30000000-0000-0000-0000-000000000001"
                        ),
                        "Oil Change",
                        "Replace engine oil and oil filter",
                        30,
                        Instant.parse("2026-10-01T08:00:00Z")
                ),
                new QualificationData(
                        UUID.fromString(
                                "30000000-0000-0000-0000-000000000002"
                        ),
                        "Tire Rotation",
                        "Rotate vehicle tires",
                        45,
                        Instant.parse("2026-10-01T08:00:00Z")
                )
        );

        List<WorkingHoursData> workingHours = List.of(
                new WorkingHoursData(
                        DayOfWeek.MONDAY,
                        LocalTime.of(8, 0),
                        LocalTime.of(12, 0)
                ),
                new WorkingHoursData(
                        DayOfWeek.MONDAY,
                        LocalTime.of(13, 0),
                        LocalTime.of(17, 0)
                ),
                new WorkingHoursData(
                        DayOfWeek.TUESDAY,
                        LocalTime.of(8, 0),
                        LocalTime.of(17, 0)
                )
        );

        List<DateOverrideData> dateOverrides = List.of(
                new DateOverrideData(
                        LocalDate.of(2026, 11, 5),
                        true,
                        null,
                        null,
                        "Personal leave"
                )
        );

        return new TechnicianDetailsData(
                technicianId,
                "John Smith",
                true,
                dealershipId,
                "Keyloop HCMC Central",
                Instant.parse("2026-09-01T08:00:00Z"),
                Instant.parse("2026-10-20T08:00:00Z"),
                qualifications,
                workingHours,
                dateOverrides
        );
    }
}
