package com.keyloop.scheduler.application.usecase.appointment.bookappointment;

import com.keyloop.scheduler.application.common.result.Error;
import com.keyloop.scheduler.application.common.result.ErrorType;

public final class Errors {

    private Errors() {}

    // Request validation

    public static final Error INVALID_REQUEST =
            new Error(
                    "INVALID_REQUEST",
                    "Booking request must not be null.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error INVALID_CUSTOMER_ID =
            new Error(
                    "INVALID_CUSTOMER_ID",
                    "Customer ID must be a valid UUID.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error INVALID_VEHICLE_ID =
            new Error(
                    "INVALID_VEHICLE_ID",
                    "Vehicle ID must be a valid UUID.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error INVALID_DEALERSHIP_ID =
            new Error(
                    "INVALID_DEALERSHIP_ID",
                    "Dealership ID must be a valid UUID.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error INVALID_SERVICE_TYPE_ID =
            new Error(
                    "INVALID_SERVICE_TYPE_ID",
                    "Service type ID must be a valid UUID.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error INVALID_TECHNICIAN_ID =
            new Error(
                    "INVALID_TECHNICIAN_ID",
                    "Technician ID must be a valid UUID.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error INVALID_SERVICE_BAY_ID =
            new Error(
                    "INVALID_SERVICE_BAY_ID",
                    "Service bay ID must be a valid UUID.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error INVALID_START_TIME =
            new Error(
                    "INVALID_START_TIME",
                    "Start time must be a valid ISO-8601 date-time with an offset.",
                    ErrorType.VALIDATION,
                    null
            );

    // Customer

    public static final Error CUSTOMER_NOT_FOUND =
            new Error(
                    "CUSTOMER_NOT_FOUND",
                    "Customer was not found.",
                    ErrorType.NOT_FOUND,
                    null
            );

    public static final Error CUSTOMER_INACTIVE =
            new Error(
                    "CUSTOMER_INACTIVE",
                    "Customer is inactive.",
                    ErrorType.VALIDATION,
                    null
            );

    // Vehicle

    public static final Error VEHICLE_NOT_FOUND =
            new Error(
                    "VEHICLE_NOT_FOUND",
                    "Vehicle was not found.",
                    ErrorType.NOT_FOUND,
                    null
            );

    public static final Error VEHICLE_NOT_OWNED_BY_CUSTOMER =
            new Error(
                    "VEHICLE_NOT_OWNED_BY_CUSTOMER",
                    "Vehicle does not belong to the specified customer.",
                    ErrorType.VALIDATION,
                    null
            );

    // Dealership

    public static final Error DEALERSHIP_NOT_FOUND =
            new Error(
                    "DEALERSHIP_NOT_FOUND",
                    "Dealership was not found.",
                    ErrorType.NOT_FOUND,
                    null
            );

    public static final Error DEALERSHIP_INACTIVE =
            new Error(
                    "DEALERSHIP_INACTIVE",
                    "Dealership is inactive.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error DEALERSHIP_CLOSED =
            new Error(
                    "DEALERSHIP_CLOSED",
                    "Dealership is closed during the requested appointment time.",
                    ErrorType.VALIDATION,
                    null
            );

    // Service type

    public static final Error SERVICE_TYPE_NOT_FOUND =
            new Error(
                    "SERVICE_TYPE_NOT_FOUND",
                    "Service type was not found.",
                    ErrorType.NOT_FOUND,
                    null
            );

    public static final Error SERVICE_TYPE_INACTIVE =
            new Error(
                    "SERVICE_TYPE_INACTIVE",
                    "Service type is inactive.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error INVALID_SERVICE_DURATION =
            new Error(
                    "INVALID_SERVICE_DURATION",
                    "Service duration must be greater than zero.",
                    ErrorType.VALIDATION,
                    null
            );

    // Technician

    public static final Error TECHNICIAN_NOT_FOUND =
            new Error(
                    "TECHNICIAN_NOT_FOUND",
                    "Technician was not found.",
                    ErrorType.NOT_FOUND,
                    null
            );

    public static final Error TECHNICIAN_NOT_IN_DEALERSHIP =
            new Error(
                    "TECHNICIAN_NOT_IN_DEALERSHIP",
                    "Technician does not belong to the specified dealership.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error TECHNICIAN_INACTIVE =
            new Error(
                    "TECHNICIAN_INACTIVE",
                    "Technician is inactive.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error TECHNICIAN_NOT_QUALIFIED =
            new Error(
                    "TECHNICIAN_NOT_QUALIFIED",
                    "Technician is not qualified for the requested service type.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error TECHNICIAN_UNAVAILABLE =
            new Error(
                    "TECHNICIAN_UNAVAILABLE",
                    "Technician is unavailable during the requested appointment time.",
                    ErrorType.VALIDATION,
                    null
            );

    // Service bay

    public static final Error SERVICE_BAY_NOT_FOUND =
            new Error(
                    "SERVICE_BAY_NOT_FOUND",
                    "Service bay was not found.",
                    ErrorType.NOT_FOUND,
                    null
            );

    public static final Error SERVICE_BAY_NOT_IN_DEALERSHIP =
            new Error(
                    "SERVICE_BAY_NOT_IN_DEALERSHIP",
                    "Service bay does not belong to the specified dealership.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error SERVICE_BAY_INACTIVE =
            new Error(
                    "SERVICE_BAY_INACTIVE",
                    "Service bay is inactive.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error SERVICE_BAY_UNAVAILABLE =
            new Error(
                    "SERVICE_BAY_UNAVAILABLE",
                    "Service bay is unavailable during the requested appointment time.",
                    ErrorType.VALIDATION,
                    null
            );

    // Persistence

    public static final Error APPOINTMENT_CREATION_FAILED =
            new Error(
                    "APPOINTMENT_CREATION_FAILED",
                    "Unable to create appointment.",
                    ErrorType.FAILURE,
                    null
            );
}
