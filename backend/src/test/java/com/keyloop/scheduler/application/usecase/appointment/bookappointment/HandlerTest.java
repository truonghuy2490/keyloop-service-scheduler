
package com.keyloop.scheduler.application.usecase.appointment.bookappointment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import com.keyloop.scheduler.application.abstraction.dataaccess.AppointmentRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.BaseRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.DealershipRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository;
import com.keyloop.scheduler.application.common.result.Error;
import com.keyloop.scheduler.application.common.result.Result;
import com.keyloop.scheduler.domain.entity.Customer;
import com.keyloop.scheduler.domain.entity.Dealership;
import com.keyloop.scheduler.domain.entity.ServiceBay;
import com.keyloop.scheduler.domain.entity.ServiceType;
import com.keyloop.scheduler.domain.entity.Technician;
import com.keyloop.scheduler.domain.entity.Vehicle;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private BaseRepository<Customer, UUID> customerRepository;

    @Mock
    private BaseRepository<Vehicle, UUID> vehicleRepository;

    @Mock
    private BaseRepository<ServiceType, UUID> serviceTypeRepository;

    @Mock
    private BaseRepository<ServiceBay, UUID> serviceBayRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @Mock
    private DealershipRepository dealershipRepository;

    @Mock
    private AppointmentScheduleValidator scheduleValidator;

    private Handler handler;

    private UUID customerId;
    private UUID vehicleId;
    private UUID dealershipId;
    private UUID serviceTypeId;
    private UUID technicianId;
    private UUID serviceBayId;

    private Request request;

    @BeforeEach
    void setUp() {

        customerId = UUID.randomUUID();
        vehicleId = UUID.randomUUID();
        dealershipId = UUID.randomUUID();
        serviceTypeId = UUID.randomUUID();
        technicianId = UUID.randomUUID();
        serviceBayId = UUID.randomUUID();

        request = new Request(
                customerId.toString(),
                vehicleId.toString(),
                dealershipId.toString(),
                serviceTypeId.toString(),
                technicianId.toString(),
                serviceBayId.toString(),
                OffsetDateTime.parse("2026-11-05T09:00:00+07:00")
        );

        handler = new Handler(
                new Validators(),
                customerRepository,
                vehicleRepository,
                serviceTypeRepository,
                serviceBayRepository,
                appointmentRepository,
                technicianRepository,
                dealershipRepository,
                scheduleValidator
        );
    }

    @Test
    void shouldReturnFailureWhenRequestIsNull() {

        // Act
        Result<Response> result = handler.handle(null).join();

        // Assert
        assertError(result, Errors.INVALID_REQUEST);

        verifyNoInteractions(
                customerRepository,
                vehicleRepository,
                dealershipRepository,
                serviceTypeRepository,
                technicianRepository,
                serviceBayRepository,
                appointmentRepository
        );
    }

    @Test
    void shouldReturnFailureWhenCustomerIdIsInvalid() {

        // Arrange
        Request invalidRequest = new Request(
                "invalid-uuid",
                vehicleId.toString(),
                dealershipId.toString(),
                serviceTypeId.toString(),
                technicianId.toString(),
                serviceBayId.toString(),
                request.startTime()
        );

        // Act
        Result<Response> result =
                handler.handle(invalidRequest).join();

        // Assert
        assertError(result, Errors.INVALID_CUSTOMER_ID);

        verifyNoInteractions(customerRepository);
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenCustomerDoesNotExist() {

        // Arrange
        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(result, Errors.CUSTOMER_NOT_FOUND);

        verify(customerRepository).findById(customerId);
        verifyNoInteractions(vehicleRepository);
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenCustomerIsDeleted() {

        // Arrange
        Customer customer = mock(Customer.class);

        when(customer.getIsDeleted()).thenReturn(true);

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(result, Errors.CUSTOMER_NOT_FOUND);

        verify(customerRepository).findById(customerId);
        verifyNoInteractions(vehicleRepository);
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenVehicleDoesNotExist() {

        // Arrange
        mockCustomer();

        when(vehicleRepository.findById(vehicleId))
                .thenReturn(Optional.empty());

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(result, Errors.VEHICLE_NOT_FOUND);

        verify(vehicleRepository).findById(vehicleId);
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenVehicleIsDeleted() {

        // Arrange
        mockCustomer();

        Vehicle vehicle = mock(Vehicle.class);

        when(vehicle.getIsDeleted()).thenReturn(true);

        when(vehicleRepository.findById(vehicleId))
                .thenReturn(Optional.of(vehicle));

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(result, Errors.VEHICLE_NOT_FOUND);

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenVehicleDoesNotBelongToCustomer() {

        // Arrange
        mockCustomer();

        Vehicle vehicle = mock(Vehicle.class);

        when(vehicle.getCustomerId())
                .thenReturn(UUID.randomUUID());

        when(vehicleRepository.findById(vehicleId))
                .thenReturn(Optional.of(vehicle));

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(
                result,
                Errors.VEHICLE_NOT_OWNED_BY_CUSTOMER
        );

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenDealershipDoesNotExist() {

        // Arrange
        mockCustomer();
        mockVehicle();

        when(dealershipRepository.findById(dealershipId))
                .thenReturn(Optional.empty());

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(result, Errors.DEALERSHIP_NOT_FOUND);

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenDealershipIsDeleted() {

        // Arrange
        mockCustomer();
        mockVehicle();

        Dealership dealership = mock(Dealership.class);

        when(dealership.getIsDeleted()).thenReturn(true);

        when(dealershipRepository.findById(dealershipId))
                .thenReturn(Optional.of(dealership));

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(result, Errors.DEALERSHIP_NOT_FOUND);

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenServiceTypeDoesNotExist() {

        // Arrange
        mockCustomer();
        mockVehicle();
        mockDealership();

        when(serviceTypeRepository.findById(serviceTypeId))
                .thenReturn(Optional.empty());

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(result, Errors.SERVICE_TYPE_NOT_FOUND);

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenServiceTypeIsDeleted() {

        // Arrange
        mockCustomer();
        mockVehicle();
        mockDealership();

        ServiceType serviceType = mock(ServiceType.class);

        when(serviceType.getIsDeleted()).thenReturn(true);

        when(serviceTypeRepository.findById(serviceTypeId))
                .thenReturn(Optional.of(serviceType));

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(result, Errors.SERVICE_TYPE_NOT_FOUND);

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenServiceDurationIsZero() {

        // Arrange
        mockCustomer();
        mockVehicle();
        mockDealership();

        mockServiceType(0);

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(result, Errors.INVALID_SERVICE_DURATION);

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenServiceDurationIsNegative() {

        // Arrange
        mockCustomer();
        mockVehicle();
        mockDealership();

        mockServiceType(-30);

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(result, Errors.INVALID_SERVICE_DURATION);

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenTechnicianDoesNotExist() {

        // Arrange
        mockValidEntitiesBeforeTechnician();

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.empty());

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(result, Errors.TECHNICIAN_NOT_FOUND);

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenTechnicianIsDeleted() {

        // Arrange
        mockValidEntitiesBeforeTechnician();

        Technician technician = mock(Technician.class);

        when(technician.getIsDeleted()).thenReturn(true);

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(result, Errors.TECHNICIAN_NOT_FOUND);

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenTechnicianBelongsToAnotherDealership() {

        // Arrange
        mockValidEntitiesBeforeTechnician();

        Technician technician = mock(Technician.class);

        when(technician.getDealershipId())
                .thenReturn(UUID.randomUUID());

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(
                result,
                Errors.TECHNICIAN_NOT_IN_DEALERSHIP
        );

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenServiceBayDoesNotExist() {

        // Arrange
        mockValidEntitiesBeforeServiceBay();

        when(serviceBayRepository.findById(serviceBayId))
                .thenReturn(Optional.empty());

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(result, Errors.SERVICE_BAY_NOT_FOUND);

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenServiceBayIsDeleted() {

        // Arrange
        mockValidEntitiesBeforeServiceBay();

        ServiceBay serviceBay = mock(ServiceBay.class);

        when(serviceBay.getIsDeleted()).thenReturn(true);

        when(serviceBayRepository.findById(serviceBayId))
                .thenReturn(Optional.of(serviceBay));

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(result, Errors.SERVICE_BAY_NOT_FOUND);

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenServiceBayBelongsToAnotherDealership() {

        // Arrange
        mockValidEntitiesBeforeServiceBay();

        ServiceBay serviceBay = mock(ServiceBay.class);

        when(serviceBay.getDealershipId())
                .thenReturn(UUID.randomUUID());

        when(serviceBayRepository.findById(serviceBayId))
                .thenReturn(Optional.of(serviceBay));

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(
                result,
                Errors.SERVICE_BAY_NOT_IN_DEALERSHIP
        );

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenTechnicianIsNotQualified() {

        // Arrange
        mockValidEntitiesBeforeServiceBay();
        mockServiceBay();

        when(technicianRepository.isQualifiedForServiceType(
                technicianId, serviceTypeId
        )).thenReturn(false);

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(result, Errors.TECHNICIAN_NOT_QUALIFIED);

        verify(technicianRepository)
                .isQualifiedForServiceType(
                        technicianId, serviceTypeId
                );

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldReturnFailureWhenDatabaseThrowsException() {

        // Arrange
        when(customerRepository.findById(customerId))
                .thenThrow(
                        new DataAccessResourceFailureException(
                                "Database unavailable"
                        )
                );

        // Act
        Result<Response> result = handler.handle(request).join();

        // Assert
        assertError(
                result,
                Errors.APPOINTMENT_CREATION_FAILED
        );

        verify(customerRepository).findById(customerId);
        verifyNoInteractions(appointmentRepository);
    }

    // -------------------------
    // Test helpers
    // -------------------------

    private void mockCustomer() {

        Customer customer = mock(Customer.class);

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));
    }

    private void mockVehicle() {

        Vehicle vehicle = mock(Vehicle.class);

        when(vehicle.getCustomerId())
                .thenReturn(customerId);

        when(vehicleRepository.findById(vehicleId))
                .thenReturn(Optional.of(vehicle));
    }

    private void mockDealership() {

        Dealership dealership = mock(Dealership.class);

        when(dealershipRepository.findById(dealershipId))
                .thenReturn(Optional.of(dealership));
    }

    private void mockServiceType(int durationMinutes) {

        ServiceType serviceType = mock(ServiceType.class);

        when(serviceType.getDurationMinutes())
                .thenReturn(durationMinutes);

        when(serviceTypeRepository.findById(serviceTypeId))
                .thenReturn(Optional.of(serviceType));
    }

    private void mockTechnician() {

        Technician technician = mock(Technician.class);

        when(technician.getDealershipId())
                .thenReturn(dealershipId);

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));
    }

    private void mockServiceBay() {

        ServiceBay serviceBay = mock(ServiceBay.class);

        when(serviceBay.getDealershipId())
                .thenReturn(dealershipId);

        when(serviceBayRepository.findById(serviceBayId))
                .thenReturn(Optional.of(serviceBay));
    }

    private void mockValidEntitiesBeforeTechnician() {

        mockCustomer();
        mockVehicle();
        mockDealership();
        mockServiceType(45);
    }

    private void mockValidEntitiesBeforeServiceBay() {

        mockValidEntitiesBeforeTechnician();
        mockTechnician();
    }

    private void assertError(
            Result<Response> result,
            Error expected
    ) {

        assertTrue(result.isFailure());

        Result.Failure<?> failure =
                (Result.Failure<?>) result;

        assertFalse(failure.errorDetails().errors().isEmpty());

        Error actual =
                failure.errorDetails().errors().get(0);

        assertEquals(expected.code(), actual.code());
        assertEquals(expected.type(), actual.type());
    }
}

