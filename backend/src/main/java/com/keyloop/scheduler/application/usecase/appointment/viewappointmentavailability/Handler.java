package com.keyloop.scheduler.application.usecase.appointment.viewappointmentavailability;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.keyloop.scheduler.application.abstraction.dataaccess.AppointmentRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.BaseRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.DealershipRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.ServiceBayRepository;
import com.keyloop.scheduler.application.common.result.Error;
import com.keyloop.scheduler.application.common.result.ErrorDetails;
import com.keyloop.scheduler.application.common.result.Result;

import com.keyloop.scheduler.domain.entity.Appointment;
import com.keyloop.scheduler.domain.entity.Dealership;
import com.keyloop.scheduler.domain.entity.ServiceBay;
import com.keyloop.scheduler.domain.entity.ServiceType;
import com.keyloop.scheduler.domain.entity.Technician;

import com.keyloop.scheduler.application.usecase.appointment.bookappointment.AppointmentScheduleValidator;

@Service("viewAppointmentAvailabilityHandler")
public class Handler {

    private static final Logger logger =
            LoggerFactory.getLogger(Handler.class);

    private static final int SLOT_INTERVAL_MINUTES = 60;

    private final Validators validators;

    private final DealershipRepository dealershipRepository;
    private final BaseRepository<ServiceType, UUID> serviceTypeRepository;
    private final TechnicianRepository technicianRepository;
    private final ServiceBayRepository serviceBayRepository;
    private final AppointmentRepository appointmentRepository;

    private final AppointmentScheduleValidator scheduleValidator;

    public Handler(
            Validators validators,
            DealershipRepository dealershipRepository,
            BaseRepository<ServiceType, UUID> serviceTypeRepository,
            TechnicianRepository technicianRepository,
            ServiceBayRepository serviceBayRepository,
            AppointmentRepository appointmentRepository,
            AppointmentScheduleValidator scheduleValidator
    ) {
        this.validators = validators;
        this.dealershipRepository = dealershipRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.technicianRepository = technicianRepository;
        this.serviceBayRepository = serviceBayRepository;
        this.appointmentRepository = appointmentRepository;
        this.scheduleValidator = scheduleValidator;
    }

    @Async("applicationTaskExecutor")
    public CompletableFuture<Result<Response>> handle(Query query) {

        logger.debug(
                "Retrieving appointment availability for dealership {}.",
                query != null ? query.dealershipId() : null
        );

        // 1. Validate request
        var validationErrors = validators.validate(query);

        if (!validationErrors.isEmpty()) {
            return failure(validationErrors);
        }

        try {
            UUID dealershipId = UUID.fromString(query.dealershipId());
            UUID serviceTypeId = UUID.fromString(query.serviceTypeId());

            UUID technicianId =
                    query.technicianId() == null
                            || query.technicianId().isBlank()
                            ? null
                            : UUID.fromString(query.technicianId());

            LocalDate date = query.date();

            // 2. Validate dealership
            var dealershipResult =
                    dealershipRepository.findById(dealershipId);

            if (dealershipResult.isEmpty()
                    || dealershipResult.get().getIsDeleted()) {
                return failure(Errors.DEALERSHIP_NOT_FOUND);
            }

            Dealership dealership = dealershipResult.get();

            ZoneId zone = ZoneId.of(dealership.getTimeZone());

            // 3. Validate service type
            var serviceTypeResult =
                    serviceTypeRepository.findById(serviceTypeId);

            if (serviceTypeResult.isEmpty()
                    || serviceTypeResult.get().getIsDeleted()) {
                return failure(Errors.SERVICE_TYPE_NOT_FOUND);
            }

            ServiceType serviceType = serviceTypeResult.get();

            Integer durationMinutes = serviceType.getDurationMinutes();

            if (durationMinutes == null || durationMinutes <= 0) {
                return failure(Errors.INVALID_SERVICE_DURATION);
            }

            // 4. Retrieve dealership schedule
            var dealershipDetailsResult =
                    dealershipRepository.findDetailsById(dealershipId);

            if (dealershipDetailsResult.isEmpty()) {
                return failure(Errors.DEALERSHIP_NOT_FOUND);
            }

            var dealershipDetails = dealershipDetailsResult.get();

            // 5. Retrieve qualified technicians
            List<Technician> technicians =
                    technicianRepository.findQualifiedTechnicians(
                            dealershipId,
                            serviceTypeId,
                            technicianId
                    );

            if (technicianId != null && technicians.isEmpty()) {
                return failure(Errors.TECHNICIAN_NOT_FOUND);
            }

            // 6. Retrieve active service bays
            List<ServiceBay> serviceBays =
                    serviceBayRepository.findActiveByDealershipId(
                            dealershipId
                    );

            // 7. Retrieve appointments occupying resources
            Instant dayStart =
                    date.atStartOfDay(zone).toInstant();

            Instant dayEnd =
                    date.plusDays(1)
                            .atStartOfDay(zone)
                            .toInstant();

            List<Appointment> appointments =
                    appointmentRepository
                            .findActiveAppointmentsByDealershipAndTimeRange(
                                    dealershipId,
                                    dayStart,
                                    dayEnd
                            );

            // 8. Build availability response
            List<Response.TechnicianAvailabilityItem> technicianItems =
                    new ArrayList<>();

            for (Technician technician : technicians) {

                var technicianDetailsResult =
                        technicianRepository.findDetailsByTechnicianId(
                                technician.getId()
                        );

                List<Response.AvailabilitySlot> slots =
                        new ArrayList<>();

                if (technicianDetailsResult.isPresent()) {

                    var technicianDetails =
                            technicianDetailsResult.get();

                    // Generate hourly candidate slots
                    for (int hour = 0; hour < 24; hour++) {

                        LocalTime localStart = LocalTime.of(hour, 0);

                        ZonedDateTime zonedStart =
                                date.atTime(localStart).atZone(zone);

                        ZonedDateTime zonedEnd =
                                zonedStart.plusMinutes(durationMinutes);

                        Instant startTime = zonedStart.toInstant();
                        Instant endTime = zonedEnd.toInstant();

                        // Check dealership business hours
                        boolean dealershipAvailable =
                                scheduleValidator.isDealershipAvailable(
                                        dealershipDetails,
                                        startTime,
                                        endTime
                                );

                        // Check technician working hours
                        boolean technicianAvailable =
                                scheduleValidator.isTechnicianAvailable(
                                        technicianDetails,
                                        zone,
                                        startTime,
                                        endTime
                                );

                        // Check technician appointment overlap
                        boolean technicianBusy =
                                appointments.stream().anyMatch(appointment ->
                                        technician.getId().equals(
                                                appointment.getTechnicianId()
                                        )
                                        && overlaps(
                                                startTime,
                                                endTime,
                                                appointment.getStartTime(),
                                                appointment.getEndTime()
                                        )
                                );

                        // Check whether at least one service bay is free
                        boolean serviceBayAvailable =
                                serviceBays.stream().anyMatch(bay ->
                                        appointments.stream().noneMatch(
                                                appointment ->
                                                        bay.getId().equals(
                                                                appointment.getServiceBayId()
                                                        )
                                                        && overlaps(
                                                                startTime,
                                                                endTime,
                                                                appointment.getStartTime(),
                                                                appointment.getEndTime()
                                                        )
                                        )
                                );

                        boolean available =
                                technician.getActive()
                                && dealershipAvailable
                                && technicianAvailable
                                && !technicianBusy
                                && serviceBayAvailable;

                        slots.add(
                                new Response.AvailabilitySlot(
                                        zonedStart.toOffsetDateTime(),
                                        zonedEnd.toOffsetDateTime(),
                                        available
                                )
                        );
                    }
                }

                technicianItems.add(
                        new Response.TechnicianAvailabilityItem(
                                technician.getId(),
                                technician.getFullName(),
                                technician.getActive(),
                                slots
                        )
                );
            }

            Response response = new Response(
                    dealershipId,
                    serviceTypeId,
                    date,
                    zone.getId(),
                    durationMinutes,
                    technicianItems
            );

            logger.info(
                    "Retrieved appointment availability for dealership {}, date {}, technicians {}.",
                    dealershipId,
                    date,
                    technicianItems.size()
            );

            return CompletableFuture.completedFuture(
                    Result.success(response)
            );

        } catch (DataAccessException ex) {

            logger.error(
                    "Failed to retrieve appointment availability.",
                    ex
            );

            return failure(Errors.AVAILABILITY_RETRIEVAL_FAILED);
        }
    }

    private boolean overlaps(
            Instant startA,
            Instant endA,
            Instant startB,
            Instant endB
    ) {
        return startA.isBefore(endB)
                && endA.isAfter(startB);
    }

    private CompletableFuture<Result<Response>> failure(Error error) {
        return failure(List.of(error));
    }

    private CompletableFuture<Result<Response>> failure(
            List<Error> errors
    ) {
        return CompletableFuture.completedFuture(
                Result.failure(new ErrorDetails(errors))
        );
    }
}
