"use client";

import { useMemo } from "react";
import { skipToken } from "@reduxjs/toolkit/query";

import {
  Users,
  UserRound,
  CheckCircle2,
  AlertCircle,
  RefreshCw,
  Clock3,
} from "lucide-react";

import {
  useGetAppointmentAvailabilityQuery,
} from "@/features/appointments/availability.api";

import type {
  AvailabilitySlot,
} from "@/features/appointments/availability.types";

import AvailableSlotGrid from "./available-slot-grid";

// Dealership working hours, expressed in its local time zone.
const OPEN_MINUTES = 9 * 60;
const CLOSE_MINUTES = 17 * 60;

function getLocalDateAndMinutes(isoTime: string, timeZone: string) {
  const date = new Date(isoTime);
  if (Number.isNaN(date.getTime())) return null;

  const parts = new Intl.DateTimeFormat("en-GB", {
    timeZone,
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    hourCycle: "h23",
  }).formatToParts(date);

  const get = (type: string) => parts.find((part) => part.type === type)?.value;
  const year = get("year");
  const month = get("month");
  const day = get("day");
  const hour = Number(get("hour"));
  const minute = Number(get("minute"));

  if (!year || !month || !day || !Number.isFinite(hour) || !Number.isFinite(minute)) {
    return null;
  }

  return {
    date: `${year}-${month}-${day}`,
    minutes: hour * 60 + minute,
  };
}

function isWithinWorkingHours(
  slot: AvailabilitySlot,
  date: string,
  timeZone: string,
  durationMinutes: number,
) {
  const start = getLocalDateAndMinutes(slot.startTime, timeZone);
  if (!start || start.date !== date || start.minutes < OPEN_MINUTES) return false;

  // Calculate the actual end instant so DST/time-zone changes are handled.
  const endInstant = new Date(new Date(slot.startTime).getTime() + durationMinutes * 60_000);
  const end = getLocalDateAndMinutes(endInstant.toISOString(), timeZone);

  return end !== null && end.date === date && end.minutes <= CLOSE_MINUTES;
}

interface TechnicianAvailabilityProps {
  dealershipId: string;
  serviceTypeId: string;
  selectedDate: string;

  technicianId?: string | null;

  selectedTechnicianId: string | null;
  selectedStartTime: string | null;

  onSelectSlot: (
    technicianId: string,
    startTime: string,
  ) => void;

  disabled?: boolean;
}

export default function TechnicianAvailability({
  dealershipId,
  serviceTypeId,
  selectedDate,
  technicianId,
  selectedTechnicianId,
  selectedStartTime,
  onSelectSlot,
  disabled = false,
}: TechnicianAvailabilityProps) {
  const queryArgs =
    dealershipId && serviceTypeId && selectedDate
      ? {
          dealershipId,
          serviceTypeId,
          date: selectedDate,
          ...(technicianId ? { technicianId } : {}),
        }
      : skipToken;

  const {
    currentData,
    isLoading,
    isFetching,
    isError,
    refetch,
  } = useGetAppointmentAvailabilityQuery(queryArgs, {
    refetchOnMountOrArgChange: true,
  });

  // currentData belongs to the current query arguments.
  // Do not display slots from a previously selected date.
  const availability = currentData;

  const technicians = useMemo(() => {
    if (!availability) return [];

    return availability.technicians.map((technician) => ({
      ...technician,
      availableSlots: technician.availableSlots.filter((slot) =>
        isWithinWorkingHours(
          slot,
          availability.date,
          availability.timeZone,
          availability.durationMinutes,
        ),
      ),
    }));
  }, [availability]);

  const availableTechnicianCount = useMemo(
    () =>
      technicians.filter(
        (technician) =>
          technician.active &&
          technician.availableSlots.some(
            (slot) => slot.available,
          ),
      ).length,
    [technicians],
  );

  const isSelectionDisabled = disabled || isFetching;

  function handleSelectSlot(
    technicianId: string,
    slot: AvailabilitySlot,
  ) {
    if (
      isSelectionDisabled ||
      !slot.available ||
      !availability ||
      !isWithinWorkingHours(
        slot,
        availability.date,
        availability.timeZone,
        availability.durationMinutes,
      )
    ) return;

    onSelectSlot(technicianId, slot.startTime);
  }

  if (isLoading || (!availability && isFetching)) {
    return (
      <section className="rounded-xl border border-base-300 bg-base-100 p-8 shadow-sm">
        <div className="flex min-h-48 flex-col items-center justify-center gap-3">
          <span className="loading loading-spinner loading-lg text-primary" />

          <p className="text-sm text-base-content/60">
            Checking technician availability...
          </p>
        </div>
      </section>
    );
  }

  if (isError) {
    return (
      <section className="rounded-xl border border-error/30 bg-base-100 p-8 shadow-sm">
        <div className="flex min-h-48 flex-col items-center justify-center gap-4 text-center">
          <AlertCircle size={32} className="text-error" />

          <div>
            <h3 className="font-semibold text-base-content">
              Unable to load availability
            </h3>

            <p className="mt-1 text-sm text-base-content/60">
              Something went wrong while checking available
              technicians and time slots.
            </p>
          </div>

          <button
            type="button"
            className="btn btn-outline btn-primary"
            onClick={() => refetch()}
          >
            <RefreshCw size={16} />
            Try Again
          </button>
        </div>
      </section>
    );
  }

  if (!availability) return null;

  return (
    <section className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
      <div className="border-b border-base-300 p-5">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div className="flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-primary/10 text-primary">
              <Users size={22} />
            </div>

            <div>
              <h2 className="font-semibold text-base-content">
                Technician Availability
              </h2>

              <p className="text-sm text-base-content/60">
                Select a technician and an available time slot
              </p>
            </div>
          </div>

          <button
            type="button"
            className="btn btn-square btn-ghost"
            onClick={() => refetch()}
            disabled={isFetching}
            aria-label="Refresh availability"
            title="Refresh availability"
          >
            <RefreshCw
              size={18}
              className={isFetching ? "animate-spin" : ""}
            />
          </button>
        </div>
      </div>

      <div className="space-y-5 p-5">
        <div className="flex flex-wrap items-center justify-between gap-3 rounded-lg bg-base-200/50 p-4">
          <div className="space-y-1">
            <p className="text-sm font-semibold text-base-content">
              {availability.date}
            </p>

            <p className="flex items-center gap-2 text-xs text-base-content/60">
              <Clock3 size={14} />
              Service duration: {availability.durationMinutes} minutes
            </p>
          </div>

          <div className="flex items-center gap-2 text-sm">
            <CheckCircle2
              size={17}
              className="text-success"
            />

            <span className="font-medium text-base-content">
              {availableTechnicianCount}
            </span>

            <span className="text-base-content/60">
              available technicians
            </span>
          </div>
        </div>

        {isFetching && (
          <div className="flex items-center gap-2 text-sm text-primary">
            <span className="loading loading-spinner loading-xs" />
            Refreshing availability...
          </div>
        )}

        {technicians.length === 0 ? (
          <div className="flex min-h-48 flex-col items-center justify-center gap-3 text-center">
            <Users
              size={32}
              className="text-base-content/40"
            />

            <h3 className="font-semibold text-base-content">
              No qualified technicians found
            </h3>

            <p className="max-w-sm text-sm text-base-content/60">
              No technicians match the selected service and date.
              Please try another service or appointment date.
            </p>
          </div>
        ) : (
          <div className="grid gap-4">
            {technicians.map((technician) => {
              const availableSlotCount =
                technician.active
                  ? technician.availableSlots.filter(
                      (slot) => slot.available,
                    ).length
                  : 0;

              const hasAvailableSlots =
                availableSlotCount > 0;

              const isTechnicianSelected =
                selectedTechnicianId === technician.id;

              return (
                <div
                  key={technician.id}
                  className={`overflow-hidden rounded-xl border transition-all ${
                    isTechnicianSelected
                      ? "border-primary bg-primary/5"
                      : "border-base-300 bg-base-100"
                  }`}
                >
                  <div className="flex flex-wrap items-center justify-between gap-3 border-b border-base-300 p-4">
                    <div className="flex items-center gap-3">
                      <div
                        className={`flex h-12 w-12 items-center justify-center rounded-full ${
                          isTechnicianSelected
                            ? "bg-primary text-primary-content"
                            : "bg-base-200 text-base-content/60"
                        }`}
                      >
                        <UserRound size={23} />
                      </div>

                      <div>
                        <h3 className="font-semibold text-base-content">
                          {technician.fullName}
                        </h3>

                        <p className="text-xs text-base-content/60">
                          Qualified Technician
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center gap-2">
                      {isTechnicianSelected && (
                        <span className="badge badge-primary badge-outline gap-1">
                          <CheckCircle2 size={13} />
                          Selected
                        </span>
                      )}

                      {!technician.active ? (
                        <span className="badge badge-ghost">
                          Inactive
                        </span>
                      ) : hasAvailableSlots ? (
                        <span className="badge badge-success badge-outline">
                          {availableSlotCount} available
                        </span>
                      ) : (
                        <span className="badge badge-ghost">
                          Fully booked
                        </span>
                      )}
                    </div>
                  </div>

                  <div className="p-4">
                    <AvailableSlotGrid
                      slots={technician.availableSlots}
                      timeZone={availability.timeZone}
                      selectedStartTime={
                        isTechnicianSelected
                          ? selectedStartTime
                          : null
                      }
                      onSelectSlot={(slot) =>
                        handleSelectSlot(
                          technician.id,
                          slot,
                        )
                      }
                      disabled={
                        isSelectionDisabled ||
                        !technician.active
                      }
                    />
                  </div>
                </div>
              );
            })}
          </div>
        )}

        <p className="text-xs text-base-content/50">
          Availability may change before your booking is
          confirmed. The system will verify the technician
          and automatically assign an available service bay
          when you submit your appointment.
        </p>
      </div>
    </section>
  );
}
