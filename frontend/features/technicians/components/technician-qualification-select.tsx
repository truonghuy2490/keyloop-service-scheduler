"use client";

import { useState } from "react";
import { BadgeCheck, Clock3, Wrench } from "lucide-react";

export interface TechnicianQualification {
  serviceTypeId: string;
  name: string;
  description: string | null;
  durationMinutes: number | null;
  qualifiedAt: string | null;
}

interface TechnicianQualificationSelectProps {
  qualifications: TechnicianQualification[];
}

function formatDate(value: string | null): string {
  if (!value) return "Not specified";
  const date = new Date(value);
  return Number.isNaN(date.getTime())
    ? "Not specified"
    : new Intl.DateTimeFormat("en-US", { dateStyle: "medium", timeZone: "UTC" }).format(date);
}

export default function TechnicianQualificationSelect({ qualifications }: TechnicianQualificationSelectProps) {
  const [selectedId, setSelectedId] = useState("");
  const selected = qualifications.find((q) => q.serviceTypeId === selectedId);

  return (
    <section className="rounded-xl border border-base-300 bg-base-100 p-5 shadow-sm sm:p-6">
      <div className="mb-5 flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10 text-primary"><Wrench size={21} /></div>
        <div>
          <h2 className="font-semibold">Service Qualifications</h2>
          <p className="text-sm text-base-content/60">Select a service to view qualification details</p>
        </div>
      </div>
      {qualifications.length === 0 ? (
        <p className="rounded-lg bg-base-200 p-4 text-sm text-base-content/60">No service qualifications found.</p>
      ) : (
        <div className="space-y-4">
          <label className="form-control block w-full">
            <span className="mb-2 block text-sm font-medium">Service type</span>
            <select className="select select-bordered w-full" value={selectedId} onChange={(e) => setSelectedId(e.target.value)}>
              <option value="">Select a service</option>
              {[...qualifications].sort((a, b) => a.name.localeCompare(b.name)).map((q) => (
                <option key={q.serviceTypeId} value={q.serviceTypeId}>{q.name}</option>
              ))}
            </select>
          </label>
          {selected ? (
            <div className="rounded-xl border border-primary/20 bg-primary/5 p-4">
              <div className="flex flex-wrap items-center justify-between gap-2">
                <h3 className="font-semibold">{selected.name}</h3>
                <span className="badge badge-success gap-1"><BadgeCheck size={14} /> Qualified</span>
              </div>
              <p className="mt-2 text-sm text-base-content/70">{selected.description || "No description available."}</p>
              <div className="mt-4 flex flex-wrap gap-4 border-t border-base-300 pt-3 text-sm text-base-content/70">
                <span className="inline-flex items-center gap-2"><Clock3 size={15} />{selected.durationMinutes != null ? `${selected.durationMinutes} minutes` : "Duration not specified"}</span>
                <span>Qualified: {formatDate(selected.qualifiedAt)}</span>
              </div>
            </div>
          ) : (
            <p className="rounded-lg bg-base-200/60 p-4 text-sm text-base-content/60">Choose a service above to see its details.</p>
          )}
        </div>
      )}
    </section>
  );
}
