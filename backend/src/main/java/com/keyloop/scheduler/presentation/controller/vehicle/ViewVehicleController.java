
package com.keyloop.scheduler.presentation.controller.vehicle;

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

import com.keyloop.scheduler.application.usecase.vehicle.viewvehicle.Handler;
import com.keyloop.scheduler.application.usecase.vehicle.viewvehicle.Query;
import com.keyloop.scheduler.presentation.utility.ErrorOrExtensions;

@RestController
@RequestMapping("/api/{version}/vehicles")
@Tag(
    name = "Vehicles",
    description = "Vehicle management APIs"
)
public class ViewVehicleController {

    private final Handler handler;

    public ViewVehicleController(
        @Qualifier("viewVehicleHandler") Handler handler
    ) {
        this.handler = handler;
    }

    @GetMapping(version = "1.0")
    @Operation(
        summary = "View customer vehicles",
        description = "Retrieve vehicles belonging to a customer with pagination."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved customer vehicles"
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid customer ID or request",
        content = @Content(
            schema = @Schema(implementation = ProblemDetail.class)
        )
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(
            schema = @Schema(implementation = ProblemDetail.class)
        )
    )
    public CompletableFuture<ResponseEntity<Object>> getVehicles(
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
