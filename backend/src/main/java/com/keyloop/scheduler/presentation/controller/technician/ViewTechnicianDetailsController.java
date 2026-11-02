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

import com.keyloop.scheduler.application.usecase.technician.viewtechniciandetails.Handler;
import com.keyloop.scheduler.application.usecase.technician.viewtechniciandetails.Query;
import com.keyloop.scheduler.presentation.utility.ErrorOrExtensions;

@RestController
@RequestMapping("/api/{version}/technicians")
@Tag(
    name = "Technicians",
    description = "Technician management APIs"
)
public class ViewTechnicianDetailsController {

    private final Handler handler;

    public ViewTechnicianDetailsController(
        @Qualifier("viewTechnicianDetailsHandler") Handler handler
    ) {
        this.handler = handler;
    }

    @GetMapping(value = "/{technicianId}", version = "1.0")
    @Operation(
        summary = "View technician details",
        description = """
            Retrieve detailed information about a technician, including
            dealership information, service qualifications, working hours,
            and date overrides.
            """
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved technician details"
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid technician ID or request",
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
        description = "Technician not found",
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
    public CompletableFuture<ResponseEntity<Object>> getTechnicianDetails(
            @PathVariable String technicianId
    ) {

        Query query = new Query(technicianId);

        return handler.handle(query)
                .thenApply(ErrorOrExtensions::toResponse);
    }
}
