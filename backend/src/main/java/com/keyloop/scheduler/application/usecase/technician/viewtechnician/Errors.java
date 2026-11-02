package com.keyloop.scheduler.application.usecase.technician.viewtechnician;

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

    public static final Error DEALERSHIP_NOT_FOUND =
            new Error(
                    "DEALERSHIP_NOT_FOUND",
                    "Dealership not found.",
                    ErrorType.NOT_FOUND,
                    null
            );

    public static final Error TECHNICIAN_RETRIEVAL_FAILED =
            new Error(
                    "TECHNICIAN_RETRIEVAL_FAILED",
                    "Unable to retrieve technicians.",
                    ErrorType.FAILURE,
                    null
            );
}
