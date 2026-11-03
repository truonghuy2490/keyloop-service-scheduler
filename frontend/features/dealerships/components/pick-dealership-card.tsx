
"use client";

import {
  Building2,
  MapPin,
  Phone,
  Mail,
  CalendarPlus,
  ArrowRight,
} from "lucide-react";
import Link from "next/link";

import type { Dealership } from "@/features/dealerships/dealership.types";

interface PickDealershipCardProps {
  dealership: Dealership;
}

export default function PickDealershipCard({
  dealership,
}: PickDealershipCardProps) {
  const name = dealership.name?.trim() || "Unnamed Dealership";
  const address = dealership.address?.trim() || "Address not available";
  const phone = dealership.phone?.trim();
  const email = dealership.email?.trim();

  const bookingUrl = `/appointments/book/${encodeURIComponent(dealership.id)}`;

  return (
    <article className="card h-full border border-base-300 bg-base-100 shadow-sm transition-all duration-200 hover:-translate-y-1 hover:border-primary/40 hover:shadow-md">
      <div className="card-body flex h-full flex-col gap-4 p-6">
        {/* Header */}
        <div className="flex items-start justify-between gap-3">
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10 text-primary">
            <Building2 size={24} strokeWidth={2} />
          </div>

          <span className="badge badge-outline badge-primary text-xs">
            Service Center
          </span>
        </div>

        {/* Dealership Name */}
        <div>
          <h3 className="text-lg font-semibold leading-snug text-base-content">
            {name}
          </h3>

          <p className="mt-1 text-xs text-base-content/50">
            Automotive Service Dealership
          </p>
        </div>

        {/* Contact Information */}
        <div className="flex flex-col gap-3 text-sm text-base-content/70">
          <div className="flex items-start gap-3">
            <MapPin
              size={17}
              className="mt-0.5 shrink-0 text-primary"
            />

            <span className="line-clamp-2">{address}</span>
          </div>

          {phone && (
            <div className="flex items-center gap-3">
              <Phone
                size={17}
                className="shrink-0 text-primary"
              />

              <span>{phone}</span>
            </div>
          )}

          {email && (
            <div className="flex min-w-0 items-center gap-3">
              <Mail
                size={17}
                className="shrink-0 text-primary"
              />

              <span className="truncate" title={email}>
                {email}
              </span>
            </div>
          )}
        </div>

        {/* Booking Action */}
        <div className="card-actions mt-auto border-t border-base-200 pt-4">
          <Link
            href={bookingUrl}
            aria-label={`Book a service appointment at ${name}`}
            className="btn btn-primary w-full gap-2"
          >
            <CalendarPlus size={18} />

            <span>Book Service</span>

            <ArrowRight size={17} className="ml-auto" />
          </Link>
        </div>
      </div>
    </article>
  );
}
