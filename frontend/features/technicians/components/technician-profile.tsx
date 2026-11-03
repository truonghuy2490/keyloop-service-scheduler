"use client";

import Link from "next/link";
import { Building2, UserRound, Wrench } from "lucide-react";

interface TechnicianProfileProps {
  id: string;
  fullName: string;
  active: boolean;
  dealership: { id?: string; name: string };
  qualificationCount: number;
}

export default function TechnicianProfile({
  id,
  fullName,
  active,
  dealership,
  qualificationCount,
}: TechnicianProfileProps) {
  return (
    <section className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
      <div className="bg-primary px-6 py-8 text-primary-content sm:px-8">
        <div className="flex flex-col gap-5 sm:flex-row sm:items-center sm:justify-between">
          <div className="flex items-center gap-4">
            <div className="flex h-16 w-16 shrink-0 items-center justify-center rounded-2xl bg-primary-content/15">
              <UserRound size={32} />
            </div>
            <div className="min-w-0">
              <p className="text-sm text-primary-content/70">Technician Profile</p>
              <h1 className="text-2xl font-bold sm:text-3xl">{fullName}</h1>
              <p className="mt-1 break-all text-xs text-primary-content/70">ID: {id}</p>
            </div>
          </div>
          <span className={`badge badge-lg ${active ? "badge-success" : "badge-ghost"}`}>
            {active ? "Active" : "Inactive"}
          </span>
        </div>
      </div>
      <div className="grid gap-5 p-5 sm:grid-cols-2">
        <div className="flex items-center gap-3">
          <Building2 size={20} className="shrink-0 text-primary" />
          <div>
            <p className="text-xs text-base-content/50">Dealership</p>
            {dealership.id ? (
              <Link className="font-medium text-primary hover:underline" href={`/dealerships/${encodeURIComponent(dealership.id)}`}>
                {dealership.name}
              </Link>
            ) : (
              <p className="font-medium">{dealership.name}</p>
            )}
          </div>
        </div>
        <div className="flex items-center gap-3">
          <Wrench size={20} className="shrink-0 text-primary" />
          <div>
            <p className="text-xs text-base-content/50">Qualifications</p>
            <p className="font-medium">{qualificationCount} service types</p>
          </div>
        </div>
      </div>
    </section>
  );
}
