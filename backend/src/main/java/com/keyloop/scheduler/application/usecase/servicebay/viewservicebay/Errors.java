package com.keyloop.scheduler.application.usecase.servicebay.viewservicebay;

import com.keyloop.scheduler.application.common.result.Error;
import com.keyloop.scheduler.application.common.result.ErrorType;

public final class Errors {

    private Errors() {}

    public static final Error INVALID_DEALERSHIP_ID =
            new Error(
                    "INVALID_DEALERSHIP_ID",
                    "Dealership ID must be a valid UUID.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error SERVICE_BAY_RETRIEVAL_FAILED =
            new Error(
                    "SERVICE_BAY_RETRIEVAL_FAILED",
                    "Unable to retrieve service bays.",
                    ErrorType.FAILURE,
                    null
            );
}
