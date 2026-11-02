package com.keyloop.scheduler.application.usecase.technician.viewtechniciandetails;

import java.util.ArrayList;
import java.util.EnumMap;
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
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository.DateOverrideData;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository.QualificationData;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository.TechnicianDetailsData;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository.WorkingHoursData;
import com.keyloop.scheduler.application.common.result.ErrorDetails;
import com.keyloop.scheduler.application.common.result.Result;

@Service("viewTechnicianDetailsHandler")
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
                "Retrieving technician details for technician {}.",
                request != null ? request.technicianId() : null
        );

        // 1. Validate request
        var validationErrors = validators.validate(request);

        if (!validationErrors.isEmpty()) {
            logger.info(
                    "Invalid technician ID: {}.",
                    request != null ? request.technicianId() : null
            );

            return CompletableFuture.completedFuture(
                    Result.failure(
                            new ErrorDetails(validationErrors)
                    )
            );
        }

        try {
            UUID technicianId = UUID.fromString(request.technicianId());

            // 2. Retrieve technician details
            var technicianOptional =
                    technicianRepository.findDetailsByTechnicianId(technicianId);

            if (technicianOptional.isEmpty()) {
                logger.info(
                        "Technician {} not found.",
                        technicianId
                );

                return CompletableFuture.completedFuture(
                        Result.failure(
                                new ErrorDetails(
                                        List.of(Errors.TECHNICIAN_NOT_FOUND)
                                )
                        )
                );
            }

            TechnicianDetailsData technician = technicianOptional.get();

            // 3. Map qualifications
            List<Response.QualificationItem> qualifications =
                    technician.qualifications()
                            .stream()
                            .map(this::mapQualification)
                            .toList();

            // 4. Group working hours by day of week
            Map<java.time.DayOfWeek, List<Response.WorkingInterval>> workingHoursMap =
                    new EnumMap<>(java.time.DayOfWeek.class);

            for (WorkingHoursData workingHour : technician.workingHours()) {

                workingHoursMap
                        .computeIfAbsent(
                                workingHour.dayOfWeek(),
                                day -> new ArrayList<>()
                        )
                        .add(
                                new Response.WorkingInterval(
                                        workingHour.startsAt(),
                                        workingHour.endsAt()
                                )
                        );
            }

            List<Response.WorkingHoursItem> workingHours =
                    workingHoursMap.entrySet()
                            .stream()
                            .map(entry -> new Response.WorkingHoursItem(
                                    entry.getKey(),
                                    entry.getValue()
                            ))
                            .toList();

            // 5. Map date overrides
            List<Response.DateOverrideItem> dateOverrides =
                    technician.dateOverrides()
                            .stream()
                            .map(this::mapDateOverride)
                            .toList();

            // 6. Create response
            Response response = new Response(
                    technician.id(),
                    technician.fullName(),
                    technician.active(),
                    new Response.DealershipItem(
                            technician.dealershipId(),
                            technician.dealershipName()
                    ),
                    qualifications,
                    workingHours,
                    dateOverrides,
                    technician.createdAt(),
                    technician.updatedAt()
            );

            logger.info(
                    "Successfully retrieved details for technician {}.",
                    technicianId
            );

            return CompletableFuture.completedFuture(
                    Result.success(response)
            );

        } catch (DataAccessException ex) {
            logger.error(
                    "Failed to retrieve details for technician {}.",
                    request.technicianId(),
                    ex
            );

            return CompletableFuture.completedFuture(
                    Result.failure(
                            new ErrorDetails(
                                    List.of(Errors.TECHNICIAN_DETAILS_RETRIEVAL_FAILED)
                            )
                    )
            );
        }
    }

    private Response.QualificationItem mapQualification(
            QualificationData qualification
    ) {
        return new Response.QualificationItem(
                qualification.serviceTypeId(),
                qualification.name(),
                qualification.description(),
                qualification.durationMinutes(),
                qualification.qualifiedAt()
        );
    }

    private Response.DateOverrideItem mapDateOverride(
            DateOverrideData dateOverride
    ) {
        return new Response.DateOverrideItem(
                dateOverride.workDate(),
                dateOverride.isUnavailable(),
                dateOverride.startsAt(),
                dateOverride.endsAt(),
                dateOverride.reason()
        );
    }
}
