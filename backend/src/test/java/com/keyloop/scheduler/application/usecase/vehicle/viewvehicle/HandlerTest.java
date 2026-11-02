
package com.keyloop.scheduler.application.usecase.vehicle.viewvehicle;

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

import com.keyloop.scheduler.application.abstraction.dataaccess.VehicleRepository;
import com.keyloop.scheduler.application.common.pagination.PagedList;
import com.keyloop.scheduler.application.common.result.Error;
import com.keyloop.scheduler.application.common.result.Result;
import com.keyloop.scheduler.domain.entity.Vehicle;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private VehicleRepository vehicleRepository;

    private Validators validators;

    private Handler handler;

    private static final String CUSTOMER_ID =
            "550e8400-e29b-41d4-a716-446655440000";

    private static final UUID CUSTOMER_UUID =
            UUID.fromString(CUSTOMER_ID);

    @BeforeEach
    void setUp() {
        validators = new Validators();
        handler = new Handler(vehicleRepository, validators);
    }

    @Test
    void shouldReturnSuccessWhenVehiclesExist() {
        // Arrange
        int pageNumber = 1;
        int pageSize = 10;

        Query query = new Query(
                CUSTOMER_ID,
                pageNumber,
                pageSize
        );

        Vehicle vehicle1 = createVehicle();
        Vehicle vehicle2 = createVehicle();

        List<Vehicle> vehicles = List.of(
                vehicle1,
                vehicle2
        );

        PagedList<Vehicle> pagedVehicles =
                PagedList.create(
                        vehicles,
                        2,
                        pageNumber,
                        pageSize
                );

        when(vehicleRepository.findAllByCustomerId(
                CUSTOMER_UUID,
                pageNumber,
                pageSize
        )).thenReturn(pagedVehicles);

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

        // Verify first vehicle mapping
        Response firstVehicle = response.items().get(0);

        assertEquals(vehicle1.getId(), firstVehicle.id());
        assertEquals(vehicle1.getCustomerId(), firstVehicle.customerId());
        assertEquals(
                vehicle1.getRegistrationNumber(),
                firstVehicle.registrationNumber()
        );
        assertEquals(vehicle1.getMake(), firstVehicle.make());
        assertEquals(vehicle1.getModel(), firstVehicle.model());
        assertEquals(vehicle1.getModelYear(), firstVehicle.modelYear());

        // Verify second vehicle mapping
        Response secondVehicle = response.items().get(1);

        assertEquals(vehicle2.getId(), secondVehicle.id());
        assertEquals(vehicle2.getCustomerId(), secondVehicle.customerId());
        assertEquals(
                vehicle2.getRegistrationNumber(),
                secondVehicle.registrationNumber()
        );
        assertEquals(vehicle2.getMake(), secondVehicle.make());
        assertEquals(vehicle2.getModel(), secondVehicle.model());
        assertEquals(vehicle2.getModelYear(), secondVehicle.modelYear());

        verify(vehicleRepository).findAllByCustomerId(
                CUSTOMER_UUID,
                pageNumber,
                pageSize
        );
        verifyNoMoreInteractions(vehicleRepository);
    }

    @Test
    void shouldReturnSuccessWhenNoVehiclesExist() {
        // Arrange
        int pageNumber = 1;
        int pageSize = 10;

        Query query = new Query(
                CUSTOMER_ID,
                pageNumber,
                pageSize
        );

        PagedList<Vehicle> emptyVehicles =
                PagedList.create(
                        List.of(),
                        0,
                        pageNumber,
                        pageSize
                );

        when(vehicleRepository.findAllByCustomerId(
                CUSTOMER_UUID,
                pageNumber,
                pageSize
        )).thenReturn(emptyVehicles);

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

        verify(vehicleRepository).findAllByCustomerId(
                CUSTOMER_UUID,
                pageNumber,
                pageSize
        );
        verifyNoMoreInteractions(vehicleRepository);
    }

    @Test
    void shouldPreserveTotalCountWhenRequestedPageHasNoItems() {
        // Arrange
        int pageNumber = 5;
        int pageSize = 10;
        long totalCount = 42;

        Query query = new Query(
                CUSTOMER_ID,
                pageNumber,
                pageSize
        );

        PagedList<Vehicle> emptyPage =
                PagedList.create(
                        List.of(),
                        totalCount,
                        pageNumber,
                        pageSize
                );

        when(vehicleRepository.findAllByCustomerId(
                CUSTOMER_UUID,
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

        verify(vehicleRepository).findAllByCustomerId(
                CUSTOMER_UUID,
                pageNumber,
                pageSize
        );
        verifyNoMoreInteractions(vehicleRepository);
    }

    @Test
    void shouldReturnFailureWhenCustomerIdIsInvalid() {
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
                Errors.INVALID_CUSTOMER_ID.code(),
                error.code()
        );

        // Repository must not be called
        verifyNoInteractions(vehicleRepository);
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
                Errors.INVALID_CUSTOMER_ID.code(),
                error.code()
        );

        verifyNoInteractions(vehicleRepository);
    }

    @Test
    void shouldReturnFailureWhenDatabaseThrowsException() {
        // Arrange
        int pageNumber = 1;
        int pageSize = 10;

        Query query = new Query(
                CUSTOMER_ID,
                pageNumber,
                pageSize
        );

        when(vehicleRepository.findAllByCustomerId(
                CUSTOMER_UUID,
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
                Errors.VEHICLE_RETRIEVAL_FAILED.code(),
                error.code()
        );

        assertEquals(
                Errors.VEHICLE_RETRIEVAL_FAILED.type(),
                error.type()
        );

        verify(vehicleRepository).findAllByCustomerId(
                CUSTOMER_UUID,
                pageNumber,
                pageSize
        );
        verifyNoMoreInteractions(vehicleRepository);
    }

    private Vehicle createVehicle() {
        Vehicle vehicle = mock(Vehicle.class);

        when(vehicle.getId()).thenReturn(UUID.randomUUID());
        when(vehicle.getCustomerId()).thenReturn(CUSTOMER_UUID);
        when(vehicle.getRegistrationNumber())
                .thenReturn("51A-12345");
        when(vehicle.getMake()).thenReturn("Toyota");
        when(vehicle.getModel()).thenReturn("Camry");
        when(vehicle.getModelYear()).thenReturn((short) 2024);

        return vehicle;
    }
}
