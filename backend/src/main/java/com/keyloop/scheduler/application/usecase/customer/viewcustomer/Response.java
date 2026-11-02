package com.keyloop.scheduler.application.usecase.customer.viewcustomer;

import java.util.UUID;

import com.keyloop.scheduler.domain.entity.Customer;

public record Response(
        UUID id,
        String fullName,
        String email,
        String phone
) {
    public static Response from(Customer customer) {
        return new Response(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }
}
