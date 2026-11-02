"use client";

import { useMemo, useState } from "react";
import {
  Users,
  Wrench,
  CheckCircle2,
  XCircle,
  Filter,
} from "lucide-react";

import { useGetDealershipTechniciansQuery } from "@/features/technicians/technician.api";

interface DealershipTechniciansProps {
  dealershipId: string;
}

export default function DealershipTechnicians({
  dealershipId,
}: DealershipTechniciansProps) {
  const [selectedServiceTypeId, setSelectedServiceTypeId] =
    useState("all");

  const {
    data,
    error,
    isLoading,
    refetch,
  } = useGetDealershipTechniciansQuery(dealershipId);

  // Extract unique service types from technician qualifications.
  const serviceTypes = useMemo(() => {
    if (!data) return [];

    const serviceTypeMap = new Map<
      string,
      { id: string; name: string }
    >();

    data.items.forEach((technician) => {
      technician.qualifications.forEach((qualification) => {
        serviceTypeMap.set(qualification.serviceTypeId, {
          id: qualification.serviceTypeId,
          name: qualification.serviceName,
        });
      });
    });

    return Array.from(serviceTypeMap.values()).sort((a, b) =>
      a.name.localeCompare(b.name),
    );
  }, [data]);

  // Filter technicians by selected service type.
  const filteredTechnicians = useMemo(() => {
    if (!data) return [];

    const technicians =
      selectedServiceTypeId === "all"
        ? [...data.items]
        : data.items.filter((technician) =>
            technician.qualifications.some(
              (qualification) =>
                qualification.serviceTypeId === selectedServiceTypeId,
            ),
          );

    return technicians.sort((a, b) =>
      a.fullName.localeCompare(b.fullName),
    );
  }, [data, selectedServiceTypeId]);

  const activeCount = data?.items.filter(
    (technician) => technician.active,
  ).length ?? 0;

  const inactiveCount = (data?.totalItems ?? 0) - activeCount;

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
              Failed to load technicians
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

  if (!data) return null;

  return (
    <section className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
      {/* Header */}
      <div className="flex flex-col gap-4 border-b border-base-300 p-5 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex items-center gap-3">
          <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary">
            <Users size={21} />
          </div>

          <div>
            <h2 className="font-semibold text-base-content">
              Technicians
            </h2>

            <p className="text-sm text-base-content/60">
              Technicians working at {data.dealershipName}
            </p>
          </div>
        </div>

        <div className="flex flex-wrap gap-2">
          <span className="badge badge-outline">
            Total: {data.totalItems}
          </span>

          <span className="badge badge-success badge-outline">
            Active: {activeCount}
          </span>

          <span className="badge badge-error badge-outline">
            Inactive: {inactiveCount}
          </span>
        </div>
      </div>

      {/* Filter */}
      <div className="flex flex-col gap-3 border-b border-base-300 p-5 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="font-medium text-base-content">
            Technician list
          </p>

          <p className="text-sm text-base-content/60">
            Showing {filteredTechnicians.length} of {data.totalItems} technicians
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Filter size={17} className="shrink-0 text-base-content/60" />

          <select
            className="select select-bordered select-sm w-full sm:w-56"
            value={selectedServiceTypeId}
            onChange={(event) =>
              setSelectedServiceTypeId(event.target.value)
            }
          >
            <option value="all">All service types</option>

            {serviceTypes.map((serviceType) => (
              <option key={serviceType.id} value={serviceType.id}>
                {serviceType.name}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Technician list */}
      <div className="p-5">
        {data.totalItems === 0 ? (
          <div className="rounded-xl border border-dashed border-base-300 py-12 text-center">
            <Users
              size={36}
              className="mx-auto mb-3 text-base-content/30"
            />

            <h3 className="font-semibold text-base-content">
              No technicians found
            </h3>

            <p className="mt-1 text-sm text-base-content/60">
              This dealership does not have any technicians yet.
            </p>
          </div>
        ) : filteredTechnicians.length === 0 ? (
          <div className="rounded-xl border border-dashed border-base-300 py-12 text-center">
            <Wrench
              size={36}
              className="mx-auto mb-3 text-base-content/30"
            />

            <h3 className="font-semibold text-base-content">
              No matching technicians
            </h3>

            <p className="mt-1 text-sm text-base-content/60">
              No technicians are qualified for this service type.
            </p>

            <button
              className="btn btn-ghost btn-sm mt-3"
              onClick={() => setSelectedServiceTypeId("all")}
            >
              Clear filter
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {filteredTechnicians.map((technician) => (
              <div
                key={technician.id}
                className="rounded-xl border border-base-300 bg-base-100 p-5 transition-colors hover:border-primary/30"
              >
                {/* Technician information */}
                <div className="flex items-start justify-between gap-3">
                  <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-primary/10 text-lg font-semibold text-primary">
                    {technician.fullName
                      .trim()
                      .charAt(0)
                      .toUpperCase()}
                  </div>

                  <span
                    className={`badge gap-1 ${
                      technician.active
                        ? "badge-success"
                        : "badge-error"
                    }`}
                  >
                    {technician.active ? (
                      <CheckCircle2 size={13} />
                    ) : (
                      <XCircle size={13} />
                    )}

                    {technician.active ? "Active" : "Inactive"}
                  </span>
                </div>

                <div className="mt-4">
                  <h3 className="font-semibold text-base-content">
                    {technician.fullName}
                  </h3>

                  <p className="mt-1 truncate text-xs text-base-content/50">
                    ID: {technician.id}
                  </p>
                </div>

                {/* Qualifications */}
                <div className="mt-4 border-t border-base-300 pt-4">
                  <div className="mb-3 flex items-center justify-between gap-2">
                    <div className="flex items-center gap-2 text-sm font-medium text-base-content">
                      <Wrench size={15} className="text-primary" />
                      Qualifications
                    </div>

                    <span className="text-xs text-base-content/50">
                      {technician.qualifications.length} service(s)
                    </span>
                  </div>

                  {technician.qualifications.length === 0 ? (
                    <p className="text-xs text-base-content/50">
                      No qualifications assigned
                    </p>
                  ) : (
                    <div className="flex flex-wrap gap-2">
                      {technician.qualifications.map((qualification) => (
                        <span
                          key={qualification.serviceTypeId}
                          className="badge badge-outline badge-primary"
                          title={qualification.serviceTypeId}
                        >
                          {qualification.serviceName}
                        </span>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </section>
  );
}
