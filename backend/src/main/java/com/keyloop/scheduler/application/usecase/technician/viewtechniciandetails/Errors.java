
package com.keyloop.scheduler.application.usecase.technician.viewtechniciandetails;

import com.keyloop.scheduler.application.common.result.Error;
import com.keyloop.scheduler.application.common.result.ErrorType;

public final class Errors {

    private Errors() {
    }

    public static final Error INVALID_TECHNICIAN_ID =
            new Error(
                    "INVALID_TECHNICIAN_ID",
                    "Technician ID must be a valid UUID.",
                    ErrorType.VALIDATION,
                    null
            );

    public static final Error TECHNICIAN_NOT_FOUND =
            new Error(
                    "TECHNICIAN_NOT_FOUND",
                    "Technician not found.",
                    ErrorType.NOT_FOUND,
                    null
            );

    public static final Error TECHNICIAN_DETAILS_RETRIEVAL_FAILED =
            new Error(
                    "TECHNICIAN_DETAILS_RETRIEVAL_FAILED",
                    "Unable to retrieve technician details.",
                    ErrorType.FAILURE,
                    null
            );
}

