
import type { ApiErrorResponse } from "./base.types";

export function isApiErrorResponse(
  error: unknown,
): error is ApiErrorResponse {
  if (typeof error !== "object" || error === null) {
    return false;
  }

  const value = error as Record<string, unknown>;

  return (
    typeof value.type === "string" &&
    typeof value.title === "string" &&
    typeof value.detail === "string"
  );
}

export function getApiErrorMessage(
  error: unknown,
  fallback = "Something went wrong. Please try again.",
): string {
  if (!isApiErrorResponse(error)) {
    return fallback;
  }

  // Never expose unexpected server errors to users
  if (error.status && error.status >= 500) {
    return "The server could not complete your request. Please try again later.";
  }

  const firstError = error.errors?.[0];

  if (firstError?.description?.trim()) {
    return firstError.description;
  }

  if (error.detail?.trim()) {
    return error.detail;
  }

  return fallback;
}

export function getApiErrorCode(
  error: unknown,
): string | null {
  if (!isApiErrorResponse(error)) {
    return null;
  }

  return error.errors?.[0]?.code ?? null;
}
