
import Link from "next/link";
import {
  ArrowRight,
  Building2,
  RefreshCw,
} from "lucide-react";

import type { Dealership } from "@/features/dealerships/dealership.types";
import DealershipCard from "@/features/dealerships/components/dealership-card";

interface ExploreDealershipsProps {
  dealerships: Dealership[];
  isLoading?: boolean;
  isError?: boolean;
  onRetry?: () => void;
}

export default function ExploreDealerships({
  dealerships,
  isLoading = false,
  isError = false,
  onRetry,
}: ExploreDealershipsProps) {
  const featuredDealerships = dealerships.slice(0, 3);

  return (
    <section className="space-y-5">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="text-xl font-bold text-base-content">
            Explore Dealerships
          </h2>

          <p className="mt-1 text-sm text-base-content/60">
            Discover dealership locations and find the right
            service center for your vehicle.
          </p>
        </div>

        <Link
          href="/dealerships"
          className="btn btn-ghost btn-sm gap-2"
        >
          View all dealerships
          <ArrowRight size={16} />
        </Link>
      </div>

      {/* Loading */}
      {isLoading ? (
        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 3 }).map((_, index) => (
            <div
              key={index}
              className="rounded-xl border border-base-300 bg-base-100 p-6"
            >
              <div className="skeleton mb-5 size-12 rounded-xl" />
              <div className="skeleton mb-4 h-6 w-3/4" />
              <div className="skeleton mb-3 h-4 w-full" />
              <div className="skeleton mb-3 h-4 w-2/3" />
              <div className="skeleton mb-6 h-4 w-4/5" />
              <div className="skeleton h-9 w-full" />
            </div>
          ))}
        </div>
      ) : isError ? (
        /* Error */
        <div className="flex flex-col items-center justify-center rounded-xl border border-error/30 bg-base-100 p-8 text-center">
          <Building2
            size={32}
            className="text-error/60"
          />

          <h3 className="mt-4 font-semibold text-error">
            Unable to load dealerships
          </h3>

          <p className="mt-2 text-sm text-base-content/60">
            Something went wrong while retrieving dealerships.
          </p>

          {onRetry && (
            <button
              type="button"
              className="btn btn-outline btn-error btn-sm mt-5"
              onClick={onRetry}
            >
              <RefreshCw size={15} />
              Retry
            </button>
          )}
        </div>
      ) : featuredDealerships.length === 0 ? (
        /* Empty */
        <div className="flex flex-col items-center justify-center rounded-xl border border-dashed border-base-300 bg-base-100 p-8 text-center">
          <Building2
            size={36}
            className="text-base-content/30"
          />

          <h3 className="mt-4 font-semibold">
            No dealerships available
          </h3>

          <p className="mt-2 text-sm text-base-content/60">
            There are currently no dealerships to explore.
          </p>
        </div>
      ) : (
        /* Dealership Cards */
        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {featuredDealerships.map((dealership) => (
            <DealershipCard
              key={dealership.id}
              dealership={dealership}
            />
          ))}
        </div>
      )}
    </section>
  );
}
