
package com.keyloop.scheduler.presentation.controller.appointment;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.keyloop.scheduler.application.usecase.appointment.viewappointmentavailability.Handler;
import com.keyloop.scheduler.application.usecase.appointment.viewappointmentavailability.Query;
import com.keyloop.scheduler.application.usecase.appointment.viewappointmentavailability.Response;
import com.keyloop.scheduler.presentation.utility.ErrorOrExtensions;

@RestController
@RequestMapping("/api/{version}/appointments")
@Tag(
        name = "Appointments",
        description = "Appointment management APIs"
)
public class ViewAppointmentAvailabilityController {

    private final Handler handler;

    public ViewAppointmentAvailabilityController(
            @Qualifier("viewAppointmentAvailabilityHandler") Handler handler
    ) {
        this.handler = handler;
    }

    @GetMapping(value = "/availability", version = "1.0")
    @Operation(
            summary = "Get appointment availability",
            description = """
                    Retrieve available appointment slots for a dealership,
                    service type, and selected date.

                    Returns qualified technicians and their hourly booking slots.

                    Availability is determined by:
                    - Dealership business hours and date overrides.
                    - Technician qualifications and working hours.
                    - Technician existing appointment conflicts.
                    - Availability of at least one active service bay
                      for the entire service duration.

                    The technicianId parameter is optional.
                    When omitted, availability is returned for all
                    qualified technicians in the dealership.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Appointment availability retrieved successfully",
            content = @Content(
                    schema = @Schema(implementation = Response.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid query parameters or business validation failed",
            content = @Content(
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Dealership, service type, or technician not found",
            content = @Content(
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(
            responseCode = "500",
            description = "Failed to retrieve appointment availability",
            content = @Content(
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    public CompletableFuture<ResponseEntity<Object>> getAvailability(

            @Parameter(
                    description = "Dealership UUID",
                    required = true
            )
            @RequestParam String dealershipId,

            @Parameter(
                    description = "Service type UUID",
                    required = true
            )
            @RequestParam String serviceTypeId,

            @Parameter(
                    description = "Appointment date in yyyy-MM-dd format",
                    required = true,
                    example = "2026-11-04"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @Parameter(
                    description = "Optional technician UUID"
            )
            @RequestParam(required = false)
            String technicianId
    ) {

        Query query = new Query(
                dealershipId,
                serviceTypeId,
                date,
                technicianId
        );

        return handler.handle(query)
                .thenApply(ErrorOrExtensions::toResponse);
    }
}

