"use client";

import { useMemo, useState } from "react";
import { CalendarDays, ChevronLeft, ChevronRight, Clock3, Info } from "lucide-react";

export interface TechnicianWorkInterval {
  startsAt: string;
  endsAt: string;
}

export interface TechnicianWorkingDay {
  dayOfWeek: string;
  intervals: TechnicianWorkInterval[];
}

export interface TechnicianDateOverride {
  workDate: string;
  isUnavailable: boolean;
  startsAt: string | null;
  endsAt: string | null;
  reason: string | null;
}

interface TechnicianWeeklyCalendarProps {
  workingHours: TechnicianWorkingDay[];
  dateOverrides: TechnicianDateOverride[];
}

const DAY_NAMES = ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];
const DATE_LABEL = new Intl.DateTimeFormat("en-US", { month: "short", day: "numeric", year: "numeric", timeZone: "UTC" });

function toDateKey(date: Date): string {
  return date.toISOString().slice(0, 10);
}

function addDays(date: Date, count: number): Date {
  const result = new Date(date);
  result.setUTCDate(result.getUTCDate() + count);
  return result;
}

function startOfMonday(date: Date): Date {
  const weekday = date.getUTCDay();
  return addDays(date, -((weekday + 6) % 7));
}

function timeLabel(value: string): string {
  return value.slice(0, 5);
}

export default function TechnicianWeeklyCalendar({ workingHours, dateOverrides }: TechnicianWeeklyCalendarProps) {
  // Use UTC calendar arithmetic so a 7-day week remains stable across DST changes.
  const [today] = useState(() => {
    const now = new Date();
    return new Date(Date.UTC(now.getFullYear(), now.getMonth(), now.getDate()));
  });
  const [weekOffset, setWeekOffset] = useState(0);
  const [selectedDate, setSelectedDate] = useState(() => toDateKey(today));

  const days = useMemo(() => {
    const monday = addDays(startOfMonday(today), weekOffset * 7);
    return Array.from({ length: 7 }, (_, index) => addDays(monday, index));
  }, [today, weekOffset]);

  const selected = days.find((day) => toDateKey(day) === selectedDate) ?? days[0];
  const selectedKey = toDateKey(selected);

  function scheduleFor(date: Date) {
    const key = toDateKey(date);
    const override = dateOverrides.find((item) => item.workDate.slice(0, 10) === key);
    if (override) {
      if (override.isUnavailable) return { intervals: [] as TechnicianWorkInterval[], override };
      const intervals = override.startsAt && override.endsAt
        ? [{ startsAt: override.startsAt, endsAt: override.endsAt }]
        : [];
      return { intervals, override };
    }
    const regular = workingHours.find((item) => item.dayOfWeek.toUpperCase() === DAY_NAMES[date.getUTCDay()]);
    return { intervals: regular?.intervals ?? [], override: null };
  }

  function moveWeek(direction: number) {
    const nextOffset = weekOffset + direction;
    const nextMonday = addDays(startOfMonday(today), nextOffset * 7);
    setWeekOffset(nextOffset);
    setSelectedDate(toDateKey(nextMonday));
  }

  const schedule = scheduleFor(selected);

  return (
    <section className="rounded-xl border border-base-300 bg-base-100 p-5 shadow-sm sm:p-6">
      <div className="mb-5 flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10 text-primary"><CalendarDays size={21} /></div>
        <div>
          <h2 className="font-semibold">Weekly Working Calendar</h2>
          <p className="text-sm text-base-content/60">Browse the technician's scheduled working hours</p>
        </div>
      </div>

      <div className="mb-4 flex items-center justify-between gap-3">
        <button type="button" className="btn btn-outline btn-sm" onClick={() => moveWeek(-1)} aria-label="Previous week"><ChevronLeft size={18} /></button>
        <div className="text-center text-sm font-semibold">
          {DATE_LABEL.format(days[0])} – {DATE_LABEL.format(days[6])}
        </div>
        <button type="button" className="btn btn-outline btn-sm" onClick={() => moveWeek(1)} aria-label="Next week"><ChevronRight size={18} /></button>
      </div>

      <div className="grid grid-cols-7 gap-1.5 sm:gap-2">
        {days.map((day) => {
          const key = toDateKey(day);
          const item = scheduleFor(day);
          const active = key === selectedKey;
          const hasHours = item.intervals.length > 0;
          return (
            <button key={key} type="button" onClick={() => setSelectedDate(key)} aria-pressed={active}
              className={`flex min-h-20 flex-col items-center justify-center gap-1 rounded-lg border px-1 py-3 text-center transition-colors ${active ? "border-primary bg-primary text-primary-content" : "border-base-300 bg-base-100 hover:border-primary/50 hover:bg-primary/5"}`}>
              <span className="text-[11px] sm:text-xs">{DAY_NAMES[day.getUTCDay()].slice(0, 3)}</span>
              <span className="text-base font-semibold">{day.getUTCDate()}</span>
              <span className={`h-1.5 w-1.5 rounded-full ${hasHours ? active ? "bg-primary-content" : "bg-success" : active ? "bg-primary-content/40" : "bg-base-content/25"}`} />
            </button>
          );
        })}
      </div>

      <div className="mt-5 rounded-xl border border-base-300 bg-base-200/40 p-4">
        <div className="mb-3 flex flex-wrap items-center justify-between gap-2">
          <h3 className="flex items-center gap-2 text-sm font-semibold"><Clock3 size={17} className="text-primary" /> {DATE_LABEL.format(selected)}</h3>
          {schedule.override && (
            <span className={`badge ${schedule.override.isUnavailable ? "badge-warning" : "badge-info"}`}>
              {schedule.override.isUnavailable ? "Day off" : "Special hours"}
            </span>
          )}
        </div>
        {schedule.override?.reason && <p className="mb-3 text-sm text-base-content/70">{schedule.override.reason}</p>}
        {schedule.intervals.length === 0 ? (
          <p className="text-sm text-base-content/60">No working hours scheduled for this day.</p>
        ) : (
          <div className="flex flex-wrap gap-2">
            {schedule.intervals.map((interval, index) => (
              <span key={`${selectedKey}-${index}`} className="badge badge-outline badge-primary gap-2 px-3 py-3">
                <Clock3 size={13} /> {timeLabel(interval.startsAt)} – {timeLabel(interval.endsAt)}
              </span>
            ))}
          </div>
        )}
      </div>
      <p className="mt-4 flex items-start gap-2 text-xs text-base-content/60">
        <Info size={15} className="mt-0.5 shrink-0" />
        This calendar shows scheduled working hours, not confirmed booking availability. Available appointment slots also depend on service bay availability.
      </p>
    </section>
  );
}
