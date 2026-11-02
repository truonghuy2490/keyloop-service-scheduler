package com.keyloop.scheduler.application.usecase.customer.viewcustomer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.keyloop.scheduler.application.abstraction.dataaccess.BaseRepository;
import com.keyloop.scheduler.application.common.pagination.PagedList;
import com.keyloop.scheduler.application.common.result.ErrorDetails;
import com.keyloop.scheduler.application.common.result.Result;
import com.keyloop.scheduler.domain.entity.Customer;

@Service("viewCustomerHandler")
public class Handler {

    private static final Logger logger =
            LoggerFactory.getLogger(Handler.class);

    private final BaseRepository<Customer, UUID> customerRepository;

    public Handler(
            BaseRepository<Customer, UUID> customerRepository
    ) {
        this.customerRepository = customerRepository;
    }

    @Async("applicationTaskExecutor")
    public CompletableFuture<Result<PagedList<Response>>> handle(Query request) {

        logger.debug("Retrieving customers.");

        int pageNumber = request.pageNumber();
        int pageSize = request.pageSize();

        try {
            // 1. Retrieve paginated customers
            PagedList<Customer> customers =
                    customerRepository.findAll(pageNumber, pageSize);

            if (customers.items().isEmpty()) {
                logger.info("No customers found.");

                return CompletableFuture.completedFuture(
                        Result.success(
                                PagedList.create(
                                        List.of(),
                                        customers.totalCount(),
                                        pageNumber,
                                        pageSize
                                )
                        )
                );
            }

            // 2. Map Domain to Response
            List<Response> items = customers.items()
                    .stream()
                    .map(Response::from)
                    .toList();

            // 3. Create paginated response
            PagedList<Response> response = PagedList.create(
                    items,
                    customers.totalCount(),
                    pageNumber,
                    pageSize
            );

            logger.info(
                    "Successfully retrieved {} customers.",
                    items.size()
            );

            return CompletableFuture.completedFuture(
                    Result.success(response)
            );

        } catch (DataAccessException ex) {
            logger.error(
                    "Failed to retrieve customers from database.",
                    ex
            );

            return CompletableFuture.completedFuture(
                    Result.failure(
                            new ErrorDetails(
                                    List.of(Errors.CUSTOMER_RETRIEVAL_FAILED)
                            )
                    )
            );
        }
    }
}
