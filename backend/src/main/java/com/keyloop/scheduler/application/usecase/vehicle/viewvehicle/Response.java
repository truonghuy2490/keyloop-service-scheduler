package com.keyloop.scheduler.application.usecase.vehicle.viewvehicle;

import java.util.UUID;

import com.keyloop.scheduler.domain.entity.Vehicle;

public record Response(
        UUID id,
        UUID customerId,
        String registrationNumber,
        String make,
        String model,
        Short modelYear
) {
    public static Response from(Vehicle vehicle) {
        return new Response(
                vehicle.getId(),
                vehicle.getCustomerId(),
                vehicle.getRegistrationNumber(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getModelYear()
        );
    }
}
