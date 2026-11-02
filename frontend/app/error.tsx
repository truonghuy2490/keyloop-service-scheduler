
"use client";

import { useEffect } from "react";
import Link from "next/link";
import { AlertTriangle, RefreshCw, Home } from "lucide-react";

import { APP_ROUTES } from "@/lib/api/routes";

type ErrorPageProps = {
  error: Error & { digest?: string };
  reset: () => void;
};

export default function ErrorPage({ error, reset }: ErrorPageProps) {
  useEffect(() => {
    console.error("Application error:", error);
  }, [error]);

  return (
    <div className="flex min-h-[70vh] w-full items-center justify-center px-6 py-12">
      <div className="relative w-full max-w-md overflow-hidden rounded-2xl border border-base-300 bg-base-100 p-8 text-center shadow-lg">
        <div className="absolute inset-x-0 top-0 h-1 bg-error" />

        <div className="mb-6 flex justify-center">
          <div className="rounded-full bg-error/10 p-4 text-error">
            <AlertTriangle size={56} strokeWidth={1.8} />
          </div>
        </div>

        <h1 className="text-6xl font-black tracking-tight text-error">
          500
        </h1>

        <h2 className="mt-4 text-xl font-bold text-base-content">
          Unexpected Error Occurred
        </h2>

        <p className="mt-2 text-sm leading-relaxed text-base-content/60">
          Something went wrong while processing your request.
          Please try again or return to the home page.
        </p>

        {error.digest && (
          <p className="mt-3 text-xs text-base-content/40">
            Error reference: {error.digest}
          </p>
        )}

        <div className="mt-8 flex flex-col gap-3">
          <button
            type="button"
            onClick={reset}
            className="btn btn-primary w-full"
          >
            <RefreshCw size={17} />
            Try Again
          </button>

          <Link
            href={APP_ROUTES.HOME}
            className="btn btn-ghost w-full border border-base-300"
          >
            <Home size={17} />
            Go Back Home
          </Link>
        </div>
      </div>
    </div>
  );
}
