package com.keyloop.scheduler.application.usecase.customer.viewcustomer;

import com.keyloop.scheduler.application.common.result.Error;
import com.keyloop.scheduler.application.common.result.ErrorType;

public final class Errors {

    private Errors() {}

    public static final Error CUSTOMER_RETRIEVAL_FAILED =
            new Error(
                    "CUSTOMER_RETRIEVAL_FAILED",
                    "Unable to retrieve customers.",
                    ErrorType.FAILURE,
                    null
            );
}
