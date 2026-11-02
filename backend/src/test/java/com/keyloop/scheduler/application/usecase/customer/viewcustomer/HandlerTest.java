package com.keyloop.scheduler.application.usecase.customer.viewcustomer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import com.keyloop.scheduler.application.abstraction.dataaccess.BaseRepository;
import com.keyloop.scheduler.application.common.pagination.PagedList;
import com.keyloop.scheduler.application.common.result.Error;
import com.keyloop.scheduler.application.common.result.Result;
import com.keyloop.scheduler.domain.entity.Customer;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private BaseRepository<Customer, UUID> customerRepository;

    private Handler handler;

    @BeforeEach
    void setUp() {
        handler = new Handler(customerRepository);
    }

    @Test
    void shouldReturnSuccessWhenCustomersExist() {

        // Arrange
        int pageNumber = 1;
        int pageSize = 10;

        Query query = new Query(pageNumber, pageSize);

        Customer customer1 = createCustomer();
        Customer customer2 = createCustomer();

        List<Customer> customers = List.of(
                customer1,
                customer2
        );

        PagedList<Customer> pagedCustomers =
                PagedList.create(
                        customers,
                        2,
                        pageNumber,
                        pageSize
                );

        when(customerRepository.findAll(pageNumber, pageSize))
                .thenReturn(pagedCustomers);

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        Result.Success<?> success =
                (Result.Success<?>) result;

        @SuppressWarnings("unchecked")
        PagedList<Response> response =
                (PagedList<Response>) success.value();

        assertEquals(2, response.items().size());
        assertEquals(2, response.totalCount());
        assertEquals(pageNumber, response.pageNumber());
        assertEquals(pageSize, response.pageSize());

        // Verify response mapping
        Response firstCustomer = response.items().get(0);

        assertEquals(customer1.getId(), firstCustomer.id());
        assertEquals(customer1.getFullName(), firstCustomer.fullName());
        assertEquals(customer1.getEmail(), firstCustomer.email());
        assertEquals(customer1.getPhone(), firstCustomer.phone());

        verify(customerRepository).findAll(pageNumber, pageSize);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void shouldReturnSuccessWhenNoCustomersExist() {

        // Arrange
        int pageNumber = 1;
        int pageSize = 10;

        Query query = new Query(pageNumber, pageSize);

        PagedList<Customer> emptyCustomers =
                PagedList.create(
                        List.of(),
                        0,
                        pageNumber,
                        pageSize
                );

        when(customerRepository.findAll(pageNumber, pageSize))
                .thenReturn(emptyCustomers);

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        Result.Success<?> success =
                (Result.Success<?>) result;

        @SuppressWarnings("unchecked")
        PagedList<Response> response =
                (PagedList<Response>) success.value();

        assertTrue(response.items().isEmpty());
        assertEquals(0, response.totalCount());
        assertEquals(pageNumber, response.pageNumber());
        assertEquals(pageSize, response.pageSize());

        verify(customerRepository).findAll(pageNumber, pageSize);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void shouldPreserveTotalCountWhenRequestedPageHasNoItems() {

        // Arrange
        int pageNumber = 5;
        int pageSize = 10;
        long totalCount = 42;

        Query query = new Query(pageNumber, pageSize);

        PagedList<Customer> emptyPage =
                PagedList.create(
                        List.of(),
                        totalCount,
                        pageNumber,
                        pageSize
                );

        when(customerRepository.findAll(pageNumber, pageSize))
                .thenReturn(emptyPage);

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertTrue(result.isSuccess());

        Result.Success<?> success =
                (Result.Success<?>) result;

        @SuppressWarnings("unchecked")
        PagedList<Response> response =
                (PagedList<Response>) success.value();

        assertTrue(response.items().isEmpty());

        // Total count must still reflect all matching records
        assertEquals(totalCount, response.totalCount());

        assertEquals(pageNumber, response.pageNumber());
        assertEquals(pageSize, response.pageSize());

        verify(customerRepository).findAll(pageNumber, pageSize);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void shouldReturnFailureWhenDatabaseThrowsException() {

        // Arrange
        int pageNumber = 1;
        int pageSize = 10;

        Query query = new Query(pageNumber, pageSize);

        when(customerRepository.findAll(pageNumber, pageSize))
                .thenThrow(
                        new DataAccessResourceFailureException(
                                "Database unavailable"
                        )
                );

        // Act
        Result<PagedList<Response>> result =
                handler.handle(query).join();

        // Assert
        assertTrue(result.isFailure());

        Result.Failure<?> failure =
                (Result.Failure<?>) result;

        assertFalse(failure.errorDetails().errors().isEmpty());

        Error error = failure.errorDetails().errors().get(0);

        assertEquals(
                Errors.CUSTOMER_RETRIEVAL_FAILED.code(),
                error.code()
        );

        assertEquals(
                Errors.CUSTOMER_RETRIEVAL_FAILED.type(),
                error.type()
        );

        verify(customerRepository).findAll(pageNumber, pageSize);
        verifyNoMoreInteractions(customerRepository);
    }

    private Customer createCustomer() {

        Customer customer = mock(Customer.class);

        when(customer.getId()).thenReturn(UUID.randomUUID());
        when(customer.getFullName()).thenReturn("Nguyen Van A");
        when(customer.getEmail()).thenReturn("nguyenvana@example.com");
        when(customer.getPhone()).thenReturn("0901234567");

        return customer;
    }
}
