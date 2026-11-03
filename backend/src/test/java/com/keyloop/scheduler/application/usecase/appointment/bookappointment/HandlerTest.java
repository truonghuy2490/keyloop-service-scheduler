package com.keyloop.scheduler.application.usecase.appointment.bookappointment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import com.keyloop.scheduler.application.abstraction.dataaccess.AppointmentRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.BaseRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.DealershipRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.ServiceBayRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository;
import com.keyloop.scheduler.application.abstraction.transaction.TransactionExecutor;
import com.keyloop.scheduler.application.common.result.Error;
import com.keyloop.scheduler.application.common.result.Result;
import com.keyloop.scheduler.domain.entity.Appointment;
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
        private ServiceBayRepository serviceBayRepository;

        @Mock
        private AppointmentRepository appointmentRepository;

        @Mock
        private TechnicianRepository technicianRepository;

        @Mock
        private DealershipRepository dealershipRepository;

        @Mock
        private AppointmentScheduleValidator scheduleValidator;

        @Mock
        private TransactionExecutor transactionExecutor;

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
                                OffsetDateTime.parse("2026-11-05T09:00:00+07:00"));

                handler = new Handler(
                                new Validators(),
                                customerRepository,
                                vehicleRepository,
                                serviceTypeRepository,
                                serviceBayRepository,
                                appointmentRepository,
                                technicianRepository,
                                dealershipRepository,
                                scheduleValidator,
                                transactionExecutor);
        }

        @Test
        void shouldReturnFailureWhenRequestIsNull() {
                Result<Response> result = handler.handle(null).join();

                assertError(result, Errors.INVALID_REQUEST);

                verifyNoInteractions(
                                customerRepository,
                                vehicleRepository,
                                dealershipRepository,
                                serviceTypeRepository,
                                technicianRepository,
                                serviceBayRepository,
                                appointmentRepository);
        }

        @Test
        void shouldReturnFailureWhenCustomerIdIsInvalid() {
                Request invalidRequest = new Request(
                                "invalid-uuid",
                                vehicleId.toString(),
                                dealershipId.toString(),
                                serviceTypeId.toString(),
                                technicianId.toString(),
                                request.startTime());

                Result<Response> result = handler.handle(invalidRequest).join();

                assertError(result, Errors.INVALID_CUSTOMER_ID);

                verifyNoInteractions(customerRepository);
                verifyNoInteractions(appointmentRepository);
        }

        @Test
        void shouldReturnFailureWhenCustomerDoesNotExist() {
                when(customerRepository.findById(customerId))
                                .thenReturn(Optional.empty());

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.CUSTOMER_NOT_FOUND);

                verifyNoInteractions(vehicleRepository);
                verifyNoInteractions(appointmentRepository);
        }

        @Test
        void shouldReturnFailureWhenCustomerIsDeleted() {
                Customer customer = mock(Customer.class);

                when(customer.getIsDeleted()).thenReturn(true);
                when(customerRepository.findById(customerId))
                                .thenReturn(Optional.of(customer));

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.CUSTOMER_NOT_FOUND);

                verifyNoInteractions(vehicleRepository);
        }

        @Test
        void shouldReturnFailureWhenVehicleDoesNotExist() {
                mockCustomer();

                when(vehicleRepository.findById(vehicleId))
                                .thenReturn(Optional.empty());

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.VEHICLE_NOT_FOUND);
                verifyNoInteractions(appointmentRepository);
        }

        @Test
        void shouldReturnFailureWhenVehicleIsDeleted() {
                mockCustomer();

                Vehicle vehicle = mock(Vehicle.class);
                when(vehicle.getIsDeleted()).thenReturn(true);

                when(vehicleRepository.findById(vehicleId))
                                .thenReturn(Optional.of(vehicle));

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.VEHICLE_NOT_FOUND);
                verifyNoInteractions(appointmentRepository);
        }

        @Test
        void shouldReturnFailureWhenVehicleDoesNotBelongToCustomer() {
                mockCustomer();

                Vehicle vehicle = mock(Vehicle.class);

                when(vehicle.getCustomerId())
                                .thenReturn(UUID.randomUUID());

                when(vehicleRepository.findById(vehicleId))
                                .thenReturn(Optional.of(vehicle));

                Result<Response> result = handler.handle(request).join();

                assertError(
                                result,
                                Errors.VEHICLE_NOT_OWNED_BY_CUSTOMER);
        }

        @Test
        void shouldReturnFailureWhenDealershipDoesNotExist() {
                mockCustomer();
                mockVehicle();

                when(dealershipRepository.findById(dealershipId))
                                .thenReturn(Optional.empty());

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.DEALERSHIP_NOT_FOUND);
                verifyNoInteractions(appointmentRepository);
        }

        @Test
        void shouldReturnFailureWhenDealershipIsDeleted() {
                mockCustomer();
                mockVehicle();

                Dealership dealership = mock(Dealership.class);
                when(dealership.getIsDeleted()).thenReturn(true);

                when(dealershipRepository.findById(dealershipId))
                                .thenReturn(Optional.of(dealership));

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.DEALERSHIP_NOT_FOUND);
        }

        @Test
        void shouldReturnFailureWhenServiceTypeDoesNotExist() {
                mockCustomer();
                mockVehicle();
                mockDealership();

                when(serviceTypeRepository.findById(serviceTypeId))
                                .thenReturn(Optional.empty());

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.SERVICE_TYPE_NOT_FOUND);
        }

        @Test
        void shouldReturnFailureWhenServiceTypeIsDeleted() {
                mockCustomer();
                mockVehicle();
                mockDealership();

                ServiceType serviceType = mock(ServiceType.class);
                when(serviceType.getIsDeleted()).thenReturn(true);

                when(serviceTypeRepository.findById(serviceTypeId))
                                .thenReturn(Optional.of(serviceType));

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.SERVICE_TYPE_NOT_FOUND);
        }

        @Test
        void shouldReturnFailureWhenServiceDurationIsZero() {
                mockCustomer();
                mockVehicle();
                mockDealership();
                mockServiceType(0);

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.INVALID_SERVICE_DURATION);
        }

        @Test
        void shouldReturnFailureWhenServiceDurationIsNegative() {
                mockCustomer();
                mockVehicle();
                mockDealership();
                mockServiceType(-30);

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.INVALID_SERVICE_DURATION);
        }

        @Test
        void shouldReturnFailureWhenTechnicianDoesNotExist() {
                mockValidEntitiesBeforeTechnician();

                when(technicianRepository.findById(technicianId))
                                .thenReturn(Optional.empty());

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.TECHNICIAN_NOT_FOUND);
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

                verifyNoInteractions(serviceBayRepository);
        }

        @Test
        void shouldReturnFailureWhenTechnicianBelongsToAnotherDealership() {
                // Arrange
                mockValidEntitiesBeforeTechnician();

                Technician technician = mock(Technician.class);

                when(technician.getActive()).thenReturn(true);

                when(technician.getDealershipId())
                                .thenReturn(UUID.randomUUID());

                when(technicianRepository.findById(technicianId))
                                .thenReturn(Optional.of(technician));

                // Act
                Result<Response> result = handler.handle(request).join();

                // Assert
                assertError(
                                result,
                                Errors.TECHNICIAN_NOT_IN_DEALERSHIP);

                verifyNoInteractions(serviceBayRepository);
        }

        @Test
        void shouldReturnFailureWhenTechnicianIsNotQualified() {
                mockValidEntitiesBeforeTechnician();
                mockTechnician();

                when(technicianRepository.isQualifiedForServiceType(
                                technicianId, serviceTypeId)).thenReturn(false);

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.TECHNICIAN_NOT_QUALIFIED);

                verifyNoInteractions(serviceBayRepository);
        }

        @Test
        void shouldReturnFailureWhenDatabaseThrowsException() {
                when(customerRepository.findById(customerId))
                                .thenThrow(
                                                new DataAccessResourceFailureException(
                                                                "Database unavailable"));

                Result<Response> result = handler.handle(request).join();

                assertError(
                                result,
                                Errors.APPOINTMENT_CREATION_FAILED);

                verifyNoInteractions(appointmentRepository);
        }

        @Test
        void shouldReturnFailureWhenNoServiceBaysExist() {
                mockValidBookingPrerequisites();

                when(serviceBayRepository.findActiveByDealershipId(dealershipId))
                                .thenReturn(List.of());

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.SERVICE_BAY_UNAVAILABLE);

                verify(serviceBayRepository)
                                .findActiveByDealershipId(dealershipId);

                verify(appointmentRepository, never())
                                .save(any(Appointment.class));
        }

        @Test
        void shouldReturnFailureWhenAllServiceBaysAreOccupied() {
                mockValidBookingPrerequisites();

                ServiceBay bay = mockServiceBay(serviceBayId);

                when(serviceBayRepository.findActiveByDealershipId(dealershipId))
                                .thenReturn(List.of(bay));

                when(appointmentRepository.existsServiceBayOverlap(
                                eq(serviceBayId),
                                any(Instant.class),
                                any(Instant.class))).thenReturn(true);

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.SERVICE_BAY_UNAVAILABLE);

                verify(appointmentRepository, never())
                                .save(any(Appointment.class));
        }

        @Test
        void shouldSelectNextServiceBayWhenFirstBayIsOccupied() {
                mockValidBookingPrerequisites();

                UUID secondBayId = UUID.randomUUID();

                ServiceBay firstBay = mockServiceBay(serviceBayId);
                ServiceBay secondBay = mockServiceBay(secondBayId);

                when(serviceBayRepository.findActiveByDealershipId(dealershipId))
                                .thenReturn(List.of(firstBay, secondBay));

                when(appointmentRepository.existsServiceBayOverlap(
                                eq(serviceBayId),
                                any(Instant.class),
                                any(Instant.class))).thenReturn(true);

                when(appointmentRepository.existsServiceBayOverlap(
                                eq(secondBayId),
                                any(Instant.class),
                                any(Instant.class))).thenReturn(false);

                // Stop immediately after selection to verify the chosen bay.
                when(appointmentRepository.save(any(Appointment.class)))
                                .thenAnswer(invocation -> {
                                        Appointment appointment = invocation.getArgument(0);

                                        assertEquals(
                                                        secondBayId,
                                                        appointment.getServiceBayId());

                                        throw new DataAccessResourceFailureException(
                                                        "Stop after verifying service bay assignment");
                                });

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.APPOINTMENT_CREATION_FAILED);

                verify(appointmentRepository)
                                .save(argThat(appointment -> secondBayId.equals(appointment.getServiceBayId())));
        }

        @Test
        void shouldReturnFailureWhenTechnicianIsInactive() {
                // Arrange
                mockValidEntitiesBeforeTechnician();

                Technician technician = mock(Technician.class);

                when(technician.getActive()).thenReturn(false);

                when(technicianRepository.findById(technicianId))
                                .thenReturn(Optional.of(technician));

                // Act
                Result<Response> result = handler.handle(request).join();

                // Assert
                assertError(result, Errors.TECHNICIAN_INACTIVE);

                verifyNoInteractions(serviceBayRepository);
                verifyNoInteractions(appointmentRepository);
        }

        @Test
        void shouldReturnFailureWhenTechnicianHasOverlappingAppointment() {
                mockValidBookingPrerequisites();

                when(appointmentRepository.existsTechnicianOverlap(
                                eq(technicianId),
                                any(Instant.class),
                                any(Instant.class))).thenReturn(true);

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.TECHNICIAN_UNAVAILABLE);
                verify(transactionExecutor).executeResult(any());
                verifyNoInteractions(serviceBayRepository);
                verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        void shouldReturnFailureWhenDatabaseFailsInsideTransaction() {
                mockValidBookingPrerequisites();

                ServiceBay bay = mockServiceBay(serviceBayId);
                when(serviceBayRepository.findActiveByDealershipId(dealershipId))
                                .thenReturn(List.of(bay));
                when(appointmentRepository.save(any(Appointment.class)))
                                .thenThrow(new DataAccessResourceFailureException(
                                                "Database unavailable during insert"));

                Result<Response> result = handler.handle(request).join();

                assertError(result, Errors.APPOINTMENT_CREATION_FAILED);
                verify(transactionExecutor).executeResult(any());
                verify(appointmentRepository).save(any(Appointment.class));
        }

        // =========================================================
        // Helpers
        // =========================================================

        private void mockTransactionExecution() {
                doAnswer(invocation -> {
                        Supplier<?> action = invocation.getArgument(0);
                        return action.get();
                }).when(transactionExecutor).executeResult(any());
        }

        private void mockCustomer() {
                Customer customer = mock(Customer.class);

                when(customerRepository.findById(customerId))
                                .thenReturn(Optional.of(customer));
        }

        private void mockVehicle() {
                Vehicle vehicle = mock(Vehicle.class);

                when(vehicle.getCustomerId()).thenReturn(customerId);

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

                when(technician.getActive()).thenReturn(true);

                when(technician.getDealershipId())
                                .thenReturn(dealershipId);

                when(technicianRepository.findById(technicianId))
                                .thenReturn(Optional.of(technician));
        }

        private ServiceBay mockServiceBay(UUID bayId) {
                ServiceBay bay = mock(ServiceBay.class);

                when(bay.getId()).thenReturn(bayId);

                return bay;
        }

        private void mockValidEntitiesBeforeTechnician() {
                mockCustomer();
                mockVehicle();
                mockDealership();
                mockServiceType(45);
        }

        private void mockValidBookingPrerequisites() {
                mockTransactionExecution();
                mockValidEntitiesBeforeTechnician();
                mockTechnician();

                when(technicianRepository.isQualifiedForServiceType(
                                technicianId, serviceTypeId)).thenReturn(true);

                var dealershipDetails = mock(
                                DealershipRepository.DealershipDetailsData.class);

                when(dealershipDetails.timeZone())
                                .thenReturn("Asia/Ho_Chi_Minh");

                when(dealershipRepository.findDetailsById(dealershipId))
                                .thenReturn(Optional.of(dealershipDetails));

                when(scheduleValidator.isDealershipAvailable(
                                eq(dealershipDetails),
                                any(Instant.class),
                                any(Instant.class))).thenReturn(true);

                var technicianDetails = mock(
                                TechnicianRepository.TechnicianDetailsData.class);

                when(technicianRepository.findDetailsByTechnicianId(technicianId))
                                .thenReturn(Optional.of(technicianDetails));

                when(scheduleValidator.isTechnicianAvailable(
                                eq(technicianDetails),
                                any(java.time.ZoneId.class),
                                any(Instant.class),
                                any(Instant.class))).thenReturn(true);

                // Mockito defaults boolean return values to false,
                // so technician overlap is false unless explicitly stubbed.
        }

        private void assertError(
                        Result<Response> result,
                        Error expected) {
                assertTrue(result.isFailure());

                Result.Failure<?> failure = (Result.Failure<?>) result;

                assertFalse(failure.errorDetails().errors().isEmpty());

                Error actual = failure.errorDetails().errors().get(0);

                assertEquals(expected.code(), actual.code());
                assertEquals(expected.type(), actual.type());
        }
}
