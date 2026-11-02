package com.keyloop.scheduler.application.usecase.vehicle.viewvehicle;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.keyloop.scheduler.application.abstraction.dataaccess.VehicleRepository;
import com.keyloop.scheduler.application.common.pagination.PagedList;
import com.keyloop.scheduler.application.common.result.ErrorDetails;
import com.keyloop.scheduler.application.common.result.Result;
import com.keyloop.scheduler.domain.entity.Vehicle;

@Service("viewVehicleHandler")
public class Handler {

    private static final Logger logger =
            LoggerFactory.getLogger(Handler.class);

    private final VehicleRepository vehicleRepository;
    private final Validators validators;

    public Handler(
            VehicleRepository vehicleRepository,
            Validators validators
    ) {
        this.vehicleRepository = vehicleRepository;
        this.validators = validators;
    }

    @Async("applicationTaskExecutor")
    public CompletableFuture<Result<PagedList<Response>>> handle(Query request) {

        logger.debug("Retrieving vehicles for customer.");

        // 1. Validate request
        var validationErrors = validators.validate(request);

        if (!validationErrors.isEmpty()) {
            return CompletableFuture.completedFuture(
                    Result.failure(new ErrorDetails(validationErrors))
            );
        }

        // 2. Convert customer ID to UUID
        UUID customerId = UUID.fromString(request.customerId());

        int pageNumber = request.pageNumber();
        int pageSize = request.pageSize();

        try {
            // 3. Retrieve vehicles by customer ID
            PagedList<Vehicle> vehicles =
                    vehicleRepository.findAllByCustomerId(
                            customerId,
                            pageNumber,
                            pageSize
                    );

            // 4. Map Domain to Response
            List<Response> items = vehicles.items()
                    .stream()
                    .map(Response::from)
                    .toList();

            // 5. Create paginated response
            PagedList<Response> response = PagedList.create(
                    items,
                    vehicles.totalCount(),
                    pageNumber,
                    pageSize
            );

            logger.info(
                    "Successfully retrieved {} vehicles for customer {}.",
                    items.size(),
                    customerId
            );

            return CompletableFuture.completedFuture(
                    Result.success(response)
            );

        } catch (DataAccessException ex) {
            logger.error(
                    "Failed to retrieve vehicles for customer {}.",
                    customerId,
                    ex
            );

            return CompletableFuture.completedFuture(
                    Result.failure(
                            new ErrorDetails(
                                    List.of(Errors.VEHICLE_RETRIEVAL_FAILED)
                            )
                    )
            );
        }
    }
}
