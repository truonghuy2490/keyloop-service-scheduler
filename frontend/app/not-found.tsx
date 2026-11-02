
"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { ArrowLeft, Home, SearchX } from "lucide-react";

import { APP_ROUTES } from "@/lib/api/routes";

export default function NotFoundPage() {
  const router = useRouter();

  return (
    <div className="flex min-h-[70vh] w-full items-center justify-center px-6 py-12">
      <div className="relative w-full max-w-md overflow-hidden rounded-2xl border border-base-300 bg-base-100 p-8 text-center shadow-lg">
        <div className="absolute inset-x-0 top-0 h-1 bg-primary" />

        <div className="mb-6 flex justify-center">
          <div className="rounded-full bg-primary/10 p-4 text-primary">
            <SearchX size={56} strokeWidth={1.8} />
          </div>
        </div>

        <h1 className="text-6xl font-black tracking-tight text-primary">
          404
        </h1>

        <h2 className="mt-4 text-xl font-bold text-base-content">
          Page Not Found
        </h2>

        <p className="mt-2 text-sm leading-relaxed text-base-content/60">
          Sorry, the page you are looking for does not exist
          or may have been moved.
        </p>

        <div className="mt-8 flex flex-col gap-3">
          <Link
            href={APP_ROUTES.HOME}
            className="btn btn-primary w-full"
          >
            <Home size={17} />
            Return to Home
          </Link>

          <button
            type="button"
            onClick={() => router.back()}
            className="btn btn-ghost w-full border border-base-300"
          >
            <ArrowLeft size={17} />
            Go Back
          </button>
        </div>
      </div>
    </div>
  );
}
