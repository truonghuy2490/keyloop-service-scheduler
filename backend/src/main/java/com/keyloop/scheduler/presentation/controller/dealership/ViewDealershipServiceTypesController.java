
package com.keyloop.scheduler.presentation.controller.dealership;

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

import com.keyloop.scheduler.application.usecase.dealership.viewdealershipservicetypes.Handler;
import com.keyloop.scheduler.application.usecase.dealership.viewdealershipservicetypes.Query;
import com.keyloop.scheduler.application.usecase.dealership.viewdealershipservicetypes.Response;
import com.keyloop.scheduler.presentation.utility.ErrorOrExtensions;

@RestController
@RequestMapping("/api/{version}/dealerships")
@Tag(
        name = "Dealerships",
        description = "Dealership management APIs"
)
public class ViewDealershipServiceTypesController {

    private final Handler handler;

    public ViewDealershipServiceTypesController(
            @Qualifier("viewDealershipServiceTypesHandler") Handler handler
    ) {
        this.handler = handler;
    }

    @GetMapping(
            value = "/{dealershipId}/service-types",
            version = "1.0"
    )
    @Operation(
            summary = "View dealership service types",
            description = "Retrieve all active service types supported by a specific dealership."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved dealership service types",
            content = @Content(
                    schema = @Schema(implementation = Response.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid dealership ID",
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
    public CompletableFuture<ResponseEntity<Object>> getDealershipServiceTypes(
            @PathVariable String dealershipId
    ) {

        Query query = new Query(dealershipId);

        return handler.handle(query)
                .thenApply(ErrorOrExtensions::toResponse);
    }
}
