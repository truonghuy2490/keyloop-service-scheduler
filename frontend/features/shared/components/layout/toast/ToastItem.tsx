"use client";

import { useEffect } from "react";
import { useAppDispatch } from "@/lib/redux/hooks";
import { removeToast, Toast, ToastType } from "@/features/shared/toast.slice";

export default function ToastItem({ toast }: { toast: Toast }) {
  const dispatch = useAppDispatch();

  // when old toat remove, the new once comes, that's why id changes,
  // so we need to clear the timer when toast.id changes
  useEffect(() => {
    const timer = setTimeout(() => {
      dispatch(removeToast({ id: toast.id }));
    }, toast.duration);

    return () => clearTimeout(timer);
  }, [toast.id, dispatch, toast.duration]);

  const typeClass =
    toast.type === ToastType.Success
      ? "alert-success"
      : toast.type === ToastType.Error
        ? "alert-error"
        : toast.type === ToastType.Warning
          ? "alert-warning"
          : "alert-info";

  return (
    <div
      data-testid={toast.testId}
      className={`alert ${typeClass} shadow-xl border-none text-white w-[calc(100vw-2rem)] sm:w-auto sm:min-w-[320px] whitespace-normal flex items-center gap-3 cursor-pointer hover:scale-[1.02] transition-transform duration-200 ease-out`}
      onClick={() => dispatch(removeToast({ id: toast.id }))}
    >
      {/* Icon based on type */}
      {toast.type === ToastType.Success && (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          className="stroke-current shrink-0 h-6 w-6"
          fill="none"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth="2"
            d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
      )}
      {toast.type === ToastType.Error && (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          className="stroke-current shrink-0 h-6 w-6"
          fill="none"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth="2"
            d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
      )}
      {toast.type === ToastType.Warning && (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          className="stroke-current shrink-0 h-6 w-6"
          fill="none"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth="2"
            d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
          />
        </svg>
      )}
      {toast.type === ToastType.Info && (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          className="stroke-current shrink-0 h-6 w-6"
          fill="none"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth="2"
            d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
      )}

      <div className="flex flex-col gap-0.5">
        <span className="font-semibold text-sm">{toast.message}</span>
      </div>
    </div>
  );
}
