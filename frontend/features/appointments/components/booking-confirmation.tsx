"use client";

import { useState } from "react";
import {
  CalendarDays,
  Clock3,
  UserRound,
  Wrench,
  Building2,
  CarFront,
  CheckCircle2,
  AlertCircle,
  ArrowLeft,
  Loader2,
  ClipboardCheck,
} from "lucide-react";

import {
  useCreateAppointmentMutation,
} from "@/features/appointments/appointment.api";

import type {
  CreateAppointmentRequest,
  CreateAppointmentResponse,
} from "@/features/appointments/appointment.types";

import type {
  SelectableServiceType,
} from "./service-type-selector";

import { useAppDispatch } from "@/lib/redux/hooks";

import {
  enqueueToast,
  ToastType,
} from "@/features/shared/toast.slice";

import {
  getApiErrorMessage,
  getApiErrorCode,
} from "@/lib/api/error.utils";

interface BookingConfirmationProps {
  customerId: string;
  vehicleId: string;

  dealership: {
    id: string;
    name: string;
    timeZone: string;
  };

  serviceType: SelectableServiceType;

  technician: {
    id: string;
    fullName: string;
  };

  selectedDate: string;
  startTime: string;

  onBack: () => void;
  onSuccess?: (appointmentId: string) => void;
}

function formatDate(
  date: string,
  timeZone: string,
): string {
  return new Intl.DateTimeFormat("en-US", {
    timeZone,
    weekday: "long",
    // month: "long",
    day: "numeric",
    year: "numeric",
  }).format(new Date(`${date}T12:00:00Z`));
}

function formatDateTime(
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

function getErrorMessage(error: unknown): string {
  if (!error || typeof error !== "object") {
    return "An unexpected error occurred.";
  }

  if ("status" in error) {
    if (error.status === 409) {
      return (
        "The selected time slot is no longer available. " +
        "Please go back and choose another slot."
      );
    }

    if (error.status === 400) {
      return "The booking information is invalid. Please review your selection.";
    }

    if (error.status === 404) {
      return "One of the selected booking resources could not be found.";
    }

    if (error.status === 500) {
      return "The server could not complete your booking. Please try again.";
    }
  }

  return "Unable to complete your booking. Please try again.";
}

function SummaryItem({
  icon: Icon,
  label,
  value,
}: {
  icon: typeof CalendarDays;
  label: string;
  value: string;
}) {
  return (
    <div className="flex items-start gap-3 rounded-lg bg-base-200/50 p-4">
      <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/10 text-primary">
        <Icon size={19} />
      </div>

      <div>
        <p className="text-xs text-base-content/60">
          {label}
        </p>

        <p className="mt-1 font-medium text-base-content">
          {value}
        </p>
      </div>
    </div>
  );
}

export default function BookingConfirmation({
  customerId,
  vehicleId,
  dealership,
  serviceType,
  technician,
  selectedDate,
  startTime,
  onBack,
  onSuccess,
}: BookingConfirmationProps) {
  const [
    createAppointment,
    { isLoading },
  ] = useCreateAppointmentMutation();

  const [errorMessage, setErrorMessage] = useState<string | null>(
    null,
  );

  const [createdAppointment, setCreatedAppointment] =
    useState<CreateAppointmentResponse | null>(null);

  const formattedDate = formatDate(
    selectedDate,
    dealership.timeZone,
  );

  const formattedStartTime = formatDateTime(
    startTime,
    dealership.timeZone,
  );

  const dispatch = useAppDispatch();

  
async function handleConfirm() {
  if (isLoading || createdAppointment) return;

  setErrorMessage(null);

  const request: CreateAppointmentRequest = {
    customerId,
    vehicleId,
    dealershipId: dealership.id,
    serviceTypeId: serviceType.id,
    technicianId: technician.id,
    startTime,
  };

  try {
    const response = await createAppointment(request).unwrap();

    setCreatedAppointment(response);

    // Success toast is handled by BookingWizard
    onSuccess?.(response.id);

  } catch (error: unknown) {
    const message = getApiErrorMessage(error);
    const errorCode = getApiErrorCode(error);

    // Display inline error
    setErrorMessage(message);

    // Display error toast
    dispatch(
      enqueueToast({
        type: ToastType.Error,
        message,
        duration: 5000,
        testId: "booking-error-toast",
      }),
    );

    // Log detailed error in development only
    if (process.env.NODE_ENV === "development") {
      console.error("Booking appointment failed:", {
        errorCode,
        error,
      });
    }
  }
}


  if (createdAppointment) {
    const appointment = createdAppointment;

    return (
      <section className="overflow-hidden rounded-xl border border-success/30 bg-base-100 shadow-sm">
        <div className="flex flex-col items-center gap-4 border-b border-base-300 p-8 text-center">
          <div className="flex h-16 w-16 items-center justify-center rounded-full bg-success/10 text-success">
            <CheckCircle2 size={34} />
          </div>

          <div>
            <h2 className="text-2xl font-bold text-base-content">
              Appointment Booked Successfully!
            </h2>

            <p className="mt-2 text-sm text-base-content/60">
              Your service appointment has been created.
            </p>
          </div>

          <span className="badge badge-success badge-outline">
            {appointment.status}
          </span>
        </div>

        <div className="space-y-5 p-5">
          <div className="rounded-lg bg-base-200/50 p-4">
            <p className="text-xs text-base-content/60">
              Appointment ID
            </p>

            <p className="mt-1 break-all font-mono text-sm font-semibold text-base-content">
              {appointment.id}
            </p>
          </div>

          <div className="grid gap-3 sm:grid-cols-2">
            <SummaryItem
              icon={Building2}
              label="Dealership"
              value={appointment.dealership.name}
            />

            <SummaryItem
              icon={Wrench}
              label="Service"
              value={appointment.service.name}
            />

            <SummaryItem
              icon={UserRound}
              label="Technician"
              value={appointment.technician.fullName}
            />

            <SummaryItem
              icon={CarFront}
              label="Service Bay"
              value={appointment.serviceBay.bayCode}
            />

            <SummaryItem
              icon={CalendarDays}
              label="Appointment Date"
              value={formatDate(
                appointment.schedule.startTime.slice(0, 10),
                appointment.schedule.timeZone,
              )}
            />

            <SummaryItem
              icon={Clock3}
              label="Scheduled Time"
              value={`${formatDateTime(
                appointment.schedule.startTime,
                appointment.schedule.timeZone,
              )} - ${formatDateTime(
                appointment.schedule.endTime,
                appointment.schedule.timeZone,
              )}`}
            />
          </div>

          <p className="text-center text-xs text-base-content/50">
            Your service bay was automatically assigned
            based on availability.
          </p>
        </div>
      </section>
    );
  }

  return (
    <section className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
      <div className="border-b border-base-300 p-5">
        <div className="flex items-center gap-3">
          <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-primary/10 text-primary">
            <ClipboardCheck size={22} />
          </div>

          <div>
            <h2 className="font-semibold text-base-content">
              Confirm Your Appointment
            </h2>

            <p className="text-sm text-base-content/60">
              Review your booking details before confirming
            </p>
          </div>
        </div>
      </div>

      <div className="space-y-5 p-5">
        <div className="grid gap-3 sm:grid-cols-2">
          <SummaryItem
            icon={Building2}
            label="Dealership"
            value={dealership.name}
          />

          <SummaryItem
            icon={Wrench}
            label="Service Type"
            value={serviceType.name}
          />

          <SummaryItem
            icon={UserRound}
            label="Technician"
            value={technician.fullName}
          />

          <SummaryItem
            icon={CalendarDays}
            label="Appointment Date"
            value={formattedDate}
          />

          <SummaryItem
            icon={Clock3}
            label="Start Time"
            value={formattedStartTime}
          />

          <SummaryItem
            icon={Clock3}
            label="Estimated Duration"
            value={`${serviceType.durationMinutes} minutes`}
          />
        </div>

        <div className="rounded-lg border border-info/30 bg-info/5 p-4">
          <div className="flex items-start gap-3">
            <CarFront
              size={20}
              className="mt-0.5 shrink-0 text-info"
            />

            <div>
              <h3 className="text-sm font-semibold text-base-content">
                Automatic Service Bay Assignment
              </h3>

              <p className="mt-1 text-sm text-base-content/70">
                An available service bay will be automatically
                assigned when your appointment is confirmed.
              </p>
            </div>
          </div>
        </div>

        {errorMessage && (
          <div
            role="alert"
            className="flex items-start gap-3 rounded-lg border border-error/30 bg-error/10 p-4"
          >
            <AlertCircle
              size={20}
              className="mt-0.5 shrink-0 text-error"
            />

            <p className="text-sm text-error">
              {errorMessage}
            </p>
          </div>
        )}

        <div className="flex items-center justify-between gap-3 border-t border-base-300 pt-5">
          <button
            type="button"
            className="btn btn-outline"
            onClick={onBack}
            disabled={isLoading}
          >
            <ArrowLeft size={17} />
            Back
          </button>

          <button
            type="button"
            className="btn btn-primary"
            onClick={handleConfirm}
            disabled={isLoading}
          >
            {isLoading ? (
              <>
                <Loader2
                  size={17}
                  className="animate-spin"
                />
                Confirming...
              </>
            ) : (
              <>
                <CheckCircle2 size={17} />
                Confirm Booking
              </>
            )}
          </button>
        </div>
      </div>
    </section>
  );
}
