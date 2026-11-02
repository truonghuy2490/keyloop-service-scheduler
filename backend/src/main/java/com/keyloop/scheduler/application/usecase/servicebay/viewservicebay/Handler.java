package com.keyloop.scheduler.application.usecase.servicebay.viewservicebay;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.keyloop.scheduler.application.abstraction.dataaccess.ServiceBayRepository;
import com.keyloop.scheduler.application.common.pagination.PagedList;
import com.keyloop.scheduler.application.common.result.ErrorDetails;
import com.keyloop.scheduler.application.common.result.Result;
import com.keyloop.scheduler.domain.entity.ServiceBay;

@Service("viewServiceBayHandler")
public class Handler {

    private static final Logger logger =
            LoggerFactory.getLogger(Handler.class);

    private final ServiceBayRepository serviceBayRepository;
    private final Validators validators;

    public Handler(
            ServiceBayRepository serviceBayRepository,
            Validators validators
    ) {
        this.serviceBayRepository = serviceBayRepository;
        this.validators = validators;
    }

    @Async("applicationTaskExecutor")
    public CompletableFuture<Result<PagedList<Response>>> handle(Query request) {

        logger.debug("Retrieving service bays for dealership.");

        // 1. Validate request
        var validationErrors = validators.validate(request);

        if (!validationErrors.isEmpty()) {
            return CompletableFuture.completedFuture(
                    Result.failure(new ErrorDetails(validationErrors))
            );
        }

        // 2. Convert dealership ID to UUID
        UUID dealershipId = UUID.fromString(request.dealershipId());

        int pageNumber = request.pageNumber();
        int pageSize = request.pageSize();

        try {
            // 3. Retrieve service bays by dealership ID
            PagedList<ServiceBay> serviceBays =
                    serviceBayRepository.findAllByDealershipId(
                            dealershipId,
                            pageNumber,
                            pageSize
                    );

            // 4. Map Domain to Response
            List<Response> items = serviceBays.items()
                    .stream()
                    .map(Response::from)
                    .toList();

            // 5. Create paginated response
            PagedList<Response> response = PagedList.create(
                    items,
                    serviceBays.totalCount(),
                    pageNumber,
                    pageSize
            );

            logger.info(
                    "Successfully retrieved {} service bays for dealership {}.",
                    items.size(),
                    dealershipId
            );

            return CompletableFuture.completedFuture(
                    Result.success(response)
            );

        } catch (DataAccessException ex) {
            logger.error(
                    "Failed to retrieve service bays for dealership {}.",
                    dealershipId,
                    ex
            );

            return CompletableFuture.completedFuture(
                    Result.failure(
                            new ErrorDetails(
                                    List.of(Errors.SERVICE_BAY_RETRIEVAL_FAILED)
                            )
                    )
            );
        }
    }
}
