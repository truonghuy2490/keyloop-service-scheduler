
"use client";

import {
  CalendarClock,
  Clock3,
  Wrench,
  Building2,
  UserRound,
  CalendarDays,
  History,
  BadgeCheck,
  CircleAlert,
} from "lucide-react";

import { useGetTechnicianDetailsQuery } from "@/features/technicians/technician.api";
import TechnicianCalendar from "./technician-calendar";

interface TechnicianDetailsProps {
  technicianId: string;
}

function formatDate(value: string | null | undefined): string {
  if (!value) return "—";

  return new Intl.DateTimeFormat("en-US", {
    dateStyle: "medium",
  }).format(new Date(value));
}

function formatDateTime(value: string | null | undefined): string {
  if (!value) return "—";

  return new Intl.DateTimeFormat("en-US", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));
}

function formatTime(value: string | null | undefined): string {
  return value ? value.slice(0, 5) : "—";
}

function formatDayOfWeek(day: string): string {
  return day.charAt(0) + day.slice(1).toLowerCase();
}

function SectionHeader({
  icon,
  title,
  description,
}: {
  icon: React.ReactNode;
  title: string;
  description?: string;
}) {
  return (
    <div className="mb-5 flex items-center gap-3">
      <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary">
        {icon}
      </div>

      <div>
        <h2 className="font-semibold text-base-content">{title}</h2>
        {description && (
          <p className="text-sm text-base-content/60">
            {description}
          </p>
        )}
      </div>
    </div>
  );
}

export default function TechnicianDetails({
  technicianId,
}: TechnicianDetailsProps) {
  const {
    data,
    isLoading,
    isError,
    refetch,
  } = useGetTechnicianDetailsQuery(technicianId);

  if (isLoading) {
    return (
      <div className="flex min-h-80 items-center justify-center">
        <span className="loading loading-spinner loading-lg text-primary" />
      </div>
    );
  }

  if (isError || !data) {
    return (
      <div className="rounded-xl border border-error/30 bg-base-100 p-8 text-center">
        <CircleAlert
          size={32}
          className="mx-auto mb-3 text-error"
        />

        <h2 className="font-semibold text-base-content">
          Unable to load technician details
        </h2>

        <p className="mt-1 text-sm text-base-content/60">
          Something went wrong while retrieving technician information.
        </p>

        <button
          type="button"
          className="btn btn-primary btn-sm mt-4"
          onClick={() => refetch()}
        >
          Try again
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Technician Profile */}
      <section className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
        <div className="bg-primary px-6 py-8 text-primary-content sm:px-8">
          <div className="flex flex-col justify-between gap-5 sm:flex-row sm:items-center">
            <div className="flex items-center gap-4">
              <div className="flex h-16 w-16 shrink-0 items-center justify-center rounded-2xl bg-primary-content/15">
                <UserRound size={32} />
              </div>

              <div>
                <p className="text-sm text-primary-content/70">
                  Technician Profile
                </p>

                <h1 className="text-2xl font-bold sm:text-3xl">
                  {data.fullName}
                </h1>

                <p className="mt-1 text-sm text-primary-content/70">
                  ID: {data.id}
                </p>
              </div>
            </div>

            <span
              className={`badge badge-lg ${
                data.active
                  ? "badge-success"
                  : "badge-ghost"
              }`}
            >
              {data.active ? "Active" : "Inactive"}
            </span>
          </div>
        </div>

        <div className="grid gap-4 p-5 sm:grid-cols-2">
          <div className="flex items-center gap-3">
            <Building2
              size={20}
              className="shrink-0 text-primary"
            />

            <div>
              <p className="text-xs text-base-content/50">
                Dealership
              </p>
              <p className="font-medium text-base-content">
                {data.dealership.name}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <Wrench
              size={20}
              className="shrink-0 text-primary"
            />

            <div>
              <p className="text-xs text-base-content/50">
                Total Qualifications
              </p>
              <p className="font-medium text-base-content">
                {data.qualifications.length} service types
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Service Qualifications */}
      <section className="rounded-xl border border-base-300 bg-base-100 p-5 shadow-sm sm:p-6">
        <SectionHeader
          icon={<Wrench size={21} />}
          title="Service Qualifications"
          description="Services this technician is qualified to perform"
        />

        {data.qualifications.length === 0 ? (
          <p className="rounded-lg bg-base-200 p-4 text-sm text-base-content/60">
            No service qualifications found.
          </p>
        ) : (
          <div className="grid gap-3 md:grid-cols-2">
            {data.qualifications.map((qualification) => (
              <div
                key={qualification.serviceTypeId}
                className="rounded-lg border border-base-300 p-4"
              >
                <div className="flex items-start gap-3">
                  <div className="mt-0.5 rounded-lg bg-secondary/15 p-2 text-secondary">
                    <BadgeCheck size={18} />
                  </div>

                  <div className="min-w-0 flex-1">
                    <h3 className="font-semibold text-base-content">
                      {qualification.name}
                    </h3>

                    <p className="mt-1 text-sm text-base-content/60">
                      {qualification.description || "No description available"}
                    </p>

                    <div className="mt-3 flex flex-wrap items-center gap-3 text-xs text-base-content/60">
                      <span>
                        Duration:{" "}
                        {qualification.durationMinutes != null
                          ? `${qualification.durationMinutes} mins`
                          : "—"}
                      </span>

                      <span>
                        Qualified: {formatDate(qualification.qualifiedAt)}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* Weekly Working Hours */}
      <section className="rounded-xl border border-base-300 bg-base-100 p-5 shadow-sm sm:p-6">
        <SectionHeader
          icon={<Clock3 size={21} />}
          title="Weekly Working Hours"
          description="Regular working schedule for each day of the week"
        />

        {data.workingHours.length === 0 ? (
          <p className="rounded-lg bg-base-200 p-4 text-sm text-base-content/60">
            No weekly working hours configured.
          </p>
        ) : (
          <div className="divide-y divide-base-300">
            {[
              "MONDAY",
              "TUESDAY",
              "WEDNESDAY",
              "THURSDAY",
              "FRIDAY",
              "SATURDAY",
              "SUNDAY",
            ].map((day) => {
              const schedule = data.workingHours.find(
                (item) => item.dayOfWeek === day,
              );

              return (
                <div
                  key={day}
                  className="flex flex-col gap-2 py-3 sm:flex-row sm:items-center sm:justify-between"
                >
                  <span className="w-32 font-medium text-base-content">
                    {formatDayOfWeek(day)}
                  </span>

                  {!schedule || schedule.intervals.length === 0 ? (
                    <span className="text-sm text-base-content/40">
                      Not scheduled
                    </span>
                  ) : (
                    <div className="flex flex-wrap gap-2">
                      {schedule.intervals.map((interval, index) => (
                        <span
                          key={`${day}-${index}`}
                          className="badge badge-outline badge-secondary gap-1 py-3"
                        >
                          <Clock3 size={13} />
                          {formatTime(interval.startsAt)} -{" "}
                          {formatTime(interval.endsAt)}
                        </span>
                      ))}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </section>

      {/* Monthly Working Calendar */}
      <TechnicianCalendar
        workingHours={data.workingHours}
        dateOverrides={data.dateOverrides}
      />

      {/* Date Overrides */}
      <section className="rounded-xl border border-base-300 bg-base-100 p-5 shadow-sm sm:p-6">
        <SectionHeader
          icon={<CalendarDays size={21} />}
          title="Date Overrides"
          description="Specific availability changes that override the weekly schedule"
        />

        {data.dateOverrides.length === 0 ? (
          <p className="rounded-lg bg-base-200 p-4 text-sm text-base-content/60">
            No date overrides configured.
          </p>
        ) : (
          <div className="space-y-3">
            {data.dateOverrides
              .slice()
              .sort((a, b) => a.workDate.localeCompare(b.workDate))
              .map((override) => (
                <div
                  key={override.workDate}
                  className="flex flex-col gap-3 rounded-lg border border-base-300 p-4 sm:flex-row sm:items-center sm:justify-between"
                >
                  <div>
                    <p className="font-semibold text-base-content">
                      {formatDate(override.workDate)}
                    </p>

                    <p className="mt-1 text-sm text-base-content/60">
                      {override.reason || "No reason provided"}
                    </p>
                  </div>

                  <div className="flex flex-wrap items-center gap-2">
                    {override.isUnavailable ? (
                      <span className="badge badge-warning">
                        Day off
                      </span>
                    ) : (
                      <span className="badge badge-secondary">
                        Special hours
                      </span>
                    )}

                    {!override.isUnavailable && (
                      <span className="text-sm font-medium text-base-content">
                        {formatTime(override.startsAt)} -{" "}
                        {formatTime(override.endsAt)}
                      </span>
                    )}
                  </div>
                </div>
              ))}
          </div>
        )}
      </section>

      {/* Audit Information */}
      <section className="rounded-xl border border-base-300 bg-base-100 p-5 shadow-sm sm:p-6">
        <SectionHeader
          icon={<History size={21} />}
          title="Record Information"
          description="Technician record timestamps"
        />

        <div className="grid gap-4 sm:grid-cols-2">
          <div>
            <p className="text-xs text-base-content/50">
              Created At
            </p>
            <p className="mt-1 text-sm font-medium text-base-content">
              {formatDateTime(data.createdAt)}
            </p>
          </div>

          <div>
            <p className="text-xs text-base-content/50">
              Last Updated
            </p>
            <p className="mt-1 text-sm font-medium text-base-content">
              {formatDateTime(data.updatedAt)}
            </p>
          </div>
        </div>
      </section>
    </div>
  );
}
