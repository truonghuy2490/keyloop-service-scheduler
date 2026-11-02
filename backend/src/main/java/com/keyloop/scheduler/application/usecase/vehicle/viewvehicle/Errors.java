package com.keyloop.scheduler.application.usecase.vehicle.viewvehicle;

import com.keyloop.scheduler.application.common.result.Error;
import com.keyloop.scheduler.application.common.result.ErrorType;

public final class Errors {

    private Errors() {}

    public static final Error INVALID_CUSTOMER_ID =
            new Error(
                    "INVALID_CUSTOMER_ID",
                    "Customer ID must be a valid UUID.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error VEHICLE_RETRIEVAL_FAILED =
            new Error(
                    "VEHICLE_RETRIEVAL_FAILED",
                    "Unable to retrieve vehicles.",
                    ErrorType.FAILURE,
                    null
            );
}
