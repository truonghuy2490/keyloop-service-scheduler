package com.keyloop.scheduler.presentation.controller.technician;

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

import com.keyloop.scheduler.application.usecase.technician.viewtechnician.Handler;
import com.keyloop.scheduler.application.usecase.technician.viewtechnician.Query;
import com.keyloop.scheduler.presentation.utility.ErrorOrExtensions;

@RestController
@RequestMapping("/api/{version}/dealerships/{dealershipId}/technicians")
@Tag(
    name = "Technicians",
    description = "Technician management APIs"
)
public class ViewTechnicianController {

    private final Handler handler;

    public ViewTechnicianController(
        @Qualifier("viewTechnicianHandler") Handler handler
    ) {
        this.handler = handler;
    }

    @GetMapping(version = "1.0")
    @Operation(
        summary = "View dealership technicians",
        description = "Retrieve technicians belonging to a dealership, including their service qualifications."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved dealership technicians"
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid dealership ID or request",
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
        responseCode = "404",
        description = "Dealership not found",
        content = @Content(
            schema = @Schema(implementation = ProblemDetail.class)
        )
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(
            schema = @Schema(implementation = ProblemDetail.class)
        )
    )
    public CompletableFuture<ResponseEntity<Object>> getTechnicians(
            @PathVariable String dealershipId
    ) {

        Query query = new Query(dealershipId);

        return handler.handle(query)
                .thenApply(ErrorOrExtensions::toResponse);
    }
}
