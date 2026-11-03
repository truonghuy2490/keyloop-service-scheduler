package com.keyloop.scheduler.application.usecase.dealership.viewdealershipservicetypes;

import com.keyloop.scheduler.application.common.result.Error;
import com.keyloop.scheduler.application.common.result.ErrorType;

public final class Errors {

    private Errors() {
    }

    public static final Error INVALID_QUERY = new Error(
            "INVALID_QUERY",
            "Query must not be null.",
            ErrorType.VALIDATION,
            null);

    public static final Error INVALID_DEALERSHIP_ID = new Error(
            "INVALID_DEALERSHIP_ID",
            "Dealership ID must be a valid UUID.",
            ErrorType.VALIDATION,
            null);

    public static final Error DEALERSHIP_NOT_FOUND = new Error(
            "DEALERSHIP_NOT_FOUND",
            "Dealership was not found.",
            ErrorType.NOT_FOUND,
            null);

    public static final Error SERVICE_TYPES_RETRIEVAL_FAILED = new Error(
            "SERVICE_TYPES_RETRIEVAL_FAILED",
            "Unable to retrieve dealership service types.",
            ErrorType.FAILURE,
            null);

    public static final Error INVALID_PAGE_NUMBER = new Error(
            "INVALID_PAGE_NUMBER",
            "Page number must be greater than or equal to 1.",
            ErrorType.VALIDATION,
            "pageNumber");

    public static final Error INVALID_PAGE_SIZE = new Error(
            "INVALID_PAGE_SIZE",
            "Page size must be between 1 and 100.",
            ErrorType.VALIDATION,
            "pageSize");
}
