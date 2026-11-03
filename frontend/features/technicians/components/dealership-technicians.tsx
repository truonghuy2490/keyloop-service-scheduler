"use client";

import { useMemo } from "react";
import { useRouter } from "next/navigation";
import { Users, CheckCircle2, XCircle } from "lucide-react";
import { useGetDealershipTechniciansQuery } from "@/features/technicians/technician.api";

interface DealershipTechniciansProps {
  dealershipId: string;
  serviceTypeId: string | null;
  serviceTypeName?: string;
}

export default function DealershipTechnicians({
  dealershipId,
  serviceTypeId,
  serviceTypeName,
}: DealershipTechniciansProps) {
  const router = useRouter();
  const { data, error, isLoading, refetch } =
    useGetDealershipTechniciansQuery(dealershipId);

  const technicians = useMemo(() => {
    if (!data || !serviceTypeId) return [];
    return data.items
      .filter((technician) =>
        technician.qualifications.some(
          (qualification) => qualification.serviceTypeId === serviceTypeId,
        ),
      )
      .sort((a, b) => a.fullName.localeCompare(b.fullName));
  }, [data, serviceTypeId]);

  if (!serviceTypeId) {
    return (
      <section className="rounded-xl border border-base-300 bg-base-100 p-6 text-center shadow-sm">
        <Users size={32} className="mx-auto mb-3 text-base-content/40" />
        <h2 className="font-semibold">Qualified Technicians</h2>
        <p className="mt-1 text-sm text-base-content/60">
          Select a service above to view qualified technicians.
        </p>
      </section>
    );
  }

  return (
    <section className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
      <div className="flex flex-col gap-3 border-b border-base-300 p-5 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex items-center gap-3">
          <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-primary/10 text-primary">
            <Users size={21} />
          </div>
          <div>
            <h2 className="font-semibold">Qualified Technicians</h2>
            <p className="text-sm text-base-content/60">
              {serviceTypeName
                ? `Technicians qualified for ${serviceTypeName}`
                : "Technicians qualified for the selected service"}
            </p>
          </div>
        </div>
        {!isLoading && !error && data && (
          <span className="badge badge-outline">Total: {technicians.length}</span>
        )}
      </div>

      <div className="p-5">
        {isLoading ? (
          <div className="flex min-h-40 items-center justify-center">
            <span className="loading loading-spinner loading-lg text-primary" />
          </div>
        ) : error ? (
          <div className="flex flex-wrap items-center justify-between gap-3">
            <p className="text-sm text-error">Failed to load technicians.</p>
            <button type="button" className="btn btn-outline btn-error btn-sm" onClick={() => refetch()}>
              Retry
            </button>
          </div>
        ) : technicians.length === 0 ? (
          <div className="py-10 text-center">
            <Users size={34} className="mx-auto mb-3 text-base-content/30" />
            <h3 className="font-semibold">No matching technicians</h3>
            <p className="mt-1 text-sm text-base-content/60">
              No technicians are qualified for this service.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {technicians.map((technician) => (
              <button
                key={technician.id}
                type="button"
                onClick={() => router.push(`/technicians/${encodeURIComponent(technician.id)}`)}
                aria-label={`View details for ${technician.fullName}`}
                className="rounded-xl border border-base-300 bg-base-100 p-5 text-left transition-all hover:-translate-y-0.5 hover:border-primary/50 hover:shadow-md focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary"
              >
                <div className="flex items-start justify-between gap-3">
                  <div className="flex h-11 w-11 items-center justify-center rounded-full bg-primary/10 text-lg font-semibold text-primary">
                    {technician.fullName.trim().charAt(0).toUpperCase()}
                  </div>
                  <span className={`badge gap-1 ${technician.active ? "badge-success" : "badge-error"}`}>
                    {technician.active ? <CheckCircle2 size={13} /> : <XCircle size={13} />}
                    {technician.active ? "Active" : "Inactive"}
                  </span>
                </div>
                <h3 className="mt-4 font-semibold text-base-content">{technician.fullName}</h3>
                <p className="mt-1 truncate text-xs text-base-content/50">ID: {technician.id}</p>
                <p className="mt-4 text-sm font-medium text-primary">View technician details</p>
              </button>
            ))}
          </div>
        )}
      </div>
    </section>
  );
}
