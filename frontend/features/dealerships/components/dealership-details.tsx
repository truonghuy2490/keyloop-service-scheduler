"use client";

import Image from "next/image";
import type { ElementType } from "react";
import {
  MapPin,
  Phone,
  Mail,
  Clock,
  Globe,
  Building2,
  Wrench,
  Users,
  Warehouse,
  CalendarDays,
  ArrowRight,
} from "lucide-react";

import { useGetDealershipByIdQuery } from "@/features/dealerships/dealership.api";
import type {
  DayOfWeek,
  DealershipWorkingHours,
} from "@/features/dealerships/dealership.types";
import DealershipMonthCalendar from "./dealership-month-calendar";
import DealershipServiceBays from "@/features/service-bay/components/dealership-service-bays";
import DealershipTechnicians from "@/features/technicians/components/dealership-technicians";

interface DealershipDetailsProps {
  dealershipId: string;
}

// Sample information for fields not provided by the backend yet.
const SAMPLE_INFO = {
  address: "123 Nguyen Van Linh, District 7, Ho Chi Minh City, Vietnam",
  phone: "+84 28 1234 5678",
  email: "service@saigonautoservice.com",
};

const DAY_ORDER: DayOfWeek[] = [
  "MONDAY",
  "TUESDAY",
  "WEDNESDAY",
  "THURSDAY",
  "FRIDAY",
  "SATURDAY",
  "SUNDAY",
];

const DAY_LABELS: Record<DayOfWeek, string> = {
  MONDAY: "Monday",
  TUESDAY: "Tuesday",
  WEDNESDAY: "Wednesday",
  THURSDAY: "Thursday",
  FRIDAY: "Friday",
  SATURDAY: "Saturday",
  SUNDAY: "Sunday",
};

function getWorkingHoursSummary(
  workingHours: DealershipWorkingHours[],
): string {
  const openDays = workingHours.filter((item) => !item.isClosed);

  if (openDays.length === 0) {
    return "Currently no opening schedule";
  }

  const firstDay = DAY_LABELS[openDays[0].dayOfWeek];
  const lastDay = DAY_LABELS[openDays[openDays.length - 1].dayOfWeek];

  return openDays.length === 7
    ? "Open every day"
    : `${firstDay} - ${lastDay}`;
}

function SectionHeading({
  icon: Icon,
  title,
  description,
}: {
  icon: ElementType;
  title: string;
  description: string;
}) {
  return (
    <div className="flex items-center gap-3">
      <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary">
        <Icon size={21} strokeWidth={2} />
      </div>

      <div>
        <h2 className="font-semibold text-base-content">{title}</h2>
        <p className="text-sm text-base-content/60">{description}</p>
      </div>
    </div>
  );
}

function InfoItem({
  label,
  value,
  icon: Icon,
  href,
  isSample = false,
}: {
  label: string;
  value: string;
  icon: ElementType;
  href?: string;
  isSample?: boolean;
}) {
  return (
    <div className="flex min-w-0 items-start gap-3">
      <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-base-200 text-neutral">
        <Icon size={18} />
      </div>

      <div className="min-w-0 flex-1">
        <p className="text-sm text-base-content/60">{label}</p>

        {href ? (
          <a
            href={href}
            className="break-words font-medium text-primary transition-colors hover:text-accent"
          >
            {value}
          </a>
        ) : (
          <p className="break-words font-medium text-base-content">
            {value}
          </p>
        )}

        {isSample && (
          <span className="mt-1 inline-block text-xs text-warning">
            Sample information
          </span>
        )}
      </div>
    </div>
  );
}

export default function DealershipDetails({
  dealershipId,
}: DealershipDetailsProps) {
  const {
    data,
    error,
    isLoading,
    refetch,
  } = useGetDealershipByIdQuery(dealershipId);

  if (isLoading) {
    return (
      <div className="flex min-h-60 items-center justify-center">
        <span className="loading loading-spinner loading-lg text-primary" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-xl border border-error/30 bg-base-100 p-6">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h2 className="font-semibold text-error">
              Failed to load dealership details
            </h2>
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

  if (!data) {
    return (
      <div className="rounded-xl border border-dashed border-base-300 bg-base-100 py-16 text-center">
        <Building2
          size={40}
          className="mx-auto mb-3 text-base-content/40"
        />
        <h2 className="font-semibold text-base-content">
          Dealership not found
        </h2>
        <p className="mt-1 text-sm text-base-content/60">
          The dealership may have been removed or does not exist.
        </p>
      </div>
    );
  }

  const address = SAMPLE_INFO.address;
  const phone = SAMPLE_INFO.phone;
  const email = SAMPLE_INFO.email;

  const sortedWorkingHours = [...data.workingHours].sort(
    (a, b) =>
      DAY_ORDER.indexOf(a.dayOfWeek) -
      DAY_ORDER.indexOf(b.dayOfWeek),
  );

  const workingHoursSummary = getWorkingHoursSummary(
    sortedWorkingHours,
  );

  const facilities = [
    {
      label: "Service Bays",
      value: data.resourceSummary.serviceBayCount,
      icon: Warehouse,
    },
    {
      label: "Technicians",
      value: data.resourceSummary.technicianCount,
      icon: Users,
    },
    {
      label: "Available Services",
      value: data.resourceSummary.serviceCount,
      icon: Wrench,
    },
  ];

  return (
    <section className="space-y-6">
      {/* Hero Banner */}
      <div className="overflow-hidden rounded-2xl border border-base-300 bg-base-100 shadow-sm">
        <div className="relative h-64 w-full md:h-80">
          <Image
            src="/images/dealership-profile.jpg"
            alt={`${data.name} dealership`}
            fill
            priority
            sizes="(max-width: 768px) 100vw, 1200px"
            className="object-cover"
          />

          <div className="absolute inset-0 bg-gradient-to-t from-primary/95 via-primary/30 to-transparent" />

          <div className="absolute inset-x-0 bottom-0 p-6 md:p-8">
            <span className="mb-3 inline-flex items-center rounded-full border border-white/30 bg-white/15 px-3 py-1 text-xs font-semibold text-white backdrop-blur-sm">
              DEALERSHIP
            </span>

            <h1 className="text-2xl font-bold text-white md:text-4xl">
              {data.name}
            </h1>

            <div className="mt-3 flex flex-col gap-2 text-sm text-white/85 sm:flex-row sm:flex-wrap sm:gap-5">
              <span className="flex items-center gap-2">
                <MapPin size={16} className="shrink-0 text-accent" />
                {address}
              </span>

              <span className="flex items-center gap-2">
                <Clock size={16} className="shrink-0 text-accent" />
                {workingHoursSummary}
              </span>
            </div>
          </div>
        </div>

        <div className="flex flex-col justify-between gap-4 p-5 sm:flex-row sm:items-center">
          <div>
            <p className="text-sm text-base-content/60">
              Welcome to
            </p>
            <p className="font-semibold text-base-content">
              {data.name}
            </p>
          </div>

          <a
            href={`tel:${phone}`}
            className="btn btn-primary gap-2"
          >
            <Phone size={16} />
            Contact Dealership
          </a>
        </div>
      </div>

      {/* Sample Data Notice */}
      <div className="alert border border-warning/30 bg-warning/10 text-base-content">
        <div className="flex items-start gap-3">
          <span className="mt-0.5 text-warning">
            <Globe size={18} />
          </span>
          <div>
            <p className="text-sm font-semibold">
              Demonstration data
            </p>
            <p className="text-sm text-base-content/70">
              Address and contact information are sample data
              for demonstration purposes and are not retrieved
              from the backend.
            </p>
          </div>
        </div>
      </div>

      {/* General Information */}
      <div className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
        <div className="border-b border-base-300 p-5">
          <SectionHeading
            icon={Building2}
            title="General Information"
            description="Basic contact and location details"
          />
        </div>

        <div className="grid gap-6 p-5 md:grid-cols-2">
          <InfoItem
            label="Dealership Name"
            value={data.name}
            icon={Building2}
          />

          <InfoItem
            label="Address"
            value={address}
            icon={MapPin}
            isSample
          />

          <InfoItem
            label="Phone Number"
            value={phone}
            icon={Phone}
            href={`tel:${phone}`}
            isSample
          />

          <InfoItem
            label="Email Address"
            value={email}
            icon={Mail}
            href={`mailto:${email}`}
            isSample
          />

          <InfoItem
            label="Time Zone"
            value={data.timeZone}
            icon={Globe}
          />

          <InfoItem
            label="Dealership ID"
            value={data.id}
            icon={Building2}
          />
        </div>
      </div>

      {/* Monthly Business Calendar */}
      <DealershipMonthCalendar
        workingHours={data.workingHours}
        dateOverrides={data.dateOverrides}
        timeZone={data.timeZone}
      />

      {/* Facilities & Capacity */}
      <div className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
        <div className="border-b border-base-300 p-5">
          <SectionHeading
            icon={Warehouse}
            title="Facilities & Capacity"
            description="Dealership service resources"
          />
        </div>

        <div className="grid grid-cols-1 gap-4 p-5 sm:grid-cols-3">
          {facilities.map((facility) => {
            const Icon = facility.icon;

            return (
              <div
                key={facility.label}
                className="rounded-xl border border-base-300 bg-base-200/70 p-5 transition-colors hover:border-primary/30 hover:bg-base-200"
              >
                <div className="mb-4 flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary">
                  <Icon size={21} />
                </div>

                <p className="text-2xl font-bold text-base-content">
                  {facility.value}
                </p>

                <p className="mt-1 text-sm text-base-content/60">
                  {facility.label}
                </p>
              </div>
            );
          })}
        </div>
      </div>

      {/* Service Bays */}
      <DealershipServiceBays dealershipId={data.id} />

      {/* Technicians */}
      <DealershipTechnicians dealershipId={data.id} />

      {/* Booking Information */}
      <div className="overflow-hidden rounded-xl bg-primary text-primary-content shadow-sm">
        <div className="flex flex-col justify-between gap-5 p-6 sm:flex-row sm:items-center">
          <div>
            <div className="flex items-center gap-2">
              <CalendarDays size={21} className="text-accent" />
              <h2 className="font-semibold">
                Ready to service your vehicle?
              </h2>
            </div>

            <p className="mt-2 text-sm text-primary-content/75">
              Choose your service and find an available appointment.
            </p>
          </div>

          <div className="flex shrink-0 items-center gap-2 rounded-lg border border-white/20 bg-white/10 px-4 py-3 text-sm">
            <span className="h-2 w-2 rounded-full bg-accent" />
            Booking feature coming soon
          </div>
        </div>
      </div>
    </section>
  );
}
