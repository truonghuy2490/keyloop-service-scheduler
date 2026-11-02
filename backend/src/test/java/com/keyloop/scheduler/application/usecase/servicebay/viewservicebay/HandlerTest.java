package com.keyloop.scheduler.application.usecase.servicebay.viewservicebay;

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

import com.keyloop.scheduler.application.abstraction.dataaccess.ServiceBayRepository;
import com.keyloop.scheduler.application.common.pagination.PagedList;
import com.keyloop.scheduler.application.common.result.Error;
import com.keyloop.scheduler.application.common.result.Result;
import com.keyloop.scheduler.domain.entity.ServiceBay;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private ServiceBayRepository serviceBayRepository;

    private Validators validators;

    private Handler handler;

    private static final String DEALERSHIP_ID =
            "10000000-0000-0000-0000-000000000001";

    private static final UUID DEALERSHIP_UUID =
            UUID.fromString(DEALERSHIP_ID);

    @BeforeEach
    void setUp() {
        validators = new Validators();
        handler = new Handler(serviceBayRepository, validators);
    }

    @Test
    void shouldReturnSuccessWhenServiceBaysExist() {
        // Arrange
        int pageNumber = 1;
        int pageSize = 10;

        Query query = new Query(
                DEALERSHIP_ID,
                pageNumber,
                pageSize
        );

        ServiceBay serviceBay1 = createServiceBay();
        ServiceBay serviceBay2 = createServiceBay();

        List<ServiceBay> serviceBays = List.of(
                serviceBay1,
                serviceBay2
        );

        PagedList<ServiceBay> pagedServiceBays =
                PagedList.create(
                        serviceBays,
                        2,
                        pageNumber,
                        pageSize
                );

        when(serviceBayRepository.findAllByDealershipId(
                DEALERSHIP_UUID,
                pageNumber,
                pageSize
        )).thenReturn(pagedServiceBays);

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        Result.Success<?> success =
                (Result.Success<?>) result;

        @SuppressWarnings("unchecked")
        PagedList<Response> response =
                (PagedList<Response>) success.value();

        assertEquals(2, response.items().size());
        assertEquals(2, response.totalCount());
        assertEquals(pageNumber, response.pageNumber());
        assertEquals(pageSize, response.pageSize());

        // Verify first service bay mapping
        Response firstServiceBay = response.items().get(0);

        assertEquals(serviceBay1.getId(), firstServiceBay.id());
        assertEquals(
                serviceBay1.getDealershipId(),
                firstServiceBay.dealershipId()
        );
        assertEquals(
                serviceBay1.getBayCode(),
                firstServiceBay.bayCode()
        );
        assertEquals(
                serviceBay1.getActive(),
                firstServiceBay.active()
        );

        // Verify second service bay mapping
        Response secondServiceBay = response.items().get(1);

        assertEquals(serviceBay2.getId(), secondServiceBay.id());
        assertEquals(
                serviceBay2.getDealershipId(),
                secondServiceBay.dealershipId()
        );
        assertEquals(
                serviceBay2.getBayCode(),
                secondServiceBay.bayCode()
        );
        assertEquals(
                serviceBay2.getActive(),
                secondServiceBay.active()
        );

        verify(serviceBayRepository).findAllByDealershipId(
                DEALERSHIP_UUID,
                pageNumber,
                pageSize
        );
        verifyNoMoreInteractions(serviceBayRepository);
    }

    @Test
    void shouldReturnSuccessWhenNoServiceBaysExist() {
        // Arrange
        int pageNumber = 1;
        int pageSize = 10;

        Query query = new Query(
                DEALERSHIP_ID,
                pageNumber,
                pageSize
        );

        PagedList<ServiceBay> emptyServiceBays =
                PagedList.create(
                        List.of(),
                        0,
                        pageNumber,
                        pageSize
                );

        when(serviceBayRepository.findAllByDealershipId(
                DEALERSHIP_UUID,
                pageNumber,
                pageSize
        )).thenReturn(emptyServiceBays);

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        Result.Success<?> success =
                (Result.Success<?>) result;

        @SuppressWarnings("unchecked")
        PagedList<Response> response =
                (PagedList<Response>) success.value();

        assertTrue(response.items().isEmpty());
        assertEquals(0, response.totalCount());
        assertEquals(pageNumber, response.pageNumber());
        assertEquals(pageSize, response.pageSize());

        verify(serviceBayRepository).findAllByDealershipId(
                DEALERSHIP_UUID,
                pageNumber,
                pageSize
        );
        verifyNoMoreInteractions(serviceBayRepository);
    }

    @Test
    void shouldPreserveTotalCountWhenRequestedPageHasNoItems() {
        // Arrange
        int pageNumber = 5;
        int pageSize = 10;
        long totalCount = 42;

        Query query = new Query(
                DEALERSHIP_ID,
                pageNumber,
                pageSize
        );

        PagedList<ServiceBay> emptyPage =
                PagedList.create(
                        List.of(),
                        totalCount,
                        pageNumber,
                        pageSize
                );

        when(serviceBayRepository.findAllByDealershipId(
                DEALERSHIP_UUID,
                pageNumber,
                pageSize
        )).thenReturn(emptyPage);

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        Result.Success<?> success =
                (Result.Success<?>) result;

        @SuppressWarnings("unchecked")
        PagedList<Response> response =
                (PagedList<Response>) success.value();

        assertTrue(response.items().isEmpty());
        assertEquals(totalCount, response.totalCount());
        assertEquals(pageNumber, response.pageNumber());
        assertEquals(pageSize, response.pageSize());

        verify(serviceBayRepository).findAllByDealershipId(
                DEALERSHIP_UUID,
                pageNumber,
                pageSize
        );
        verifyNoMoreInteractions(serviceBayRepository);
    }

    @Test
    void shouldReturnFailureWhenDealershipIdIsInvalid() {
        // Arrange
        Query query = new Query(
                "invalid-uuid",
                1,
                10
        );

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

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
        verifyNoInteractions(serviceBayRepository);
    }

    @Test
    void shouldReturnFailureWhenQueryIsNull() {
        // Arrange
        Query query = null;

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

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

        verifyNoInteractions(serviceBayRepository);
    }

    @Test
    void shouldReturnFailureWhenDatabaseThrowsException() {
        // Arrange
        int pageNumber = 1;
        int pageSize = 10;

        Query query = new Query(
                DEALERSHIP_ID,
                pageNumber,
                pageSize
        );

        when(serviceBayRepository.findAllByDealershipId(
                DEALERSHIP_UUID,
                pageNumber,
                pageSize
        )).thenThrow(
                new DataAccessResourceFailureException(
                        "Database unavailable"
                )
        );

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertTrue(result.isFailure());

        Result.Failure<?> failure =
                (Result.Failure<?>) result;

        assertFalse(failure.errorDetails().errors().isEmpty());

        Error error = failure.errorDetails().errors().get(0);

        assertEquals(
                Errors.SERVICE_BAY_RETRIEVAL_FAILED.code(),
                error.code()
        );

        assertEquals(
                Errors.SERVICE_BAY_RETRIEVAL_FAILED.type(),
                error.type()
        );

        verify(serviceBayRepository).findAllByDealershipId(
                DEALERSHIP_UUID,
                pageNumber,
                pageSize
        );
        verifyNoMoreInteractions(serviceBayRepository);
    }

    private ServiceBay createServiceBay() {
        ServiceBay serviceBay = mock(ServiceBay.class);

        when(serviceBay.getId()).thenReturn(UUID.randomUUID());
        when(serviceBay.getDealershipId()).thenReturn(DEALERSHIP_UUID);
        when(serviceBay.getBayCode()).thenReturn("BAY-001");
        when(serviceBay.getActive()).thenReturn(true);

        return serviceBay;
    }
}
