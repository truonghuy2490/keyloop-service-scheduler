package com.keyloop.scheduler.application.usecase.appointment.viewcustomerappointment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import com.keyloop.scheduler.application.abstraction.dataaccess.AppointmentRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.VehicleRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.BaseRepository;
import com.keyloop.scheduler.application.common.pagination.PagedList;
import com.keyloop.scheduler.application.common.result.Result;

import com.keyloop.scheduler.domain.entity.Appointment;
import com.keyloop.scheduler.domain.entity.Vehicle;
import com.keyloop.scheduler.domain.entity.Dealership;
import com.keyloop.scheduler.domain.entity.ServiceType;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private BaseRepository<Dealership, UUID> dealershipRepository;

    @Mock
    private BaseRepository<ServiceType, UUID> serviceTypeRepository;

    private Validators validators;
    private Handler handler;

    private static final String CUSTOMER_ID =
            "550e8400-e29b-41d4-a716-446655440000";

    private static final UUID CUSTOMER_UUID =
            UUID.fromString(CUSTOMER_ID);

    private static final UUID VEHICLE_ID = UUID.randomUUID();
    private static final UUID DEALERSHIP_ID = UUID.randomUUID();
    private static final UUID SERVICE_TYPE_ID = UUID.randomUUID();

    private static final Instant START_TIME =
            Instant.parse("2026-11-26T02:00:00Z");

    private static final Instant END_TIME =
            Instant.parse("2026-11-26T03:00:00Z");

    @BeforeEach
    void setUp() {
        validators = new Validators();

        handler = new Handler(
                appointmentRepository,
                vehicleRepository,
                dealershipRepository,
                serviceTypeRepository,
                validators
        );
    }

    @Test
    void shouldReturnSuccessWhenAppointmentsExist() {
        // Arrange
        int pageNumber = 1;
        int pageSize = 10;

        Query query = new Query(CUSTOMER_ID, pageNumber, pageSize);

        Appointment appointment = createAppointment();
        mockRelatedEntities();

        PagedList<Appointment> pagedAppointments =
                PagedList.create(
                        List.of(appointment),
                        1,
                        pageNumber,
                        pageSize
                );

        when(appointmentRepository.findAllByCustomerId(
                CUSTOMER_UUID, pageNumber, pageSize
        )).thenReturn(pagedAppointments);

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        PagedList<Response> response = getSuccessValue(result);

        assertEquals(1, response.items().size());
        assertEquals(1, response.totalCount());
        assertEquals(pageNumber, response.pageNumber());
        assertEquals(pageSize, response.pageSize());

        Response item = response.items().get(0);

        assertEquals(appointment.getId(), item.id());
        assertEquals("CONFIRMED", item.status());

        // Vehicle mapping
        assertEquals(
                "59A-12345",
                item.vehicle().registrationNumber()
        );
        assertEquals("Toyota", item.vehicle().make());
        assertEquals("Camry", item.vehicle().model());

        // Dealership mapping
        assertEquals(
                "Keyloop Service Center",
                item.dealership().name()
        );

        // Service mapping
        assertEquals("Wheel Alignment", item.service().name());
        assertEquals(60, item.service().durationMinutes());

        // Timezone mapping: UTC -> UTC+7
        assertEquals(
                OffsetDateTime.parse("2026-11-26T09:00:00+07:00"),
                item.schedule().startTime()
        );

        assertEquals(
                OffsetDateTime.parse("2026-11-26T10:00:00+07:00"),
                item.schedule().endTime()
        );

        verify(appointmentRepository).findAllByCustomerId(
                CUSTOMER_UUID, pageNumber, pageSize
        );

        verify(vehicleRepository).findById(VEHICLE_ID);
        verify(dealershipRepository).findById(DEALERSHIP_ID);
        verify(serviceTypeRepository).findById(SERVICE_TYPE_ID);

        verifyNoMoreInteractions(
                appointmentRepository,
                vehicleRepository,
                dealershipRepository,
                serviceTypeRepository
        );
    }

    @Test
    void shouldReturnSuccessWhenNoAppointmentsExist() {
        // Arrange
        Query query = new Query(CUSTOMER_ID, 1, 10);

        PagedList<Appointment> emptyAppointments =
                PagedList.create(List.of(), 0, 1, 10);

        when(appointmentRepository.findAllByCustomerId(
                CUSTOMER_UUID, 1, 10
        )).thenReturn(emptyAppointments);

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        PagedList<Response> response = getSuccessValue(result);

        assertTrue(response.items().isEmpty());
        assertEquals(0, response.totalCount());
        assertEquals(1, response.pageNumber());
        assertEquals(10, response.pageSize());

        verify(appointmentRepository).findAllByCustomerId(
                CUSTOMER_UUID, 1, 10
        );

        verifyNoInteractions(
                vehicleRepository,
                dealershipRepository,
                serviceTypeRepository
        );
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

        PagedList<Appointment> emptyPage =
                PagedList.create(
                        List.of(),
                        totalCount,
                        pageNumber,
                        pageSize
                );

        when(appointmentRepository.findAllByCustomerId(
                CUSTOMER_UUID, pageNumber, pageSize
        )).thenReturn(emptyPage);

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        PagedList<Response> response = getSuccessValue(result);

        assertTrue(response.items().isEmpty());
        assertEquals(totalCount, response.totalCount());
        assertEquals(pageNumber, response.pageNumber());
        assertEquals(pageSize, response.pageSize());

        verifyNoInteractions(
                vehicleRepository,
                dealershipRepository,
                serviceTypeRepository
        );
    }

    @Test
    void shouldReturnFailureWhenCustomerIdIsInvalid() {
        // Arrange
        Query query = new Query("invalid-uuid", 1, 10);

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertFailureCode(
                result,
                Errors.INVALID_CUSTOMER_ID.code()
        );

        verifyNoInteractions(
                appointmentRepository,
                vehicleRepository,
                dealershipRepository,
                serviceTypeRepository
        );
    }

    @Test
    void shouldReturnFailureWhenQueryIsNull() {
        // Arrange
        Query query = null;

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertFailureCode(
                result,
                Errors.INVALID_CUSTOMER_ID.code()
        );

        verifyNoInteractions(
                appointmentRepository,
                vehicleRepository,
                dealershipRepository,
                serviceTypeRepository
        );
    }

    @Test
    void shouldReturnFailureWhenDatabaseThrowsException() {
        // Arrange
        Query query = new Query(CUSTOMER_ID, 1, 10);

        when(appointmentRepository.findAllByCustomerId(
                CUSTOMER_UUID, 1, 10
        )).thenThrow(
                new DataAccessResourceFailureException(
                        "Database unavailable"
                )
        );

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertFailureCode(
                result,
                Errors.APPOINTMENT_RETRIEVAL_FAILED.code()
        );

        verify(appointmentRepository).findAllByCustomerId(
                CUSTOMER_UUID, 1, 10
        );

        verifyNoInteractions(
                vehicleRepository,
                dealershipRepository,
                serviceTypeRepository
        );
    }

    @Test
    void shouldReturnFailureWhenVehicleRepositoryThrowsException() {
        // Arrange
        Query query = new Query(CUSTOMER_ID, 1, 10);

        Appointment appointment = createAppointment();

        when(appointmentRepository.findAllByCustomerId(
                CUSTOMER_UUID, 1, 10
        )).thenReturn(
                PagedList.create(List.of(appointment), 1, 1, 10)
        );

        when(vehicleRepository.findById(VEHICLE_ID))
                .thenThrow(
                        new DataAccessResourceFailureException(
                                "Vehicle database unavailable"
                        )
                );

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertFailureCode(
                result,
                Errors.APPOINTMENT_RETRIEVAL_FAILED.code()
        );

        verify(vehicleRepository).findById(VEHICLE_ID);

        verifyNoInteractions(
                dealershipRepository,
                serviceTypeRepository
        );
    }

    // -------------------------
    // Test helpers
    // -------------------------

    private Appointment createAppointment() {
        return Appointment.builder()
                .id(UUID.randomUUID())
                .customerId(CUSTOMER_UUID)
                .vehicleId(VEHICLE_ID)
                .dealershipId(DEALERSHIP_ID)
                .serviceTypeId(SERVICE_TYPE_ID)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .status("CONFIRMED")
                .build();
    }

    private void mockRelatedEntities() {
        Vehicle vehicle = Vehicle.builder()
                .id(VEHICLE_ID)
                .customerId(CUSTOMER_UUID)
                .registrationNumber("59A-12345")
                .make("Toyota")
                .model("Camry")
                .build();

        Dealership dealership = Dealership.builder()
                .id(DEALERSHIP_ID)
                .name("Keyloop Service Center")
                .timeZone("Asia/Ho_Chi_Minh")
                .build();

        ServiceType serviceType = ServiceType.builder()
                .id(SERVICE_TYPE_ID)
                .name("Wheel Alignment")
                .durationMinutes(60)
                .build();

        when(vehicleRepository.findById(VEHICLE_ID))
                .thenReturn(java.util.Optional.of(vehicle));

        when(dealershipRepository.findById(DEALERSHIP_ID))
                .thenReturn(java.util.Optional.of(dealership));

        when(serviceTypeRepository.findById(SERVICE_TYPE_ID))
                .thenReturn(java.util.Optional.of(serviceType));
    }

    @SuppressWarnings("unchecked")
    private PagedList<Response> getSuccessValue(
            Result<PagedList<Response>> result
    ) {
        assertTrue(result.isSuccess());

        return (PagedList<Response>)
                ((Result.Success<?>) result).value();
    }

    private void assertFailureCode(
            Result<PagedList<Response>> result,
            String expectedCode
    ) {
        assertTrue(result.isFailure());

        Result.Failure<?> failure =
                (Result.Failure<?>) result;

        assertEquals(
                1,
                failure.errorDetails().errors().size()
        );

        assertEquals(
                expectedCode,
                failure.errorDetails().errors().get(0).code()
        );
    }
}
