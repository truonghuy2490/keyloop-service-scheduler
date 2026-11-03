
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
import com.keyloop.scheduler.application.common.pagination.PagedList;
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

    private static final int PAGE_NUMBER = 1;
    private static final int PAGE_SIZE = 10;

    @BeforeEach
    void setUp() {

        dealershipId = UUID.randomUUID();

        query = new Query(
                dealershipId.toString(),
                PAGE_NUMBER,
                PAGE_SIZE
        );

        handler = new Handler(
                new Validators(),
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

        PagedList<ServiceTypeData> pagedServiceTypes =
                PagedList.create(
                        serviceTypes,
                        2,
                        PAGE_NUMBER,
                        PAGE_SIZE
                );

        when(dealershipRepository.findActiveByDealershipId(
                dealershipId,
                PAGE_NUMBER,
                PAGE_SIZE
        )).thenReturn(pagedServiceTypes);

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        PagedList<Response> response = getSuccessValue(result);

        assertNotNull(response);
        assertEquals(2, response.items().size());
        assertEquals(2, response.totalCount());
        assertEquals(PAGE_NUMBER, response.pageNumber());
        assertEquals(PAGE_SIZE, response.pageSize());

        // Verify first service type
        Response oilChange = response.items().get(0);

        assertEquals(oilChangeId, oilChange.id());
        assertEquals("Oil Change", oilChange.name());
        assertEquals(
                "Engine oil and filter replacement",
                oilChange.description()
        );
        assertEquals(60, oilChange.durationMinutes());

        // Verify second service type
        Response brakeInspection = response.items().get(1);

        assertEquals(brakeInspectionId, brakeInspection.id());
        assertEquals("Brake Inspection", brakeInspection.name());
        assertEquals(
                "Brake system inspection",
                brakeInspection.description()
        );
        assertEquals(90, brakeInspection.durationMinutes());

        verify(dealershipRepository).findById(dealershipId);

        verify(dealershipRepository).findActiveByDealershipId(
                dealershipId,
                PAGE_NUMBER,
                PAGE_SIZE
        );

        verifyNoMoreInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnSuccessWhenDealershipHasNoServiceTypes() {

        // Arrange
        mockActiveDealership();

        PagedList<ServiceTypeData> emptyPage =
                PagedList.create(
                        List.of(),
                        0,
                        PAGE_NUMBER,
                        PAGE_SIZE
                );

        when(dealershipRepository.findActiveByDealershipId(
                dealershipId,
                PAGE_NUMBER,
                PAGE_SIZE
        )).thenReturn(emptyPage);

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        PagedList<Response> response = getSuccessValue(result);

        assertNotNull(response.items());
        assertTrue(response.items().isEmpty());
        assertEquals(0, response.totalCount());
        assertEquals(PAGE_NUMBER, response.pageNumber());
        assertEquals(PAGE_SIZE, response.pageSize());

        verify(dealershipRepository).findById(dealershipId);

        verify(dealershipRepository).findActiveByDealershipId(
                dealershipId,
                PAGE_NUMBER,
                PAGE_SIZE
        );

        verifyNoMoreInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnSuccessWhenRequestingSecondPage() {

        // Arrange
        mockActiveDealership();

        int pageNumber = 2;
        int pageSize = 2;

        Query secondPageQuery = new Query(
                dealershipId.toString(),
                pageNumber,
                pageSize
        );

        UUID serviceTypeId = UUID.randomUUID();

        List<ServiceTypeData> serviceTypes = List.of(
                new ServiceTypeData(
                        serviceTypeId,
                        "Tire Replacement",
                        "Replace vehicle tires",
                        45
                )
        );

        PagedList<ServiceTypeData> pagedServiceTypes =
                PagedList.create(
                        serviceTypes,
                        3,
                        pageNumber,
                        pageSize
                );

        when(dealershipRepository.findActiveByDealershipId(
                dealershipId,
                pageNumber,
                pageSize
        )).thenReturn(pagedServiceTypes);

        // Act
        Result<PagedList<Response>> result =
                handler.handle(secondPageQuery).join();

        // Assert
        assertTrue(result.isSuccess());

        PagedList<Response> response = getSuccessValue(result);

        assertEquals(1, response.items().size());
        assertEquals(3, response.totalCount());
        assertEquals(pageNumber, response.pageNumber());
        assertEquals(pageSize, response.pageSize());

        assertEquals(
                serviceTypeId,
                response.items().get(0).id()
        );

        verify(dealershipRepository).findById(dealershipId);

        verify(dealershipRepository).findActiveByDealershipId(
                dealershipId,
                pageNumber,
                pageSize
        );

        verifyNoMoreInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnEmptyItemsWhenPageExceedsAvailablePages() {

        // Arrange
        mockActiveDealership();

        int pageNumber = 10;
        int pageSize = 10;

        Query outOfRangeQuery = new Query(
                dealershipId.toString(),
                pageNumber,
                pageSize
        );

        PagedList<ServiceTypeData> emptyPage =
                PagedList.create(
                        List.of(),
                        3,
                        pageNumber,
                        pageSize
                );

        when(dealershipRepository.findActiveByDealershipId(
                dealershipId,
                pageNumber,
                pageSize
        )).thenReturn(emptyPage);

        // Act
        Result<PagedList<Response>> result =
                handler.handle(outOfRangeQuery).join();

        // Assert
        assertTrue(result.isSuccess());

        PagedList<Response> response = getSuccessValue(result);

        assertTrue(response.items().isEmpty());
        assertEquals(3, response.totalCount());
        assertEquals(pageNumber, response.pageNumber());
        assertEquals(pageSize, response.pageSize());

        verify(dealershipRepository).findById(dealershipId);

        verify(dealershipRepository).findActiveByDealershipId(
                dealershipId,
                pageNumber,
                pageSize
        );

        verifyNoMoreInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenDealershipDoesNotExist() {

        // Arrange
        when(dealershipRepository.findById(dealershipId))
                .thenReturn(Optional.empty());

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertError(result, Errors.DEALERSHIP_NOT_FOUND);

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
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertError(result, Errors.DEALERSHIP_NOT_FOUND);

        verify(dealershipRepository).findById(dealershipId);
        verifyNoMoreInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenQueryIsNull() {

        // Act
        Result<PagedList<Response>> result =
                handler.handle(null).join();

        // Assert
        assertError(result, Errors.INVALID_QUERY);

        verifyNoInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenDealershipIdIsInvalid() {

        // Arrange
        Query invalidQuery = new Query(
                "invalid-uuid",
                PAGE_NUMBER,
                PAGE_SIZE
        );

        // Act
        Result<PagedList<Response>> result =
                handler.handle(invalidQuery).join();

        // Assert
        assertError(result, Errors.INVALID_DEALERSHIP_ID);

        verifyNoInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenDealershipIdIsNull() {

        // Arrange
        Query invalidQuery = new Query(
                null,
                PAGE_NUMBER,
                PAGE_SIZE
        );

        // Act
        Result<PagedList<Response>> result =
                handler.handle(invalidQuery).join();

        // Assert
        assertError(result, Errors.INVALID_DEALERSHIP_ID);

        verifyNoInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenDealershipIdIsBlank() {

        // Arrange
        Query invalidQuery = new Query(
                " ",
                PAGE_NUMBER,
                PAGE_SIZE
        );

        // Act
        Result<PagedList<Response>> result =
                handler.handle(invalidQuery).join();

        // Assert
        assertError(result, Errors.INVALID_DEALERSHIP_ID);

        verifyNoInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenPageNumberIsInvalid() {

        // Arrange
        Query invalidQuery = new Query(
                dealershipId.toString(),
                0,
                PAGE_SIZE
        );

        // Act
        Result<PagedList<Response>> result =
                handler.handle(invalidQuery).join();

        // Assert
        assertError(result, Errors.INVALID_PAGE_NUMBER);

        verifyNoInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenPageSizeIsInvalid() {

        // Arrange
        Query invalidQuery = new Query(
                dealershipId.toString(),
                PAGE_NUMBER,
                101
        );

        // Act
        Result<PagedList<Response>> result =
                handler.handle(invalidQuery).join();

        // Assert
        assertError(result, Errors.INVALID_PAGE_SIZE);

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
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertError(result, Errors.SERVICE_TYPES_RETRIEVAL_FAILED);

        verify(dealershipRepository).findById(dealershipId);
        verifyNoMoreInteractions(dealershipRepository);
    }

    @Test
    void shouldReturnFailureWhenDatabaseThrowsExceptionOnServiceTypeLookup() {

        // Arrange
        mockActiveDealership();

        when(dealershipRepository.findActiveByDealershipId(
                dealershipId,
                PAGE_NUMBER,
                PAGE_SIZE
        )).thenThrow(
                new DataAccessResourceFailureException(
                        "Failed to retrieve service types"
                )
        );

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertError(result, Errors.SERVICE_TYPES_RETRIEVAL_FAILED);

        verify(dealershipRepository).findById(dealershipId);

        verify(dealershipRepository).findActiveByDealershipId(
                dealershipId,
                PAGE_NUMBER,
                PAGE_SIZE
        );

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

    @SuppressWarnings("unchecked")
    private PagedList<Response> getSuccessValue(
            Result<PagedList<Response>> result
    ) {

        assertTrue(result.isSuccess());

        Result.Success<?> success =
                (Result.Success<?>) result;

        return (PagedList<Response>) success.value();
    }

    private Error getFirstError(
            Result<PagedList<Response>> result
    ) {

        assertTrue(result.isFailure());

        Result.Failure<?> failure =
                (Result.Failure<?>) result;

        assertFalse(failure.errorDetails().errors().isEmpty());

        return failure.errorDetails().errors().get(0);
    }

    private void assertError(
            Result<PagedList<Response>> result,
            Error expected
    ) {

        Error actual = getFirstError(result);

        assertEquals(expected.code(), actual.code());
        assertEquals(expected.type(), actual.type());
        assertEquals(expected.property(), actual.property());
    }
}
