"use client";

import { Warehouse, CheckCircle2, XCircle } from "lucide-react";

import { useGetServiceBaysQuery } from "@/features/service-bay/servicebay.api";
import type { ServiceBay } from "@/features/service-bay/servicebay.types";

interface DealershipServiceBaysProps {
  dealershipId: string;
}

function ServiceBayCard({ serviceBay }: { serviceBay: ServiceBay }) {
  return (
    <div className="rounded-xl border border-base-300 bg-base-100 p-5 transition-colors hover:border-primary/30">
      <div className="flex items-start justify-between gap-3">
        <div className="flex h-11 w-11 items-center justify-center rounded-lg bg-primary/10 text-primary">
          <Warehouse size={21} />
        </div>

        <div
          className={`badge gap-1 ${
            serviceBay.active ? "badge-success" : "badge-error"
          }`}
        >
          {serviceBay.active ? (
            <CheckCircle2 size={13} />
          ) : (
            <XCircle size={13} />
          )}

          {serviceBay.active ? "Active" : "Inactive"}
        </div>
      </div>

      <div className="mt-4">
        <p className="text-xs font-medium uppercase tracking-wide text-base-content/50">
          Service Bay
        </p>

        <h3 className="mt-1 text-xl font-bold text-base-content">
          {serviceBay.bayCode}
        </h3>

        <p className="mt-2 truncate text-xs text-base-content/50">
          ID: {serviceBay.id}
        </p>
      </div>
    </div>
  );
}

export default function DealershipServiceBays({
  dealershipId,
}: DealershipServiceBaysProps) {
  const {
    data,
    error,
    isLoading,
    refetch,
  } = useGetServiceBaysQuery({
    dealershipId,
    pageNumber: 1,
    pageSize: 100,
  });

  const serviceBays = data?.items ?? [];

  const activeCount = serviceBays.filter((bay) => bay.active).length;
  const inactiveCount = serviceBays.length - activeCount;

  if (isLoading) {
    return (
      <div className="flex min-h-40 items-center justify-center rounded-xl border border-base-300 bg-base-100">
        <span className="loading loading-spinner loading-lg text-primary" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-xl border border-error/30 bg-base-100 p-6">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h3 className="font-semibold text-error">
              Failed to load service bays
            </h3>

            <p className="mt-1 text-sm text-base-content/60">
              Please check your connection and try again.
            </p>
          </div>

          <button
            className="btn btn-outline btn-error btn-sm"
            onClick={() => refetch()}
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <section className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
      {/* Header */}
      <div className="flex flex-col gap-4 border-b border-base-300 p-5 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex items-center gap-3">
          <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary">
            <Warehouse size={21} />
          </div>

          <div>
            <h2 className="font-semibold text-base-content">
              Service Bays
            </h2>

            <p className="text-sm text-base-content/60">
              Service bays available at this dealership
            </p>
          </div>
        </div>

        <div className="flex flex-wrap gap-2">
          <span className="badge badge-outline">
            Total: {serviceBays.length}
          </span>

          <span className="badge badge-success badge-outline">
            Active: {activeCount}
          </span>

          <span className="badge badge-error badge-outline">
            Inactive: {inactiveCount}
          </span>
        </div>
      </div>

      {/* Content */}
      <div className="p-5">
        {serviceBays.length === 0 ? (
          <div className="rounded-xl border border-dashed border-base-300 py-12 text-center">
            <Warehouse
              size={36}
              className="mx-auto mb-3 text-base-content/30"
            />

            <h3 className="font-semibold text-base-content">
              No service bays found
            </h3>

            <p className="mt-1 text-sm text-base-content/60">
              This dealership does not have any service bays yet.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {serviceBays.map((serviceBay) => (
              <ServiceBayCard
                key={serviceBay.id}
                serviceBay={serviceBay}
              />
            ))}
          </div>
        )}
      </div>
    </section>
  );
}
