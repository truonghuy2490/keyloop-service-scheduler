package com.keyloop.scheduler.application.usecase.appointment.viewcustomerappointment;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.keyloop.scheduler.application.abstraction.dataaccess.AppointmentRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.VehicleRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.BaseRepository;

import com.keyloop.scheduler.application.common.pagination.PagedList;
import com.keyloop.scheduler.application.common.result.ErrorDetails;
import com.keyloop.scheduler.application.common.result.Result;

import com.keyloop.scheduler.domain.entity.Appointment;
import com.keyloop.scheduler.domain.entity.Vehicle;
import com.keyloop.scheduler.domain.entity.Dealership;
import com.keyloop.scheduler.domain.entity.ServiceType;

@Service("viewCustomerAppointmentHandler")
public class Handler {

    private static final Logger logger =
            LoggerFactory.getLogger(Handler.class);

    private final AppointmentRepository appointmentRepository;
    private final VehicleRepository vehicleRepository;
    private final BaseRepository<Dealership, UUID> dealershipRepository;
    private final BaseRepository<ServiceType, UUID> serviceTypeRepository;
    private final Validators validators;

    public Handler(
            AppointmentRepository appointmentRepository,
            VehicleRepository vehicleRepository,
            BaseRepository<Dealership, UUID> dealershipRepository,
            BaseRepository<ServiceType, UUID> serviceTypeRepository,
            Validators validators
    ) {
        this.appointmentRepository = appointmentRepository;
        this.vehicleRepository = vehicleRepository;
        this.dealershipRepository = dealershipRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.validators = validators;
    }

    @Async("applicationTaskExecutor")
    public CompletableFuture<Result<PagedList<Response>>> handle(Query request) {

        logger.debug("Retrieving customer appointments.");

        // 1. Validate request
        var validationErrors = validators.validate(request);

        if (!validationErrors.isEmpty()) {
            return CompletableFuture.completedFuture(
                    Result.failure(new ErrorDetails(validationErrors))
            );
        }

        UUID customerId = UUID.fromString(request.customerId());

        int pageNumber = request.pageNumber();
        int pageSize = request.pageSize();

        try {
            // 2. Retrieve customer appointments
            PagedList<Appointment> appointments =
                    appointmentRepository.findAllByCustomerId(
                            customerId,
                            pageNumber,
                            pageSize
                    );

            // 3. Map Domain to Response
            List<Response> items = appointments.items()
                    .stream()
                    .map(this::mapToResponse)
                    .toList();

            // 4. Create paginated response
            PagedList<Response> response = PagedList.create(
                    items,
                    appointments.totalCount(),
                    pageNumber,
                    pageSize
            );

            logger.info(
                    "Successfully retrieved {} appointments for customer {}.",
                    items.size(),
                    customerId
            );

            return CompletableFuture.completedFuture(
                    Result.success(response)
            );

        } catch (DataAccessException ex) {
            logger.error(
                    "Failed to retrieve appointments for customer {}.",
                    customerId,
                    ex
            );

            return CompletableFuture.completedFuture(
                    Result.failure(
                            new ErrorDetails(
                                    List.of(Errors.APPOINTMENT_RETRIEVAL_FAILED)
                            )
                    )
            );
        }
    }

    private Response mapToResponse(Appointment appointment) {

        Vehicle vehicle = vehicleRepository
                .findById(appointment.getVehicleId())
                .orElseThrow(() -> new IllegalStateException(
                        "Vehicle not found: " + appointment.getVehicleId()
                ));

        Dealership dealership = dealershipRepository
                .findById(appointment.getDealershipId())
                .orElseThrow(() -> new IllegalStateException(
                        "Dealership not found: " + appointment.getDealershipId()
                ));

        ServiceType serviceType = serviceTypeRepository
                .findById(appointment.getServiceTypeId())
                .orElseThrow(() -> new IllegalStateException(
                        "Service type not found: " + appointment.getServiceTypeId()
                ));

        return Response.from(
                appointment,
                vehicle,
                dealership,
                serviceType
        );
    }
}
