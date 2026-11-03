
package com.keyloop.scheduler.application.usecase.dealership.viewdealershipservicetypes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import com.keyloop.scheduler.application.abstraction.dataaccess.DealershipRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.DealershipRepository.ServiceTypeData;
import com.keyloop.scheduler.application.common.result.Error;
import com.keyloop.scheduler.application.common.result.Result;
import com.keyloop.scheduler.domain.entity.Dealership;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private DealershipRepository dealershipRepository;

    private Handler handler;

    private UUID dealershipId;
    private Query query;

    @BeforeEach
    void setUp() {

        dealershipId = UUID.randomUUID();
        query = new Query(dealershipId.toString());

        Validators validators = new Validators();

        handler = new Handler(
                validators,
                dealershipRepository
        );
    }

    @Test
    void shouldReturnSuccessWhenDealershipHasServiceTypes() {

        // Arrange
        mockActiveDealership();

        UUID oilChangeId = UUID.randomUUID();
        UUID brakeInspectionId = UUID.randomUUID();

        List<ServiceTypeData> serviceTypes = List.of(
                new ServiceTypeData(
                        oilChangeId,
                        "Oil Change",
                        "Engine oil and filter replacement",
                        60
                ),
                new ServiceTypeData(
                        brakeInspectionId,
                        "Brake Inspection",
                        "Brake system inspection",
                        90
                )
        );

        when(dealershipRepository.findActiveByDealershipId(dealershipId))
                .thenReturn(serviceTypes);

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        Response response = getSuccessValue(result);

        assertNotNull(response);
        assertNotNull(response.items());
        assertEquals(2, response.items().size());

        // Verify first service type
        Response.ServiceTypeItem oilChange =
                response.items().get(0);

        assertEquals(oilChangeId, oilChange.id());
        assertEquals("Oil Change", oilChange.name());
        assertEquals(
                "Engine oil and filter replacement",
                oilChange.description()
        );
        assertEquals(60, oilChange.durationMinutes());

        // Verify second service type
        Response.ServiceTypeItem brakeInspection =
                response.items().get(1);

        assertEquals(brakeInspectionId, brakeInspection.id());
        assertEquals(
                "Brake Inspection",
                brakeInspection.name()
        );
        assertEquals(
                "Brake system inspection",
                brakeInspection.description()
        );
        assertEquals(90, brakeInspection.durationMinutes());

        verify(dealershipRepository).findById(dealershipId);

        verify(dealershipRepository)
                .findActiveByDealershipId(dealershipId);

        verifyNoMoreInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnSuccessWhenDealershipHasNoServiceTypes() {

        // Arrange
        mockActiveDealership();

        when(dealershipRepository.findActiveByDealershipId(dealershipId))
                .thenReturn(List.of());

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        Response response = getSuccessValue(result);

        assertNotNull(response.items());
        assertTrue(response.items().isEmpty());

        verify(dealershipRepository).findById(dealershipId);

        verify(dealershipRepository)
                .findActiveByDealershipId(dealershipId);

        verifyNoMoreInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenDealershipDoesNotExist() {

        // Arrange
        when(dealershipRepository.findById(dealershipId))
                .thenReturn(Optional.empty());

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertError(
                result,
                Errors.DEALERSHIP_NOT_FOUND
        );

        verify(dealershipRepository).findById(dealershipId);

        verifyNoMoreInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenDealershipIsDeleted() {

        // Arrange
        Dealership dealership = mock(Dealership.class);

        when(dealership.getIsDeleted()).thenReturn(true);

        when(dealershipRepository.findById(dealershipId))
                .thenReturn(Optional.of(dealership));

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertError(
                result,
                Errors.DEALERSHIP_NOT_FOUND
        );

        verify(dealershipRepository).findById(dealershipId);

        verifyNoMoreInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenQueryIsNull() {

        // Act
        Result<Response> result = handler.handle(null).join();

        // Assert
        assertError(
                result,
                Errors.INVALID_QUERY
        );

        verifyNoInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenDealershipIdIsInvalid() {

        // Arrange
        Query invalidQuery = new Query("invalid-uuid");

        // Act
        Result<Response> result = handler.handle(invalidQuery).join();

        // Assert
        assertError(
                result,
                Errors.INVALID_DEALERSHIP_ID
        );

        verifyNoInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenDealershipIdIsNull() {

        // Arrange
        Query invalidQuery = new Query(null);

        // Act
        Result<Response> result = handler.handle(invalidQuery).join();

        // Assert
        assertError(
                result,
                Errors.INVALID_DEALERSHIP_ID
        );

        verifyNoInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenDealershipIdIsBlank() {

        // Arrange
        Query invalidQuery = new Query(" ");

        // Act
        Result<Response> result = handler.handle(invalidQuery).join();

        // Assert
        assertError(
                result,
                Errors.INVALID_DEALERSHIP_ID
        );

        verifyNoInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenDatabaseThrowsExceptionOnDealershipLookup() {

        // Arrange
        when(dealershipRepository.findById(dealershipId))
                .thenThrow(
                        new DataAccessResourceFailureException(
                                "Database unavailable"
                        )
                );

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertError(
                result,
                Errors.SERVICE_TYPES_RETRIEVAL_FAILED
        );

        verify(dealershipRepository).findById(dealershipId);

        verifyNoMoreInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenDatabaseThrowsExceptionOnServiceTypeLookup() {

        // Arrange
        mockActiveDealership();

        when(dealershipRepository.findActiveByDealershipId(dealershipId))
                .thenThrow(
                        new DataAccessResourceFailureException(
                                "Failed to retrieve service types"
                        )
                );

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertError(
                result,
                Errors.SERVICE_TYPES_RETRIEVAL_FAILED
        );

        verify(dealershipRepository).findById(dealershipId);

        verify(dealershipRepository)
                .findActiveByDealershipId(dealershipId);

        verifyNoMoreInteractions(dealershipRepository);
    }

    // =========================================================
    // Helpers
    // =========================================================

    private void mockActiveDealership() {

        Dealership dealership = mock(Dealership.class);

        when(dealershipRepository.findById(dealershipId))
                .thenReturn(Optional.of(dealership));
    }

    private Response getSuccessValue(Result<Response> result) {

        assertTrue(result.isSuccess());

        Result.Success<?> success =
                (Result.Success<?>) result;

        return (Response) success.value();
    }

    private Error getFirstError(Result<Response> result) {

        assertTrue(result.isFailure());

        Result.Failure<?> failure =
                (Result.Failure<?>) result;

        assertFalse(
                failure.errorDetails().errors().isEmpty()
        );

        return failure.errorDetails().errors().get(0);
    }

    private void assertError(
            Result<Response> result,
            Error expected
    ) {

        Error actual = getFirstError(result);

        assertEquals(expected.code(), actual.code());
        assertEquals(expected.type(), actual.type());
        assertEquals(expected.property(), actual.property());
    }
}
