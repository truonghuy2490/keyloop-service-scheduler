package com.keyloop.scheduler.application.usecase.servicebay.viewservicebay;

import java.util.UUID;

import com.keyloop.scheduler.domain.entity.ServiceBay;

public record Response(
        UUID id,
        UUID dealershipId,
        String bayCode,
        boolean active
) {
    public static Response from(ServiceBay serviceBay) {
        return new Response(
                serviceBay.getId(),
                serviceBay.getDealershipId(),
                serviceBay.getBayCode(),
                serviceBay.getActive()
        );
    }
}
