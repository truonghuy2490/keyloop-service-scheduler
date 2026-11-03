
package com.keyloop.scheduler.application.usecase.appointment.viewappointmentavailability;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import com.keyloop.scheduler.application.abstraction.dataaccess.AppointmentRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.BaseRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.DealershipRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.ServiceBayRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository;
import com.keyloop.scheduler.application.common.result.Result;
import com.keyloop.scheduler.application.usecase.appointment.bookappointment.AppointmentScheduleValidator;
import com.keyloop.scheduler.domain.entity.Appointment;
import com.keyloop.scheduler.domain.entity.Dealership;
import com.keyloop.scheduler.domain.entity.ServiceBay;
import com.keyloop.scheduler.domain.entity.ServiceType;
import com.keyloop.scheduler.domain.entity.Technician;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private DealershipRepository dealershipRepository;

    @Mock
    private BaseRepository<ServiceType, UUID> serviceTypeRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @Mock
    private ServiceBayRepository serviceBayRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentScheduleValidator scheduleValidator;

    private Handler handler;

    private final Validators validators = new Validators();

    private UUID dealershipId;
    private UUID serviceTypeId;
    private UUID technicianId;
    private UUID serviceBayId;

    private LocalDate date;
    private Query query;

    @BeforeEach
    void setUp() {
        handler = new Handler(
                validators,
                dealershipRepository,
                serviceTypeRepository,
                technicianRepository,
                serviceBayRepository,
                appointmentRepository,
                scheduleValidator
        );

        dealershipId = UUID.randomUUID();
        serviceTypeId = UUID.randomUUID();
        technicianId = UUID.randomUUID();
        serviceBayId = UUID.randomUUID();

        date = LocalDate.of(2026, 11, 4);

        query = new Query(
                dealershipId.toString(),
                serviceTypeId.toString(),
                date,
                null
        );
    }

    @Test
    void shouldReturnFailureWhenQueryIsInvalid() {
        // Act
        Result<Response> result = handler.handle(null).join();

        // Assert
        assertNotNull(result);
        assertTrue(result.isFailure());

        verifyNoInteractions(
                dealershipRepository,
                serviceTypeRepository,
                technicianRepository,
                serviceBayRepository,
                appointmentRepository
        );
    }

    @Test
    void shouldReturnFailureWhenDealershipNotFound() {
        // Arrange
        when(dealershipRepository.findById(dealershipId))
                .thenReturn(Optional.empty());

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isFailure());

        verify(dealershipRepository).findById(dealershipId);
        verifyNoInteractions(serviceTypeRepository);
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
        assertTrue(result.isFailure());

        verifyNoInteractions(serviceTypeRepository);
    }

    @Test
    void shouldReturnFailureWhenServiceTypeNotFound() {
        // Arrange
        mockValidDealership();

        when(serviceTypeRepository.findById(serviceTypeId))
                .thenReturn(Optional.empty());

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isFailure());

        verify(serviceTypeRepository).findById(serviceTypeId);
        verifyNoInteractions(technicianRepository);
    }

    @Test
    void shouldReturnFailureWhenServiceTypeIsDeleted() {
        // Arrange
        mockValidDealership();

        ServiceType serviceType = mock(ServiceType.class);

        when(serviceType.getIsDeleted()).thenReturn(true);
        when(serviceTypeRepository.findById(serviceTypeId))
                .thenReturn(Optional.of(serviceType));

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isFailure());

        verifyNoInteractions(technicianRepository);
    }

    @Test
    void shouldReturnFailureWhenServiceDurationIsInvalid() {
        // Arrange
        mockValidDealership();

        ServiceType serviceType = mock(ServiceType.class);

        when(serviceType.getIsDeleted()).thenReturn(false);
        when(serviceType.getDurationMinutes()).thenReturn(0);

        when(serviceTypeRepository.findById(serviceTypeId))
                .thenReturn(Optional.of(serviceType));

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isFailure());

        verifyNoInteractions(technicianRepository);
    }

    @Test
    void shouldReturnFailureWhenDealershipDetailsNotFound() {
        // Arrange
        mockValidDealership();
        mockValidServiceType(60);

        when(dealershipRepository.findDetailsById(dealershipId))
                .thenReturn(Optional.empty());

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isFailure());

        verifyNoInteractions(technicianRepository);
    }

    @Test
    void shouldReturnFailureWhenSelectedTechnicianNotQualified() {
        // Arrange
        mockValidDealership();
        mockValidServiceType(60);
        mockDealershipDetails();

        Query selectedTechnicianQuery = new Query(
                dealershipId.toString(),
                serviceTypeId.toString(),
                date,
                technicianId.toString()
        );

        when(technicianRepository.findQualifiedTechnicians(
                dealershipId,
                serviceTypeId,
                technicianId
        )).thenReturn(List.of());

        // Act
        Result<Response> result =
                handler.handle(selectedTechnicianQuery).join();

        // Assert
        assertTrue(result.isFailure());

        verify(technicianRepository)
                .findQualifiedTechnicians(
                        dealershipId,
                        serviceTypeId,
                        technicianId
                );

        verifyNoInteractions(serviceBayRepository);
    }

    @Test
    void shouldReturnSuccessWhenNoQualifiedTechniciansExist() {
        // Arrange
        mockValidDealership();
        mockValidServiceType(60);
        mockDealershipDetails();

        when(technicianRepository.findQualifiedTechnicians(
                dealershipId,
                serviceTypeId,
                null
        )).thenReturn(List.of());

        when(serviceBayRepository.findActiveByDealershipId(
                dealershipId
        )).thenReturn(List.of());

        when(appointmentRepository
                .findActiveAppointmentsByDealershipAndTimeRange(
                        eq(dealershipId),
                        any(Instant.class),
                        any(Instant.class)
                )).thenReturn(List.of());

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldReturnFailureWhenDatabaseThrowsException() {
        // Arrange
        when(dealershipRepository.findById(dealershipId))
                .thenThrow(new DataAccessResourceFailureException(
                        "Database connection failed"
                ));

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isFailure());

        verify(dealershipRepository).findById(dealershipId);
        verifyNoInteractions(serviceTypeRepository);
    }

    @Test
    void shouldReturnUnavailableSlotsWhenNoServiceBaysExist() {
        // Arrange
        mockValidDealership();
        mockValidServiceType(60);
        mockDealershipDetails();

        Technician technician = mockTechnician(true);

        when(technicianRepository.findQualifiedTechnicians(
                dealershipId, serviceTypeId, null
        )).thenReturn(List.of(technician));

        when(serviceBayRepository.findActiveByDealershipId(
                dealershipId
        )).thenReturn(List.of());

        when(appointmentRepository
                .findActiveAppointmentsByDealershipAndTimeRange(
                        eq(dealershipId),
                        any(Instant.class),
                        any(Instant.class)
                )).thenReturn(List.of());

        mockTechnicianDetails();

        // Act
        Result<Response> result = handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        // No service bays means no slot can be booked.
        verify(serviceBayRepository)
                .findActiveByDealershipId(dealershipId);
    }

    // =========================================================
    // Helpers
    // =========================================================

    private void mockValidDealership() {
        Dealership dealership = mock(Dealership.class);

        when(dealership.getIsDeleted()).thenReturn(false);
        when(dealership.getTimeZone()).thenReturn("Asia/Ho_Chi_Minh");

        when(dealershipRepository.findById(dealershipId))
                .thenReturn(Optional.of(dealership));
    }

    private void mockValidServiceType(int durationMinutes) {
        ServiceType serviceType = mock(ServiceType.class);

        when(serviceType.getIsDeleted()).thenReturn(false);
        when(serviceType.getDurationMinutes())
                .thenReturn(durationMinutes);

        when(serviceTypeRepository.findById(serviceTypeId))
                .thenReturn(Optional.of(serviceType));
    }

    private Technician mockTechnician(boolean active) {
        Technician technician = mock(Technician.class);

        when(technician.getId()).thenReturn(technicianId);
        when(technician.getFullName()).thenReturn("John Smith");
        when(technician.getActive()).thenReturn(active);

        return technician;
    }

    private void mockDealershipDetails() {
        // Replace with the actual DealershipDetailsData
        // constructor or fixture from your repository.
        var details = mock(
                DealershipRepository.DealershipDetailsData.class
        );

        when(dealershipRepository.findDetailsById(dealershipId))
                .thenReturn(Optional.of(details));
    }

    private void mockTechnicianDetails() {
        var details = mock(
                TechnicianRepository.TechnicianDetailsData.class
        );

        when(technicianRepository.findDetailsByTechnicianId(
                technicianId
        )).thenReturn(Optional.of(details));
    }
}
