package com.keyloop.scheduler.application.usecase.appointment.viewcustomerappointment;

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

    public static final Error APPOINTMENT_RETRIEVAL_FAILED =
            new Error(
                    "APPOINTMENT_RETRIEVAL_FAILED",
                    "Unable to retrieve customer appointments.",
                    ErrorType.FAILURE,
                    null
            );
}
