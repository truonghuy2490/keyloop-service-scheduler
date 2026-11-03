
"use client";

import { MapPin, Phone, Mail, ArrowRight } from "lucide-react";
import Link from "next/link";
import type { Dealership } from "@/features/dealerships/dealership.types";

interface DealershipCardProps {
  dealership: Dealership;
}

export default function DealershipCard({
  dealership,
}: DealershipCardProps) {
  const name = dealership.name?.trim() || "Unnamed Dealership";
  const address = dealership.address?.trim() || "Address not available";
  const phone = dealership.phone?.trim() || "No phone number";
  const email = dealership.email?.trim() || "No email available";

  return (
    <article className="card h-full border border-base-300 bg-base-100 shadow-sm transition-all duration-200 hover:-translate-y-1 hover:border-primary/30 hover:shadow-md">
      <div className="card-body gap-4 p-6">
        {/* Card header */}
        <div className="flex items-start justify-between">
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10 text-primary">
            <MapPin size={24} strokeWidth={2} />
          </div>

          {/* <span className="badge badge-success badge-outline px-3 py-3 text-xs font-medium">
            Active
          </span> */}
        </div>

        {/* Dealership name */}
        <h2 className="card-title min-h-7 text-lg font-semibold text-base-content">
          {name}
        </h2>

        {/* Contact information */}
        <div className="space-y-3 text-sm text-base-content/70">
          <p className="flex items-start gap-2.5">
            <MapPin
              size={17}
              className="mt-0.5 shrink-0 text-neutral"
            />
            <span>{address}</span>
          </p>

          <p className="flex items-center gap-2.5">
            <Phone size={17} className="shrink-0 text-neutral" />
            <span>{phone}</span>
          </p>

          <p className="flex min-w-0 items-center gap-2.5">
            <Mail size={17} className="shrink-0 text-neutral" />
            <span className="truncate">{email}</span>
          </p>
        </div>

        {/* Action */}
        <div className="card-actions mt-auto pt-2">
          <Link
            href={`/dealerships/${dealership.id}`}
            className="btn btn-outline btn-primary btn-sm w-full gap-2 transition-colors hover:bg-primary hover:text-primary-content"
          >
            View details
            <ArrowRight size={16} />
          </Link>
        </div>
      </div>
    </article>
  );
}
