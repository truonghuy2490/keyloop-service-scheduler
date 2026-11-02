package com.keyloop.scheduler.presentation.controller.customer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.keyloop.scheduler.application.usecase.customer.viewcustomer.Handler;
import com.keyloop.scheduler.application.usecase.customer.viewcustomer.Query;
import com.keyloop.scheduler.presentation.utility.ErrorOrExtensions;

@RestController
@RequestMapping("/api/{version}/customers")
@Tag(
    name = "Customers",
    description = "Customer management APIs"
)
public class ViewCustomerController {

    private final Handler handler;

    public ViewCustomerController(
        @Qualifier("viewCustomerHandler") Handler handler
    ) {
        this.handler = handler;
    }

    @GetMapping(version = "1.0")
    @Operation(
        summary = "View customers",
        description = "Retrieve customers with pagination."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved customers"
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
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
    public CompletableFuture<ResponseEntity<Object>> getCustomers(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {

        Query query = new Query(pageNumber, pageSize);

        return handler.handle(query)
                .thenApply(ErrorOrExtensions::toResponse);
    }
}
