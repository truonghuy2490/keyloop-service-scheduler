package com.keyloop.scheduler.presentation.controller.servicebay;

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

import com.keyloop.scheduler.application.usecase.servicebay.viewservicebay.Handler;
import com.keyloop.scheduler.application.usecase.servicebay.viewservicebay.Query;
import com.keyloop.scheduler.presentation.utility.ErrorOrExtensions;

@RestController
@RequestMapping("/api/{version}/service-bays")
@Tag(
    name = "Service Bays",
    description = "Service bay management APIs"
)
public class ViewServiceBayController {

    private final Handler handler;

    public ViewServiceBayController(
        @Qualifier("viewServiceBayHandler") Handler handler
    ) {
        this.handler = handler;
    }

    @GetMapping(version = "1.0")
    @Operation(
        summary = "View dealership service bays",
        description = "Retrieve service bays belonging to a dealership with pagination."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved dealership service bays"
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid dealership ID or request",
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
    public CompletableFuture<ResponseEntity<Object>> getServiceBays(
            @RequestParam String dealershipId,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {

        Query query = new Query(
                dealershipId,
                pageNumber,
                pageSize
        );

        return handler.handle(query)
                .thenApply(ErrorOrExtensions::toResponse);
    }
}
