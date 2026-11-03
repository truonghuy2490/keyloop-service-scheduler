package com.keyloop.scheduler.application.usecase.appointment.viewcustomerappointment;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

import com.keyloop.scheduler.domain.entity.Appointment;
import com.keyloop.scheduler.domain.entity.Vehicle;
import com.keyloop.scheduler.domain.entity.Dealership;
import com.keyloop.scheduler.domain.entity.ServiceType;

public record Response(
        UUID id,
        String status,
        VehicleItem vehicle,
        DealershipItem dealership,
        ServiceItem service,
        ScheduleItem schedule
) {

    public record VehicleItem(
            String registrationNumber,
            String make,
            String model
    ) {}

    public record DealershipItem(
            String name
    ) {}

    public record ServiceItem(
            String name,
            Integer durationMinutes
    ) {}

    public record ScheduleItem(
            OffsetDateTime startTime,
            OffsetDateTime endTime
    ) {}

    public static Response from(
            Appointment appointment,
            Vehicle vehicle,
            Dealership dealership,
            ServiceType serviceType
    ) {

        ZoneId zone = ZoneId.of(dealership.getTimeZone());

        return new Response(
                appointment.getId(),
                appointment.getStatus(),

                new VehicleItem(
                        vehicle.getRegistrationNumber(),
                        vehicle.getMake(),
                        vehicle.getModel()
                ),

                new DealershipItem(
                        dealership.getName()
                ),

                new ServiceItem(
                        serviceType.getName(),
                        serviceType.getDurationMinutes()
                ),

                new ScheduleItem(
                        appointment.getStartTime()
                                .atZone(zone)
                                .toOffsetDateTime(),
                        appointment.getEndTime()
                                .atZone(zone)
                                .toOffsetDateTime()
                )
        );
    }
}
