package com.keyloop.scheduler.presentation.controller.appointment;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import com.keyloop.scheduler.application.usecase.appointment.viewcustomerappointment.Handler;
import com.keyloop.scheduler.application.usecase.appointment.viewcustomerappointment.Query;
import com.keyloop.scheduler.presentation.utility.ErrorOrExtensions;

@RestController
@RequestMapping("/api/{version}/appointments")
@Tag(
        name = "Appointments",
        description = "Appointment management APIs"
)
public class ViewCustomerAppointmentController {

    private final Handler handler;

    public ViewCustomerAppointmentController(
            @Qualifier("viewCustomerAppointmentHandler") Handler handler
    ) {
        this.handler = handler;
    }

    @GetMapping(value = "/customer", version = "1.0")
    @Operation(
            summary = "View customer appointments",
            description = """
                    Retrieve a paginated list of appointments belonging
                    to a specific customer.

                    Each appointment includes vehicle information,
                    dealership details, service information, appointment
                    status, and scheduled start/end times.

                    Appointment times are returned using the
                    dealership's local timezone.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved customer appointments"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid customer ID or request",
            content = @Content(
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
    )
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden"
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    public CompletableFuture<ResponseEntity<Object>> getCustomerAppointments(
            @RequestParam String customerId,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {

        Query query = new Query(
                customerId,
                pageNumber,
                pageSize
        );

        return handler.handle(query)
                .thenApply(ErrorOrExtensions::toResponse);
    }
}
