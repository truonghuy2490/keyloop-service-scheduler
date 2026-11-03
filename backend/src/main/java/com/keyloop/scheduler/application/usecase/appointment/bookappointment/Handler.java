package com.keyloop.scheduler.application.usecase.appointment.bookappointment;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import com.keyloop.scheduler.application.abstraction.dataaccess.AppointmentRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.BaseRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.DealershipRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.ServiceBayRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository;
import com.keyloop.scheduler.application.abstraction.transaction.TransactionExecutor;
import com.keyloop.scheduler.application.common.result.ErrorDetails;
import com.keyloop.scheduler.application.common.result.Result;
import com.keyloop.scheduler.domain.entity.Appointment;
import com.keyloop.scheduler.domain.entity.Customer;
import com.keyloop.scheduler.domain.entity.Dealership;
import com.keyloop.scheduler.domain.entity.ServiceBay;
import com.keyloop.scheduler.domain.entity.ServiceType;
import com.keyloop.scheduler.domain.entity.Vehicle;
import com.keyloop.scheduler.domain.enums.AppointmentStatus;

@Service("bookAppointmentHandler")
public class Handler {

        private static final Logger logger = LoggerFactory.getLogger(Handler.class);

        private final Validators validators;

        private final BaseRepository<Customer, UUID> customerRepository;
        private final BaseRepository<Vehicle, UUID> vehicleRepository;
        private final BaseRepository<ServiceType, UUID> serviceTypeRepository;

        private final ServiceBayRepository serviceBayRepository;
        private final AppointmentRepository appointmentRepository;
        private final TechnicianRepository technicianRepository;
        private final DealershipRepository dealershipRepository;

        private final AppointmentScheduleValidator scheduleValidator;
        private final TransactionExecutor transactionExecutor;

        public Handler(
                        Validators validators,
                        BaseRepository<Customer, UUID> customerRepository,
                        BaseRepository<Vehicle, UUID> vehicleRepository,
                        BaseRepository<ServiceType, UUID> serviceTypeRepository,
                        ServiceBayRepository serviceBayRepository,
                        AppointmentRepository appointmentRepository,
                        TechnicianRepository technicianRepository,
                        DealershipRepository dealershipRepository,
                        AppointmentScheduleValidator scheduleValidator,
                        TransactionExecutor transactionExecutor

        ) {
                this.validators = validators;
                this.customerRepository = customerRepository;
                this.dealershipRepository = dealershipRepository;
                this.vehicleRepository = vehicleRepository;
                this.serviceTypeRepository = serviceTypeRepository;
                this.serviceBayRepository = serviceBayRepository;
                this.appointmentRepository = appointmentRepository;
                this.technicianRepository = technicianRepository;
                this.scheduleValidator = scheduleValidator;
                this.transactionExecutor = transactionExecutor;
        }

        @Async("applicationTaskExecutor")
        public CompletableFuture<Result<Response>> handle(Request request) {

                logger.debug(
                                "Booking appointment for customer {}.",
                                request != null ? request.customerId() : null);

                // 1. Validate request
                var validationErrors = validators.validate(request);

                if (!validationErrors.isEmpty()) {
                        logger.info(
                                        "Invalid appointment booking request.");

                        return CompletableFuture.completedFuture(
                                        Result.failure(
                                                        new ErrorDetails(validationErrors)));
                }

                try {
                        UUID customerId = UUID.fromString(request.customerId());
                        UUID vehicleId = UUID.fromString(request.vehicleId());
                        UUID dealershipId = UUID.fromString(request.dealershipId());
                        UUID serviceTypeId = UUID.fromString(request.serviceTypeId());
                        UUID technicianId = UUID.fromString(request.technicianId());

                        Instant startTime = request.startTime().toInstant();

                        /*
                         * 2. Validate business data
                         */

                        // Customer exists and active
                        var customerResult = customerRepository.findById(customerId);

                        if (customerResult.isEmpty()) {
                                logger.info("Customer {} was not found.", customerId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.CUSTOMER_NOT_FOUND))));
                        }

                        Customer customer = customerResult.get();

                        if (customer.getIsDeleted()) {
                                logger.info("Customer {} is deleted.", customerId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.CUSTOMER_NOT_FOUND))));
                        }

                        // Vehicle belongs to customer
                        var vehicleResult = vehicleRepository.findById(vehicleId);

                        if (vehicleResult.isEmpty()) {
                                logger.info("Vehicle {} was not found.", vehicleId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.VEHICLE_NOT_FOUND))));
                        }

                        Vehicle vehicle = vehicleResult.get();

                        if (vehicle.getIsDeleted()) {
                                logger.info("Vehicle {} is deleted.", vehicleId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.VEHICLE_NOT_FOUND))));
                        }

                        if (!vehicle.getCustomerId().equals(customerId)) {
                                logger.info(
                                                "Vehicle {} does not belong to customer {}.",
                                                vehicleId,
                                                customerId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.VEHICLE_NOT_OWNED_BY_CUSTOMER))));
                        }

                        // Dealership exists and is active
                        var dealershipResult = dealershipRepository.findById(dealershipId);

                        if (dealershipResult.isEmpty()) {
                                logger.info("Dealership {} was not found.", dealershipId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.DEALERSHIP_NOT_FOUND))));
                        }

                        Dealership dealership = dealershipResult.get();

                        if (dealership.getIsDeleted()) {
                                logger.info("Dealership {} is deleted.", dealershipId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.DEALERSHIP_NOT_FOUND))));
                        }

                        // ServiceType exists and is active
                        var serviceTypeResult = serviceTypeRepository.findById(serviceTypeId);

                        if (serviceTypeResult.isEmpty()) {
                                logger.info("ServiceType {} was not found.", serviceTypeId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.SERVICE_TYPE_NOT_FOUND))));
                        }

                        ServiceType serviceType = serviceTypeResult.get();

                        if (serviceType.getIsDeleted()) {
                                logger.info("ServiceType {} is deleted.", serviceTypeId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.SERVICE_TYPE_NOT_FOUND))));
                        }

                        Integer durationMinutes = serviceType.getDurationMinutes();

                        if (durationMinutes == null || durationMinutes <= 0) {
                                logger.info(
                                                "ServiceType {} has an invalid duration.",
                                                serviceTypeId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.INVALID_SERVICE_DURATION))));
                        }

                        // Technician exists and belongs to dealership
                        var technicianResult = technicianRepository.findById(technicianId);

                        if (technicianResult.isEmpty()) {
                                logger.info("Technician {} was not found.", technicianId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.TECHNICIAN_NOT_FOUND))));
                        }

                        var technician = technicianResult.get();

                        if (technician.getIsDeleted()) {
                                logger.info("Technician {} is deleted.", technicianId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.TECHNICIAN_NOT_FOUND))));
                        }

                        if (!technician.getActive()) {
                                logger.info(
                                                "Technician {} is inactive.",
                                                technicianId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.TECHNICIAN_INACTIVE))));
                        }

                        if (!technician.getDealershipId().equals(dealershipId)) {
                                logger.info(
                                                "Technician {} does not belong to dealership {}.",
                                                technicianId,
                                                dealershipId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.TECHNICIAN_NOT_IN_DEALERSHIP))));
                        }

                        /*
                         * 3. Validate technician qualification
                         */
                        boolean isQualified = technicianRepository.isQualifiedForServiceType(
                                        technicianId,
                                        serviceTypeId);

                        if (!isQualified) {
                                logger.info(
                                                "Technician {} is not qualified for service type {}.",
                                                technicianId,
                                                serviceTypeId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.TECHNICIAN_NOT_QUALIFIED))));
                        }

                        logger.debug(
                                        "Technician {} is qualified for service type {}.",
                                        technicianId,
                                        serviceTypeId);

                        /*
                         * 4. Calculate appointment duration
                         */
                        Instant endTime = startTime.plusSeconds(
                                        durationMinutes.longValue() * 60L);

                        logger.debug(
                                        "Calculated appointment schedule: startTime={}, endTime={}, duration={} minutes.",
                                        startTime,
                                        endTime,
                                        durationMinutes);

                        /*
                         * 5. Validate schedule
                         */

                        // Retrieve dealership schedule
                        var dealershipDetailsResult = dealershipRepository.findDetailsById(dealershipId);

                        if (dealershipDetailsResult.isEmpty()) {
                                logger.info(
                                                "Dealership schedule {} was not found.",
                                                dealershipId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.DEALERSHIP_NOT_FOUND))));
                        }

                        var dealershipDetails = dealershipDetailsResult.get();

                        ZoneId dealershipZone = ZoneId.of(
                                        dealershipDetails.timeZone());

                        // Validate dealership business hours and date overrides
                        boolean isDealershipAvailable = scheduleValidator.isDealershipAvailable(
                                        dealershipDetails,
                                        startTime,
                                        endTime);

                        if (!isDealershipAvailable) {
                                logger.info(
                                                "Dealership {} is closed for appointment from {} to {}.",
                                                dealershipId,
                                                startTime,
                                                endTime);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.DEALERSHIP_CLOSED))));
                        }

                        // Retrieve technician schedule
                        var technicianDetailsResult = technicianRepository.findDetailsByTechnicianId(technicianId);

                        if (technicianDetailsResult.isEmpty()) {
                                logger.info(
                                                "Technician schedule {} was not found.",
                                                technicianId);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.TECHNICIAN_NOT_FOUND))));
                        }

                        var technicianDetails = technicianDetailsResult.get();

                        // Validate technician working hours and date overrides
                        boolean isTechnicianAvailable = scheduleValidator.isTechnicianAvailable(
                                        technicianDetails,
                                        dealershipZone,
                                        startTime,
                                        endTime);

                        if (!isTechnicianAvailable) {
                                logger.info(
                                                "Technician {} is not working from {} to {}.",
                                                technicianId,
                                                startTime,
                                                endTime);

                                return CompletableFuture.completedFuture(
                                                Result.failure(
                                                                new ErrorDetails(
                                                                                List.of(Errors.TECHNICIAN_UNAVAILABLE))));
                        }

                        logger.debug(
                                        "Schedule validation passed for dealership {} and technician {}.",
                                        dealershipId,
                                        technicianId);

                        /*
                         * 6-8. Execute booking within @Transaction
                         */

                        Result<Response> bookingResult = transactionExecutor.executeResult(() -> {

                                // 6. Check technician appointment overlap
                                boolean technicianHasOverlap = appointmentRepository.existsTechnicianOverlap(
                                                technicianId,
                                                startTime,
                                                endTime);

                                if (technicianHasOverlap) {
                                        logger.info(
                                                        "Technician {} already has an appointment between {} and {}.",
                                                        technicianId,
                                                        startTime,
                                                        endTime);

                                        return Result.failure(
                                                        new ErrorDetails(
                                                                        List.of(Errors.TECHNICIAN_UNAVAILABLE)));
                                }

                                // 7. Automatically assign an available service bay
                                List<ServiceBay> serviceBays = serviceBayRepository.findActiveByDealershipId(
                                                dealershipId);

                                ServiceBay selectedServiceBay = null;

                                for (ServiceBay bay : serviceBays) {

                                        boolean hasOverlap = appointmentRepository.existsServiceBayOverlap(
                                                        bay.getId(),
                                                        startTime,
                                                        endTime);

                                        if (!hasOverlap) {
                                                selectedServiceBay = bay;
                                                break;
                                        }
                                }

                                if (selectedServiceBay == null) {
                                        logger.info(
                                                        "No available service bay for dealership {} between {} and {}.",
                                                        dealershipId,
                                                        startTime,
                                                        endTime);

                                        return Result.failure(
                                                        new ErrorDetails(
                                                                        List.of(Errors.SERVICE_BAY_UNAVAILABLE)));
                                }

                                UUID serviceBayId = selectedServiceBay.getId();

                                // 8. Create appointment
                                Appointment appointment = Appointment.builder()
                                                .customerId(customerId)
                                                .vehicleId(vehicleId)
                                                .dealershipId(dealershipId)
                                                .serviceTypeId(serviceTypeId)
                                                .technicianId(technicianId)
                                                .serviceBayId(serviceBayId)
                                                .startTime(startTime)
                                                .endTime(endTime)
                                                .status(AppointmentStatus.CONFIRMED.name())
                                                .isDeleted(false)
                                                .build();

                                Appointment savedAppointment = appointmentRepository.save(appointment);

                                logger.info(
                                                "Appointment {} persisted for customer {}.",
                                                savedAppointment.getId(),
                                                customerId);

                                // 9. Map response
                                ZoneId responseZone = ZoneId.of(
                                                dealership.getTimeZone());

                                OffsetDateTime appointmentStartTime = savedAppointment.getStartTime()
                                                .atZone(responseZone)
                                                .toOffsetDateTime();

                                OffsetDateTime appointmentEndTime = savedAppointment.getEndTime()
                                                .atZone(responseZone)
                                                .toOffsetDateTime();

                                Response response = new Response(
                                                savedAppointment.getId(),
                                                savedAppointment.getStatus(),

                                                new Response.CustomerItem(
                                                                customer.getId(),
                                                                customer.getFullName()),

                                                new Response.VehicleItem(
                                                                vehicle.getId(),
                                                                vehicle.getRegistrationNumber(),
                                                                vehicle.getMake(),
                                                                vehicle.getModel(),
                                                                vehicle.getModelYear() != null
                                                                                ? vehicle.getModelYear().intValue()
                                                                                : null),

                                                new Response.DealershipItem(
                                                                dealership.getId(),
                                                                dealership.getName(),
                                                                dealership.getTimeZone()),

                                                new Response.ServiceItem(
                                                                serviceType.getId(),
                                                                serviceType.getName(),
                                                                serviceType.getDescription(),
                                                                serviceType.getDurationMinutes()),

                                                new Response.TechnicianItem(
                                                                technician.getId(),
                                                                technician.getFullName()),

                                                new Response.ServiceBayItem(
                                                                selectedServiceBay.getId(),
                                                                selectedServiceBay.getBayCode()),

                                                new Response.ScheduleItem(
                                                                appointmentStartTime,
                                                                appointmentEndTime,
                                                                durationMinutes,
                                                                dealership.getTimeZone()),

                                                savedAppointment.getCreatedAt(),
                                                savedAppointment.getUpdatedAt());

                                return Result.success(response);
                        });

                        return CompletableFuture.completedFuture(bookingResult);

                } catch (DataAccessException ex) {
                        logger.error(
                                        "Failed to create appointment for customer {}.",
                                        request.customerId(),
                                        ex);

                        return CompletableFuture.completedFuture(
                                        Result.failure(
                                                        new ErrorDetails(
                                                                        List.of(Errors.APPOINTMENT_CREATION_FAILED))));
                }
        }
}
