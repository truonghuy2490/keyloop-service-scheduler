
package com.keyloop.scheduler.application.usecase.dealership.viewdealershipservicetypes;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.keyloop.scheduler.application.abstraction.dataaccess.DealershipRepository;
import com.keyloop.scheduler.application.common.pagination.PagedList;
import com.keyloop.scheduler.application.common.result.ErrorDetails;
import com.keyloop.scheduler.application.common.result.Result;

@Service("viewDealershipServiceTypesHandler")
public class Handler {

    private static final Logger logger =
            LoggerFactory.getLogger(Handler.class);

    private final Validators validators;
    private final DealershipRepository dealershipRepository;

    public Handler(
            Validators validators,
            DealershipRepository dealershipRepository
    ) {
        this.validators = validators;
        this.dealershipRepository = dealershipRepository;
    }

    @Async("applicationTaskExecutor")
    public CompletableFuture<Result<PagedList<Response>>> handle(Query query) {

        // 1. Validate query
        var validationErrors = validators.validate(query);

        if (!validationErrors.isEmpty()) {
            return CompletableFuture.completedFuture(
                    Result.failure(
                            new ErrorDetails(validationErrors)
                    )
            );
        }

        UUID dealershipId = UUID.fromString(query.dealershipId());

        int pageNumber = query.pageNumber();
        int pageSize = query.pageSize();

        try {

            // 2. Check dealership
            var dealershipResult =
                    dealershipRepository.findById(dealershipId);

            if (dealershipResult.isEmpty()
                    || dealershipResult.get().getIsDeleted()) {

                logger.info(
                        "Dealership {} was not found.",
                        dealershipId
                );

                return CompletableFuture.completedFuture(
                        Result.failure(
                                new ErrorDetails(
                                        List.of(Errors.DEALERSHIP_NOT_FOUND)
                                )
                        )
                );
            }

            // 3. Retrieve paginated service types
            PagedList<DealershipRepository.ServiceTypeData> serviceTypes =
                    dealershipRepository.findActiveByDealershipId(
                            dealershipId,
                            pageNumber,
                            pageSize
                    );

            // 4. Map data to response
            List<Response> items = serviceTypes.items()
                    .stream()
                    .map(serviceType -> new Response(
                            serviceType.id(),
                            serviceType.name(),
                            serviceType.description(),
                            serviceType.durationMinutes()
                    ))
                    .toList();

            // 5. Create paginated response
            PagedList<Response> response = PagedList.create(
                    items,
                    serviceTypes.totalCount(),
                    pageNumber,
                    pageSize
            );

            logger.info(
                    "Retrieved {} service types for dealership {}.",
                    items.size(),
                    dealershipId
            );

            return CompletableFuture.completedFuture(
                    Result.success(response)
            );

        } catch (DataAccessException ex) {

            logger.error(
                    "Failed to retrieve service types for dealership {}.",
                    dealershipId,
                    ex
            );

            return CompletableFuture.completedFuture(
                    Result.failure(
                            new ErrorDetails(
                                    List.of(Errors.SERVICE_TYPES_RETRIEVAL_FAILED)
                            )
                    )
            );
        }
    }
}

