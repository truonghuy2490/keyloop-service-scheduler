package com.keyloop.scheduler.application.usecase.technician.viewtechnician;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository.TechnicianData;
import com.keyloop.scheduler.application.common.result.ErrorDetails;
import com.keyloop.scheduler.application.common.result.Result;

@Service("viewTechnicianHandler")
public class Handler {

    private static final Logger logger =
            LoggerFactory.getLogger(Handler.class);

    private final TechnicianRepository technicianRepository;
    private final Validators validators;

    public Handler(
            TechnicianRepository technicianRepository,
            Validators validators
    ) {
        this.technicianRepository = technicianRepository;
        this.validators = validators;
    }

    @Async("applicationTaskExecutor")
    public CompletableFuture<Result<Response>> handle(Query request) {

        logger.debug(
                "Retrieving technicians for dealership {}.",
                request != null ? request.dealershipId() : null
        );

        // 1. Validate request
        var validationErrors = validators.validate(request);

        if (!validationErrors.isEmpty()) {
            logger.info(
                    "Invalid dealership ID: {}.",
                    request != null ? request.dealershipId() : null
            );

            return CompletableFuture.completedFuture(
                    Result.failure(
                            new ErrorDetails(validationErrors)
                    )
            );
        }

        try {
            UUID dealershipId = UUID.fromString(request.dealershipId());

            // 2. Retrieve dealership and technicians
            List<TechnicianData> rows =
                    technicianRepository.findDetailsByDealershipId(dealershipId);

            if (rows.isEmpty()) {
                logger.info(
                        "Dealership {} not found.",
                        dealershipId
                );

                return CompletableFuture.completedFuture(
                        Result.failure(
                                new ErrorDetails(
                                        List.of(Errors.DEALERSHIP_NOT_FOUND)
                                )
                        )
                );
            }

            // 3. Group technicians and qualifications
            TechnicianData dealership = rows.get(0);

            Map<UUID, TechnicianAccumulator> technicianMap =
                    new LinkedHashMap<>();

            for (TechnicianData row : rows) {

                // Skip when dealership has no technicians
                if (row.technicianId() == null) {
                    continue;
                }

                TechnicianAccumulator technician =
                        technicianMap.computeIfAbsent(
                                row.technicianId(),
                                id -> new TechnicianAccumulator(
                                        id,
                                        row.fullName(),
                                        row.active()
                                )
                        );

                // Add qualification when available
                if (row.serviceTypeId() != null) {
                    technician.qualifications().putIfAbsent(
                            row.serviceTypeId(),
                            new Response.QualificationItem(
                                    row.serviceTypeId(),
                                    row.serviceName()
                            )
                    );
                }
            }

            // 4. Map to response
            List<Response.TechnicianItem> items =
                    technicianMap.values()
                            .stream()
                            .map(technician -> new Response.TechnicianItem(
                                    technician.id(),
                                    technician.fullName(),
                                    technician.active(),
                                    new ArrayList<>(
                                            technician.qualifications().values()
                                    )
                            ))
                            .toList();

            // 5. Create response
            Response response = new Response(
                    dealership.dealershipId(),
                    dealership.dealershipName(),
                    items,
                    items.size()
            );

            logger.info(
                    "Successfully retrieved {} technicians for dealership {}.",
                    items.size(),
                    dealershipId
            );

            return CompletableFuture.completedFuture(
                    Result.success(response)
            );

        } catch (DataAccessException ex) {
            logger.error(
                    "Failed to retrieve technicians for dealership {}.",
                    request.dealershipId(),
                    ex
            );

            return CompletableFuture.completedFuture(
                    Result.failure(
                            new ErrorDetails(
                                    List.of(Errors.TECHNICIAN_RETRIEVAL_FAILED)
                            )
                    )
            );
        }
    }

    private record TechnicianAccumulator(
            UUID id,
            String fullName,
            boolean active,
            Map<UUID, Response.QualificationItem> qualifications
    ) {

        private TechnicianAccumulator(
                UUID id,
                String fullName,
                boolean active
        ) {
            this(
                    id,
                    fullName,
                    active,
                    new LinkedHashMap<>()
            );
        }
    }
}
