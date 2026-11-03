"use client";

import { Check, Clock3 } from "lucide-react";

import type {
  AvailabilitySlot,
} from "@/features/appointments/availability.types";

interface AvailableSlotGridProps {
  slots: AvailabilitySlot[];
  timeZone: string;

  selectedStartTime: string | null;
  onSelectSlot: (slot: AvailabilitySlot) => void;

  disabled?: boolean;
}

function formatSlotTime(
  dateTime: string,
  timeZone: string,
): string {
  return new Intl.DateTimeFormat("en-GB", {
    timeZone,
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  }).format(new Date(dateTime));
}

export default function AvailableSlotGrid({
  slots,
  timeZone,
  selectedStartTime,
  onSelectSlot,
  disabled = false,
}: AvailableSlotGridProps) {
  const sortedSlots = [...slots].sort((a, b) =>
    a.startTime.localeCompare(b.startTime),
  );

  if (sortedSlots.length === 0) {
    return (
      <div className="flex min-h-24 flex-col items-center justify-center gap-2 rounded-lg bg-base-200/50 p-4 text-center">
        <Clock3 size={22} className="text-base-content/40" />

        <p className="text-sm text-base-content/60">
          No time slots available for this day.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between gap-3">
        <h4 className="flex items-center gap-2 text-sm font-semibold text-base-content">
          <Clock3 size={16} className="text-primary" />
          Available Time Slots
        </h4>

        <span className="text-xs text-base-content/60">
          {sortedSlots.filter((slot) => slot.available).length}{" "}
          available
        </span>
      </div>

      <div className="grid grid-cols-2 gap-2 sm:grid-cols-3 lg:grid-cols-4">
        {sortedSlots.map((slot) => {
          const isSelected =
            selectedStartTime === slot.startTime;

          const isDisabled = disabled || !slot.available;

          const start = formatSlotTime(
            slot.startTime,
            timeZone,
          );

          const end = formatSlotTime(
            slot.endTime,
            timeZone,
          );

          return (
            <button
              key={slot.startTime}
              type="button"
              disabled={isDisabled}
              onClick={() => onSelectSlot(slot)}
              aria-pressed={isSelected}
              className={`flex min-h-20 flex-col items-center justify-center gap-1 rounded-lg border p-2 text-center transition-all ${
                !slot.available
                  ? "cursor-not-allowed border-base-300 bg-base-200 text-base-content/40"
                  : isSelected
                    ? "border-primary bg-primary text-primary-content shadow-md"
                    : "border-base-300 bg-base-100 text-base-content hover:border-primary hover:bg-primary/5"
              } ${
                disabled ? "cursor-not-allowed opacity-60" : ""
              }`}
            >
              <span className="text-sm font-semibold">
                {start} - {end}
              </span>

              {isSelected && slot.available ? (
                <span className="flex items-center gap-1 text-xs">
                  <Check size={13} />
                  Selected
                </span>
              ) : !slot.available ? (
                <span className="text-[11px]">
                  Fully booked
                </span>
              ) : (
                <span className="text-[11px] opacity-70">
                  Available
                </span>
              )}
            </button>
          );
        })}
      </div>
    </div>
  );
}
