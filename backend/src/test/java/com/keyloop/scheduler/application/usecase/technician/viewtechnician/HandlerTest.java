
package com.keyloop.scheduler.application.usecase.technician.viewtechnician;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository.TechnicianData;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository.TechnicianDetailsData;
import com.keyloop.scheduler.application.common.result.Error;
import com.keyloop.scheduler.application.common.result.Result;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private TechnicianRepository technicianRepository;

    private Validators validators;

    private Handler handler;

    private static final String DEALERSHIP_ID =
            "10000000-0000-0000-0000-000000000001";

    private static final UUID DEALERSHIP_UUID =
            UUID.fromString(DEALERSHIP_ID);

    private static final UUID TECHNICIAN_ID_1 =
            UUID.fromString("40000000-0000-0000-0000-000000000001");

    private static final UUID TECHNICIAN_ID_2 =
            UUID.fromString("40000000-0000-0000-0000-000000000002");

    private static final UUID SERVICE_TYPE_ID_1 =
            UUID.fromString("30000000-0000-0000-0000-000000000001");

    private static final UUID SERVICE_TYPE_ID_2 =
            UUID.fromString("30000000-0000-0000-0000-000000000002");

    private static final UUID SERVICE_TYPE_ID_3 =
            UUID.fromString("30000000-0000-0000-0000-000000000009");

    private static final String DEALERSHIP_NAME =
            "Keyloop HCMC Central";

    @BeforeEach
    void setUp() {
        validators = new Validators();
        handler = new Handler(technicianRepository, validators);
    }

    @Test
    void shouldReturnSuccessWhenTechniciansExist() {
        // Arrange
        Query query = new Query(DEALERSHIP_ID);

        List<TechnicianData> rows = List.of(
                createTechnicianDetails(
                        TECHNICIAN_ID_1,
                        "John Smith",
                        true,
                        SERVICE_TYPE_ID_1,
                        "Oil Change"
                ),
                createTechnicianDetails(
                        TECHNICIAN_ID_1,
                        "John Smith",
                        true,
                        SERVICE_TYPE_ID_2,
                        "Tire Rotation"
                ),
                createTechnicianDetails(
                        TECHNICIAN_ID_2,
                        "David Wilson",
                        true,
                        SERVICE_TYPE_ID_3,
                        "Full Vehicle Inspection"
                )
        );

        when(technicianRepository.findDetailsByDealershipId(
                DEALERSHIP_UUID
        )).thenReturn(rows);

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        Result.Success<?> success =
                (Result.Success<?>) result;

        Response response = (Response) success.value();

        assertEquals(DEALERSHIP_UUID, response.dealershipId());
        assertEquals(DEALERSHIP_NAME, response.dealershipName());
        assertEquals(2, response.totalItems());
        assertEquals(2, response.items().size());

        // Verify first technician
        Response.TechnicianItem firstTechnician =
                response.items().get(0);

        assertEquals(TECHNICIAN_ID_1, firstTechnician.id());
        assertEquals("John Smith", firstTechnician.fullName());
        assertTrue(firstTechnician.active());

        assertEquals(2, firstTechnician.qualifications().size());

        assertEquals(
                SERVICE_TYPE_ID_1,
                firstTechnician.qualifications().get(0).serviceTypeId()
        );

        assertEquals(
                "Oil Change",
                firstTechnician.qualifications().get(0).serviceName()
        );

        assertEquals(
                SERVICE_TYPE_ID_2,
                firstTechnician.qualifications().get(1).serviceTypeId()
        );

        assertEquals(
                "Tire Rotation",
                firstTechnician.qualifications().get(1).serviceName()
        );

        // Verify second technician
        Response.TechnicianItem secondTechnician =
                response.items().get(1);

        assertEquals(TECHNICIAN_ID_2, secondTechnician.id());
        assertEquals("David Wilson", secondTechnician.fullName());
        assertTrue(secondTechnician.active());

        assertEquals(1, secondTechnician.qualifications().size());

        assertEquals(
                SERVICE_TYPE_ID_3,
                secondTechnician.qualifications().get(0).serviceTypeId()
        );

        assertEquals(
                "Full Vehicle Inspection",
                secondTechnician.qualifications().get(0).serviceName()
        );

        verify(technicianRepository).findDetailsByDealershipId(
                DEALERSHIP_UUID
        );
        verifyNoMoreInteractions(technicianRepository);
    }

    @Test
    void shouldReturnSuccessWhenTechnicianHasNoQualifications() {
        // Arrange
        Query query = new Query(DEALERSHIP_ID);

        List<TechnicianData> rows = List.of(
                createTechnicianDetails(
                        TECHNICIAN_ID_1,
                        "John Smith",
                        true,
                        null,
                        null
                )
        );

        when(technicianRepository.findDetailsByDealershipId(
                DEALERSHIP_UUID
        )).thenReturn(rows);

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        Result.Success<?> success =
                (Result.Success<?>) result;

        Response response = (Response) success.value();

        assertEquals(1, response.totalItems());
        assertEquals(1, response.items().size());

        Response.TechnicianItem technician =
                response.items().get(0);

        assertEquals(TECHNICIAN_ID_1, technician.id());
        assertEquals("John Smith", technician.fullName());
        assertTrue(technician.qualifications().isEmpty());

        verify(technicianRepository).findDetailsByDealershipId(
                DEALERSHIP_UUID
        );
        verifyNoMoreInteractions(technicianRepository);
    }

    @Test
    void shouldReturnSuccessWhenNoTechniciansExist() {
        // Arrange
        Query query = new Query(DEALERSHIP_ID);

        List<TechnicianData> rows = List.of(
                createTechnicianDetails(
                        null,
                        null,
                        false,
                        null,
                        null
                )
        );

        when(technicianRepository.findDetailsByDealershipId(
                DEALERSHIP_UUID
        )).thenReturn(rows);

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        Result.Success<?> success =
                (Result.Success<?>) result;

        Response response = (Response) success.value();

        assertEquals(DEALERSHIP_UUID, response.dealershipId());
        assertEquals(DEALERSHIP_NAME, response.dealershipName());
        assertTrue(response.items().isEmpty());
        assertEquals(0, response.totalItems());

        verify(technicianRepository).findDetailsByDealershipId(
                DEALERSHIP_UUID
        );
        verifyNoMoreInteractions(technicianRepository);
    }

    @Test
    void shouldReturnFailureWhenDealershipDoesNotExist() {
        // Arrange
        Query query = new Query(DEALERSHIP_ID);

        when(technicianRepository.findDetailsByDealershipId(
                DEALERSHIP_UUID
        )).thenReturn(List.of());

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isFailure());

        Result.Failure<?> failure =
                (Result.Failure<?>) result;

        assertEquals(1, failure.errorDetails().errors().size());

        Error error = failure.errorDetails().errors().get(0);

        assertEquals(
                Errors.DEALERSHIP_NOT_FOUND.code(),
                error.code()
        );

        assertEquals(
                Errors.DEALERSHIP_NOT_FOUND.type(),
                error.type()
        );

        verify(technicianRepository).findDetailsByDealershipId(
                DEALERSHIP_UUID
        );
        verifyNoMoreInteractions(technicianRepository);
    }

    @Test
    void shouldReturnFailureWhenDealershipIdIsInvalid() {
        // Arrange
        Query query = new Query("invalid-uuid");

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isFailure());

        Result.Failure<?> failure =
                (Result.Failure<?>) result;

        assertEquals(1, failure.errorDetails().errors().size());

        Error error = failure.errorDetails().errors().get(0);

        assertEquals(
                Errors.INVALID_DEALERSHIP_ID.code(),
                error.code()
        );

        // Repository must not be called
        verifyNoInteractions(technicianRepository);
    }

    @Test
    void shouldReturnFailureWhenQueryIsNull() {
        // Arrange
        Query query = null;

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isFailure());

        Result.Failure<?> failure =
                (Result.Failure<?>) result;

        assertEquals(1, failure.errorDetails().errors().size());

        Error error = failure.errorDetails().errors().get(0);

        assertEquals(
                Errors.INVALID_DEALERSHIP_ID.code(),
                error.code()
        );

        verifyNoInteractions(technicianRepository);
    }

    @Test
    void shouldReturnFailureWhenDatabaseThrowsException() {
        // Arrange
        Query query = new Query(DEALERSHIP_ID);

        when(technicianRepository.findDetailsByDealershipId(
                DEALERSHIP_UUID
        )).thenThrow(
                new DataAccessResourceFailureException(
                        "Database unavailable"
                )
        );

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isFailure());

        Result.Failure<?> failure =
                (Result.Failure<?>) result;

        assertFalse(failure.errorDetails().errors().isEmpty());

        Error error = failure.errorDetails().errors().get(0);

        assertEquals(
                Errors.TECHNICIAN_RETRIEVAL_FAILED.code(),
                error.code()
        );

        assertEquals(
                Errors.TECHNICIAN_RETRIEVAL_FAILED.type(),
                error.type()
        );

        verify(technicianRepository).findDetailsByDealershipId(
                DEALERSHIP_UUID
        );
        verifyNoMoreInteractions(technicianRepository);
    }

    private TechnicianData createTechnicianDetails(
            UUID technicianId,
            String fullName,
            boolean active,
            UUID serviceTypeId,
            String serviceName
    ) {
        return new TechnicianData(
                DEALERSHIP_UUID,
                DEALERSHIP_NAME,
                technicianId,
                fullName,
                active,
                serviceTypeId,
                serviceName
        );
    }
}

