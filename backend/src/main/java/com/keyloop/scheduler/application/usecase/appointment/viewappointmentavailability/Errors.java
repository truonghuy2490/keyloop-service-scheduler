package com.keyloop.scheduler.application.usecase.appointment.viewappointmentavailability;

import com.keyloop.scheduler.application.common.result.Error;
import com.keyloop.scheduler.application.common.result.ErrorType;

public final class Errors {

    private Errors() {}

    public static final Error INVALID_REQUEST =
            new Error(
                    "INVALID_REQUEST",
                    "Availability request must not be null.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error INVALID_DEALERSHIP_ID =
            new Error(
                    "INVALID_DEALERSHIP_ID",
                    "Dealership ID must be a valid UUID.",
                    ErrorType.VALIDATION,
                    "dealershipId"
            );

    public static final Error INVALID_SERVICE_TYPE_ID =
            new Error(
                    "INVALID_SERVICE_TYPE_ID",
                    "Service type ID must be a valid UUID.",
                    ErrorType.VALIDATION,
                    "serviceTypeId"
            );

    public static final Error INVALID_DATE =
            new Error(
                    "INVALID_DATE",
                    "Date must not be null.",
                    ErrorType.VALIDATION,
                    "date"
            );

    public static final Error INVALID_TECHNICIAN_ID =
            new Error(
                    "INVALID_TECHNICIAN_ID",
                    "Technician ID must be a valid UUID.",
                    ErrorType.VALIDATION,
                    "technicianId"
            );

    public static final Error DEALERSHIP_NOT_FOUND =
            new Error(
                    "DEALERSHIP_NOT_FOUND",
                    "Dealership was not found.",
                    ErrorType.NOT_FOUND,
                    null
            );

    public static final Error SERVICE_TYPE_NOT_FOUND =
            new Error(
                    "SERVICE_TYPE_NOT_FOUND",
                    "Service type was not found.",
                    ErrorType.NOT_FOUND,
                    null
            );

    public static final Error INVALID_SERVICE_DURATION =
            new Error(
                    "INVALID_SERVICE_DURATION",
                    "Service duration must be greater than zero.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error TECHNICIAN_NOT_FOUND =
            new Error(
                    "TECHNICIAN_NOT_FOUND",
                    "Technician was not found in this dealership.",
                    ErrorType.NOT_FOUND,
                    null
            );

    public static final Error AVAILABILITY_RETRIEVAL_FAILED =
            new Error(
                    "AVAILABILITY_RETRIEVAL_FAILED",
                    "Unable to retrieve appointment availability.",
                    ErrorType.FAILURE,
                    null
            );
}
