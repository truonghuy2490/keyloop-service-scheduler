package com.keyloop.scheduler.presentation.controller.appointment;

import java.util.concurrent.CompletableFuture;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.keyloop.scheduler.application.usecase.appointment.bookappointment.Handler;
import com.keyloop.scheduler.application.usecase.appointment.bookappointment.Request;
import com.keyloop.scheduler.application.usecase.appointment.bookappointment.Response;
import com.keyloop.scheduler.presentation.utility.ErrorOrExtensions;

@RestController
@RequestMapping("/api/{version}/appointments")
@Tag(
        name = "Appointments",
        description = "Appointment management APIs"
)
public class CreateAppointmentController {

    private final Handler handler;

    public CreateAppointmentController(
            @Qualifier("bookAppointmentHandler") Handler handler
    ) {
        this.handler = handler;
    }

    @PostMapping(version = "1.0")
    @Operation(
            summary = "Book an appointment",
            description = """
                    Book a service appointment for a customer's vehicle.
                    Validates dealership business hours, technician qualifications,
                    working hours, and availability of the technician and service bay
                    for the entire service duration.
                    """
    )
    @ApiResponse(
            responseCode = "201",
            description = "Appointment booked successfully",
            content = @Content(
                    schema = @Schema(implementation = Response.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request or business validation failed",
            content = @Content(
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "A referenced resource was not found",
            content = @Content(
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(
            responseCode = "500",
            description = "Failed to create appointment",
            content = @Content(
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    public CompletableFuture<ResponseEntity<Object>> createAppointment(
            @RequestBody Request request
    ) {
        return handler.handle(request)
                .thenApply(ErrorOrExtensions::toResponse);
    }
}
