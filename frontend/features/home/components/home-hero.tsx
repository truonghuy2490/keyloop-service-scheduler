"use client";

import Link from "next/link";
import { CalendarPlus } from "lucide-react";

interface HomeHeroProps {
  customerName?: string;
}

export default function HomeHero({
  customerName,
}: HomeHeroProps) {
  return (
    <section className="rounded-2xl border border-base-300 bg-base-100 p-6 shadow-sm md:p-8">
      <div className="flex flex-col justify-between gap-6 md:flex-row md:items-center">
        <div>
          <h1 className="text-2xl font-bold md:text-3xl">
            Welcome back{customerName ? `, ${customerName}` : ""}!
          </h1>

          <p className="mt-2 text-sm text-base-content/60">
            Ready for your next service? Book an appointment
            at your preferred dealership.
          </p>
        </div>

        <Link
          href="/appointments/book"
          className="btn btn-primary"
        >
          <CalendarPlus size={18} />
          Book a Service
        </Link>
      </div>
    </section>
  );
}
