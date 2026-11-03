import Link from "next/link";
import {
  CalendarDays,
  CarFront,
  MapPin,
  ArrowRight,
} from "lucide-react";

import type {
  CustomerAppointment,
} from "@/features/appointments/appointment.types";

interface Props {
  appointment?: CustomerAppointment;
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat("en-GB", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(value));
}

export default function NextAppointmentCard({
  appointment,
}: Props) {
  return (
    <section className="rounded-2xl border border-base-300 bg-base-100 p-6 shadow-sm">
      <h2 className="text-lg font-bold">
        Next Appointment
      </h2>

      {!appointment ? (
        <div className="mt-8 flex flex-col items-center text-center">
          <CalendarDays
            size={40}
            className="text-base-content/30"
          />

          <h3 className="mt-4 font-semibold">
            Your next service starts here
          </h3>

          <p className="mt-2 text-sm text-base-content/60">
            Choose a dealership and find a time
            that works for you.
          </p>

          <Link
            href="/appointments/book"
            className="btn btn-primary btn-sm mt-5"
          >
            Book a Service
          </Link>
        </div>
      ) : (
        <div className="mt-5 space-y-4">
          <div className="flex flex-wrap items-center gap-3">
            <h3 className="text-xl font-semibold">
              {appointment.service.name}
            </h3>

            <span className="badge badge-success badge-outline">
              {appointment.status}
            </span>
          </div>

          <div className="space-y-3 text-sm">
            <div className="flex items-start gap-3">
              <CalendarDays size={18} className="text-primary" />
              <span>
                {formatDate(appointment.schedule.startTime)}
                {" – "}
                {formatDate(appointment.schedule.endTime)}
              </span>
            </div>

            <div className="flex items-center gap-3">
              <MapPin size={18} className="text-primary" />
              <span>{appointment.dealership.name}</span>
            </div>

            <div className="flex items-center gap-3">
              <CarFront size={18} className="text-primary" />
              <span>
                {appointment.vehicle.make}{" "}
                {appointment.vehicle.model}
                {" · "}
                {appointment.vehicle.registrationNumber}
              </span>
            </div>
          </div>

          <div className="flex flex-wrap gap-3 border-t border-base-300 pt-4">
            <Link
              href="/appointments"
              className="btn btn-primary btn-sm"
            >
              View Appointment
              <ArrowRight size={16} />
            </Link>

            <Link
              href="/appointments"
              className="btn btn-ghost btn-sm"
            >
              View all appointments
            </Link>
          </div>
        </div>
      )}
    </section>
  );
}
