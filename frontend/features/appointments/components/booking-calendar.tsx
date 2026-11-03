"use client";

import { useMemo, useState } from "react";
import {
  ChevronLeft,
  ChevronRight,
  CalendarDays,
  CheckCircle2,
} from "lucide-react";

import type {
  DayOfWeek,
  DealershipWorkingHours,
  DealershipDateOverride,
} from "@/features/dealerships/dealership.types";

interface BookingCalendarProps {
  workingHours: DealershipWorkingHours[];
  dateOverrides: DealershipDateOverride[];
  timeZone: string;

  selectedDate: string | null;
  onSelectDate: (date: string) => void;

  disabled?: boolean;
  numberOfMonths?: number;
}

interface MonthValue {
  year: number;
  month: number;
}

type DayStatus = "open" | "closed" | "holiday" | "special";

interface CalendarDay {
  date: number;
  dateKey: string;
  status: DayStatus;
  label: string;
  hours?: string;
  isToday: boolean;
  isPast: boolean;
  isSelected: boolean;
  selectable: boolean;
}

const WEEKDAYS: { key: DayOfWeek; label: string }[] = [
  { key: "SUNDAY", label: "Sun" },
  { key: "MONDAY", label: "Mon" },
  { key: "TUESDAY", label: "Tue" },
  { key: "WEDNESDAY", label: "Wed" },
  { key: "THURSDAY", label: "Thu" },
  { key: "FRIDAY", label: "Fri" },
  { key: "SATURDAY", label: "Sat" },
];

const JS_DAY_TO_WEEKDAY: DayOfWeek[] = [
  "SUNDAY",
  "MONDAY",
  "TUESDAY",
  "WEDNESDAY",
  "THURSDAY",
  "FRIDAY",
  "SATURDAY",
];

function pad(value: number): string {
  return String(value).padStart(2, "0");
}

function toDateKey(year: number, month: number, day: number): string {
  return `${year}-${pad(month + 1)}-${pad(day)}`;
}

function getTodayInTimeZone(timeZone: string): string {
  const parts = new Intl.DateTimeFormat("en-CA", {
    timeZone,
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  }).formatToParts(new Date());

  const getPart = (type: string) =>
    parts.find((part) => part.type === type)?.value ?? "";

  return `${getPart("year")}-${getPart("month")}-${getPart("day")}`;
}

function getMonthTitle({ year, month }: MonthValue): string {
  return new Intl.DateTimeFormat("en-US", {
    month: "long",
    year: "numeric",
  }).format(new Date(year, month, 1));
}

function formatTime(time: string | null): string {
  return time ? time.slice(0, 5) : "";
}

export default function BookingCalendar({
  workingHours,
  dateOverrides,
  timeZone,
  selectedDate,
  onSelectDate,
  disabled = false,
  numberOfMonths = 3,
}: BookingCalendarProps) {
  const todayKey = getTodayInTimeZone(timeZone);

  const [selectedMonthIndex, setSelectedMonthIndex] = useState(0);

  const startMonth = useMemo(() => {
    const [year, month] = todayKey.split("-").map(Number);

    return { year, month: month - 1 };
  }, [todayKey]);

  const maxMonthIndex = Math.max(0, numberOfMonths - 1);
  const currentMonthIndex = Math.min(
    selectedMonthIndex,
    maxMonthIndex,
  );

  const currentMonth = useMemo<MonthValue>(() => {
    const date = new Date(
      startMonth.year,
      startMonth.month + currentMonthIndex,
      1,
    );

    return {
      year: date.getFullYear(),
      month: date.getMonth(),
    };
  }, [startMonth, currentMonthIndex]);

  const calendarDays = useMemo(() => {
    const { year, month } = currentMonth;

    const firstDayOfWeek = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    const workingHoursMap = new Map(
      workingHours.map((item) => [item.dayOfWeek, item]),
    );

    const overrideMap = new Map(
      dateOverrides.map((item) => [item.businessDate, item]),
    );

    const days: (CalendarDay | null)[] = [];

    for (let i = 0; i < firstDayOfWeek; i++) {
      days.push(null);
    }

    for (let day = 1; day <= daysInMonth; day++) {
      const dateKey = toDateKey(year, month, day);
      const date = new Date(year, month, day);

      const weekday = JS_DAY_TO_WEEKDAY[date.getDay()];
      const weeklySchedule = workingHoursMap.get(weekday);
      const override = overrideMap.get(dateKey);

      let status: DayStatus = "closed";
      let label = "Closed";
      let hours: string | undefined;

      if (override) {
        if (override.isClosed) {
          status = "holiday";
          label = override.reason || "Holiday";
        } else if (override.opensAt && override.closesAt) {
          status = "special";
          label = "Special hours";
          hours = `${formatTime(override.opensAt)} - ${formatTime(
            override.closesAt,
          )}`;
        }
      } else if (weeklySchedule && !weeklySchedule.isClosed) {
        if (weeklySchedule.opensAt && weeklySchedule.closesAt) {
          status = "open";
          label = "Open";
          hours = `${formatTime(
            weeklySchedule.opensAt,
          )} - ${formatTime(weeklySchedule.closesAt)}`;
        }
      }

      const isPast = dateKey < todayKey;

      days.push({
        date: day,
        dateKey,
        status,
        label,
        hours,
        isToday: dateKey === todayKey,
        isPast,
        isSelected: selectedDate === dateKey,
        selectable:
          !isPast && (status === "open" || status === "special"),
      });
    }

    return days;
  }, [
    currentMonth,
    workingHours,
    dateOverrides,
    todayKey,
    selectedDate,
  ]);

  const canGoPrevious = currentMonthIndex > 0;
  const canGoNext = currentMonthIndex < maxMonthIndex;

  function changeMonth(offset: number) {
    setSelectedMonthIndex((previous) =>
      Math.max(0, Math.min(maxMonthIndex, previous + offset)),
    );
  }

  return (
    <section className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
      <div className="border-b border-base-300 p-5">
        <div className="flex items-center gap-3">
          <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-primary/10 text-primary">
            <CalendarDays size={22} />
          </div>

          <div>
            <h2 className="font-semibold text-base-content">
              Select Appointment Date
            </h2>

            <p className="text-sm text-base-content/60">
              Choose your preferred appointment date
            </p>
          </div>
        </div>
      </div>

      <div className="space-y-5 p-5">
        <div className="flex items-center justify-between gap-3">
          <button
            type="button"
            className="btn btn-square btn-ghost text-primary"
            onClick={() => changeMonth(-1)}
            disabled={!canGoPrevious || disabled}
            aria-label="Previous month"
          >
            <ChevronLeft size={22} />
          </button>

          <div className="text-center">
            <h3 className="text-lg font-bold text-base-content">
              {getMonthTitle(currentMonth)}
            </h3>

            <p className="text-xs text-base-content/50">
              {currentMonthIndex + 1} of {numberOfMonths} months
            </p>
          </div>

          <button
            type="button"
            className="btn btn-square btn-primary"
            onClick={() => changeMonth(1)}
            disabled={!canGoNext || disabled}
            aria-label="Next month"
          >
            <ChevronRight size={22} />
          </button>
        </div>

        <div className="grid grid-cols-7 gap-1.5 sm:gap-2">
          {WEEKDAYS.map((weekday) => (
            <div
              key={weekday.key}
              className="py-2 text-center text-xs font-semibold text-base-content/60 sm:text-sm"
            >
              {weekday.label}
            </div>
          ))}

          {calendarDays.map((day, index) => {
            if (!day) {
              return (
                <div
                  key={`empty-${index}`}
                  className="min-h-20 sm:min-h-24"
                />
              );
            }

            const isDisabled = disabled || !day.selectable;

            return (
              <button
                key={day.dateKey}
                type="button"
                disabled={isDisabled}
                onClick={() => onSelectDate(day.dateKey)}
                aria-pressed={day.isSelected}
                title={`${day.dateKey}: ${day.label}${
                  day.hours ? ` (${day.hours})` : ""
                }`}
                className={`relative flex min-h-20 flex-col items-center justify-center gap-1 rounded-lg border p-1 text-center transition-all sm:min-h-24 sm:p-2 ${
                  day.isSelected
                    ? "border-primary bg-primary text-primary-content shadow-md"
                    : day.isPast
                      ? "border-base-300 bg-base-200 text-base-content/30"
                      : day.status === "open"
                        ? "border-secondary/50 bg-secondary/15 text-base-content hover:border-primary"
                        : day.status === "special"
                          ? "border-accent/50 bg-accent/10 text-base-content hover:border-primary"
                          : day.status === "holiday"
                            ? "border-warning/40 bg-warning/10 text-base-content/60"
                            : "border-base-300 bg-base-200 text-base-content/40"
                } ${
                  day.isToday
                    ? "ring-2 ring-primary ring-offset-2 ring-offset-base-100"
                    : ""
                } ${
                  isDisabled
                    ? "cursor-not-allowed"
                    : "cursor-pointer hover:shadow-sm"
                }`}
              >
                <span className="text-sm font-bold sm:text-base">
                  {day.date}
                </span>

                {day.isSelected ? (
                  <CheckCircle2 size={15} />
                ) : day.isToday ? (
                  <span className="text-[10px] font-bold">
                    Today
                  </span>
                ) : null}

                <span className="line-clamp-2 text-[9px] leading-tight sm:text-xs">
                  {day.isPast
                    ? "Past"
                    : day.status === "holiday"
                      ? "Holiday"
                      : day.status === "closed"
                        ? "Closed"
                        : day.status === "special"
                          ? "Special"
                          : day.hours || "Open"}
                </span>
              </button>
            );
          })}
        </div>

        {selectedDate && (
          <div className="flex items-center gap-2 rounded-lg bg-primary/5 p-3 text-sm text-primary">
            <CheckCircle2 size={18} />
            <span>
              Selected date: <strong>{selectedDate}</strong>
            </span>
          </div>
        )}

        <div className="space-y-3 border-t border-base-300 pt-5">
          <h3 className="text-sm font-semibold text-base-content">
            Calendar legend
          </h3>

          <div className="grid gap-3 sm:grid-cols-2">
            {[
              {
                label: "Available business day",
                style: "border-secondary/50 bg-secondary/15",
              },
              {
                label: "Regular day off",
                style: "border-base-300 bg-base-200",
              },
              {
                label: "Holiday / Special closure",
                style: "border-warning/40 bg-warning/10",
              },
              {
                label: "Special opening hours",
                style: "border-accent/50 bg-accent/10",
              },
              {
                label: "Selected date",
                style: "border-primary bg-primary",
              },
            ].map((item) => (
              <div
                key={item.label}
                className="flex items-center gap-3 text-sm"
              >
                <span
                  className={`h-4 w-4 shrink-0 rounded border ${item.style}`}
                />

                <span className="text-base-content/70">
                  {item.label}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
}
