import {
  createApi,
  fetchBaseQuery,
  FetchArgs,
  FetchBaseQueryError,
  BaseQueryFn,
} from "@reduxjs/toolkit/query/react";

import { ENV_CONFIGS } from "@/lib/config/env"
import { ApiErrorResponse } from "./base.types";

// ================= Base Query =================

const rawBaseQuery = fetchBaseQuery({
  baseUrl: `${ENV_CONFIGS.apiUrl.replace(/\/$/, "")}/api/1.0/`,
  headers: {
    Accept: "application/json",
  },
});

// ================= Error Mapping =================

const mapErrorResponse = (
  error: FetchBaseQueryError
): ApiErrorResponse => {
  const rawErrorData =
    typeof error.data === "object" && error.data !== null
      ? (error.data as Record<string, unknown>)
      : {};

  const httpStatus =
    typeof error.status === "number" ? error.status : undefined;

  const bodyStatus =
    typeof rawErrorData.status === "number"
      ? rawErrorData.status
      : undefined;

  return {
    type:
      typeof rawErrorData.type === "string"
        ? rawErrorData.type
        : "about:blank",

    title:
      typeof rawErrorData.title === "string"
        ? rawErrorData.title
        : "An error occurred",

    status: bodyStatus ?? httpStatus,

    detail:
      typeof rawErrorData.detail === "string"
        ? rawErrorData.detail
        : getFallbackErrorMessage(error),

    instance:
      typeof rawErrorData.instance === "string"
        ? rawErrorData.instance
        : undefined,

    errors: Array.isArray(rawErrorData.errors)
      ? rawErrorData.errors as ApiErrorResponse["errors"]
      : undefined,
  };
};

// ================= Fallback Error =================

const getFallbackErrorMessage = (
  error: FetchBaseQueryError
): string => {
  switch (error.status) {
    case "FETCH_ERROR":
      return "Unable to connect to the server.";

    case "TIMEOUT_ERROR":
      return "The request timed out.";

    case "PARSING_ERROR":
      return "Unable to parse the server response.";

    case "CUSTOM_ERROR":
      return error.error || "An unexpected error occurred.";

    default:
      return "Something went wrong. Please try again later.";
  }
};

// ================= Custom Base Query =================

const customBaseQuery: BaseQueryFn<
  string | FetchArgs,
  unknown,
  ApiErrorResponse
> = async (args, api, extraOptions) => {
  const result = await rawBaseQuery(
    args,
    api,
    extraOptions
  );

  if (result.error) {
    return {
      error: mapErrorResponse(result.error),
    };
  }

  return {
    data: result.data,
  };
};

// ================= Base API =================

export const baseApiSlice = createApi({
  reducerPath: "api",

  baseQuery: customBaseQuery,

  tagTypes: [
    "Appointment",
    "Dealership",
    "Vehicle",
    "ServiceType",
    "ServiceBay",
    "Customer",
    "Technician",
  ],

  endpoints: () => ({}),

  keepUnusedDataFor: 15,
});