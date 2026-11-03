"use client";

import { useMemo, useState } from "react";
import {
    ArrowLeft,
    ArrowRight,
    CalendarDays,
    CheckCircle2,
    UserRound,
    Wrench,
    Search,
    AlertCircle,
} from "lucide-react";

import type {
    DealershipWorkingHours,
    DealershipDateOverride,
} from "@/features/dealerships/dealership.types";

import {
    useGetDealershipTechniciansQuery,
} from "@/features/technicians/technician.api";

import type {
    TechnicianItem,
} from "@/features/technicians/technician.types";

import ServiceTypeSelector, {
    type SelectableServiceType,
} from "./service-type-selector";

import BookingCalendar from "./booking-calendar";
import TechnicianAvailability from "./technician-availability";
import BookingConfirmation from "./booking-confirmation";

export type BookingFlow =
    | "SERVICE_FIRST"
    | "DATE_FIRST"
    | "TECHNICIAN_FIRST";

type BookingStep =
    | "SERVICE_TYPE"
    | "CALENDAR"
    | "TECHNICIAN"
    | "AVAILABILITY"
    | "CONFIRMATION";

interface BookingState {
    serviceTypeId: string | null;
    selectedDate: string | null;
    technicianId: string | null;
    startTime: string | null;
}

interface BookingWizardProps {
    dealership: {
        id: string;
        name: string;
        timeZone: string;
        workingHours: DealershipWorkingHours[];
        dateOverrides: DealershipDateOverride[];
    };

    serviceTypes: SelectableServiceType[];

    customerId: string;
    vehicleId: string;

    onBookingSuccess?: (appointmentId: string) => void;
}

const BOOKING_FLOW_STEPS: Record<
    BookingFlow,
    BookingStep[]
> = {
    SERVICE_FIRST: [
        "SERVICE_TYPE",
        "CALENDAR",
        "AVAILABILITY",
        "CONFIRMATION",
    ],

    DATE_FIRST: [
        "CALENDAR",
        "SERVICE_TYPE",
        "AVAILABILITY",
        "CONFIRMATION",
    ],

    TECHNICIAN_FIRST: [
        "TECHNICIAN",
        "SERVICE_TYPE",
        "CALENDAR",
        "AVAILABILITY",
        "CONFIRMATION",
    ],
};

const STEP_LABELS: Record<BookingStep, string> = {
    SERVICE_TYPE: "Service",
    CALENDAR: "Date",
    TECHNICIAN: "Technician",
    AVAILABILITY: "Time Slot",
    CONFIRMATION: "Confirm",
};

function TechnicianSelector({
    technicians,
    selectedTechnicianId,
    onSelect,
    isLoading,
}: {
    technicians: TechnicianItem[];
    selectedTechnicianId: string | null;
    onSelect: (id: string) => void;
    isLoading: boolean;
}) {
    const [search, setSearch] = useState("");
    const [serviceTypeFilter, setServiceTypeFilter] = useState("");

    const serviceOptions = useMemo(() => {
        const map = new Map<string, string>();

        technicians.forEach((technician) => {
            technician.qualifications.forEach((qualification) => {
                map.set(
                    qualification.serviceTypeId,
                    qualification.serviceName,
                );
            });
        });

        return Array.from(map.entries())
            .map(([id, name]) => ({ id, name }))
            .sort((a, b) => a.name.localeCompare(b.name));
    }, [technicians]);

    const filteredTechnicians = useMemo(() => {
        const keyword = search.trim().toLowerCase();

        return technicians
            .filter((technician) => technician.active)
            .filter((technician) => {
                if (!keyword) return true;

                return technician.fullName
                    .toLowerCase()
                    .includes(keyword);
            })
            .filter((technician) => {
                if (!serviceTypeFilter) return true;

                return technician.qualifications.some(
                    (qualification) =>
                        qualification.serviceTypeId === serviceTypeFilter,
                );
            })
            .sort((a, b) =>
                a.fullName.localeCompare(b.fullName),
            );
    }, [technicians, search, serviceTypeFilter]);

    return (
        <section className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
            <div className="border-b border-base-300 p-5">
                <div className="flex items-center gap-3">
                    <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-primary/10 text-primary">
                        <UserRound size={22} />
                    </div>

                    <div>
                        <h2 className="font-semibold text-base-content">
                            Select Technician
                        </h2>

                        <p className="text-sm text-base-content/60">
                            Choose your preferred service technician
                        </p>
                    </div>
                </div>
            </div>

            <div className="space-y-5 p-5">
                <div className="grid gap-3 sm:grid-cols-2">
                    <label className="input input-bordered flex w-full items-center gap-2">
                        <Search
                            size={18}
                            className="text-base-content/50"
                        />

                        <input
                            type="text"
                            className="grow"
                            placeholder="Search technicians..."
                            value={search}
                            onChange={(event) =>
                                setSearch(event.target.value)
                            }
                        />
                    </label>

                    <select
                        className="select select-bordered w-full"
                        value={serviceTypeFilter}
                        onChange={(event) =>
                            setServiceTypeFilter(event.target.value)
                        }
                    >
                        <option value="">All Service Types</option>

                        {serviceOptions.map((service) => (
                            <option key={service.id} value={service.id}>
                                {service.name}
                            </option>
                        ))}
                    </select>
                </div>

                {isLoading ? (
                    <div className="flex min-h-40 items-center justify-center">
                        <span className="loading loading-spinner loading-md text-primary" />
                    </div>
                ) : filteredTechnicians.length === 0 ? (
                    <div className="flex min-h-40 flex-col items-center justify-center gap-3 text-center">
                        <AlertCircle
                            size={28}
                            className="text-base-content/40"
                        />

                        <p className="text-sm text-base-content/60">
                            No technicians found.
                        </p>
                    </div>
                ) : (
                    <div className="grid gap-3 sm:grid-cols-2">
                        {filteredTechnicians.map((technician) => {
                            const isSelected =
                                selectedTechnicianId === technician.id;

                            return (
                                <button
                                    key={technician.id}
                                    type="button"
                                    onClick={() => onSelect(technician.id)}
                                    aria-pressed={isSelected}
                                    className={`rounded-xl border p-4 text-left transition-all ${isSelected
                                        ? "border-primary bg-primary/5 ring-1 ring-primary"
                                        : "border-base-300 hover:border-primary/50 hover:shadow-md"
                                        }`}
                                >
                                    <div className="flex items-center justify-between gap-3">
                                        <div className="flex items-center gap-3">
                                            <div className="flex h-11 w-11 items-center justify-center rounded-full bg-primary/10 text-primary">
                                                <UserRound size={21} />
                                            </div>

                                            <div>
                                                <h3 className="font-semibold text-base-content">
                                                    {technician.fullName}
                                                </h3>

                                                <p className="text-xs text-base-content/60">
                                                    {technician.qualifications.length}{" "}
                                                    qualifications
                                                </p>
                                            </div>
                                        </div>

                                        {isSelected && (
                                            <CheckCircle2
                                                size={20}
                                                className="text-primary"
                                            />
                                        )}
                                    </div>

                                    <div className="mt-4 flex flex-wrap gap-2">
                                        {technician.qualifications.map(
                                            (qualification) => (
                                                <span
                                                    key={qualification.serviceTypeId}
                                                    className="badge badge-outline badge-sm"
                                                >
                                                    {qualification.serviceName}
                                                </span>
                                            ),
                                        )}
                                    </div>
                                </button>
                            );
                        })}
                    </div>
                )}
            </div>
        </section>
    );
}

export default function BookingWizard({
    dealership,
    serviceTypes,
    customerId,
    vehicleId,
    onBookingSuccess,
}: BookingWizardProps) {
    const [flow, setFlow] = useState<BookingFlow | null>(null);
    const [stepIndex, setStepIndex] = useState(0);

    const [booking, setBooking] = useState<BookingState>({
        serviceTypeId: null,
        selectedDate: null,
        technicianId: null,
        startTime: null,
    });

    const {
        data: technicianData,
        isLoading: isLoadingTechnicians,
        isError: isTechniciansError,
        refetch: refetchTechnicians,
    } = useGetDealershipTechniciansQuery(dealership.id);

    const technicians = technicianData?.items ?? [];

    const steps = flow ? BOOKING_FLOW_STEPS[flow] : [];
    const currentStep = steps[stepIndex];

    const selectedService = serviceTypes.find(
        (service) => service.id === booking.serviceTypeId,
    );

    const selectedTechnician = technicians.find(
        (technician) => technician.id === booking.technicianId,
    );

    const qualifiedServiceTypeIds =
        flow === "TECHNICIAN_FIRST" && selectedTechnician
            ? selectedTechnician.qualifications.map(
                (qualification) => qualification.serviceTypeId,
            )
            : undefined;

    function selectFlow(selectedFlow: BookingFlow) {
        setFlow(selectedFlow);
        setStepIndex(0);

        setBooking({
            serviceTypeId: null,
            selectedDate: null,
            technicianId: null,
            startTime: null,
        });
    }

    function selectServiceType(serviceTypeId: string) {
        const service = serviceTypes.find((item) => item.id === serviceTypeId);
        if (!service) return;
        setBooking((previous) => ({
            ...previous,
            serviceTypeId,
            technicianId:
                flow === "TECHNICIAN_FIRST"
                    ? previous.technicianId
                    : null,
            startTime: null,
        }));
    }

    function selectDate(date: string) {
        setBooking((previous) => ({
            ...previous,
            selectedDate: date,
            startTime: null,
        }));
    }

    function selectTechnician(technicianId: string) {
        setBooking((previous) => ({
            ...previous,
            technicianId,
            serviceTypeId: null,
            startTime: null,
        }));
    }

    function selectSlot(
        technicianId: string,
        startTime: string,
    ) {
        setBooking((previous) => ({
            ...previous,
            technicianId,
            startTime,
        }));
    }

    const canContinue = (() => {
        switch (currentStep) {
            case "SERVICE_TYPE":
                return Boolean(booking.serviceTypeId);

            case "CALENDAR":
                return Boolean(booking.selectedDate);

            case "TECHNICIAN":
                return Boolean(booking.technicianId);

            case "AVAILABILITY":
                return Boolean(
                    booking.technicianId && booking.startTime,
                );

            case "CONFIRMATION":
                return false;

            default:
                return false;
        }
    })();

    function nextStep() {
        if (!canContinue) return;

        setStepIndex((previous) =>
            Math.min(previous + 1, steps.length - 1),
        );
    }

    function previousStep() {
        setStepIndex((previous) =>
            Math.max(previous - 1, 0),
        );
    }

    if (!flow) {
        const flowOptions: {
            id: BookingFlow;
            title: string;
            description: string;
            icon: typeof Wrench;
        }[] = [
                {
                    id: "SERVICE_FIRST",
                    title: "Book by Service",
                    description:
                        "Choose a service, then find a suitable date and technician.",
                    icon: Wrench,
                },
                {
                    id: "DATE_FIRST",
                    title: "Book by Date",
                    description:
                        "Select your preferred date, then explore available services.",
                    icon: CalendarDays,
                },
                {
                    id: "TECHNICIAN_FIRST",
                    title: "Book by Technician",
                    description:
                        "Choose your preferred technician and find their available slots.",
                    icon: UserRound,
                },
            ];

        return (
            <section className="rounded-xl border border-base-300 bg-base-100 p-5 shadow-sm">
                <div className="mb-6">
                    <h2 className="text-xl font-bold text-base-content">
                        How would you like to book?
                    </h2>

                    <p className="mt-1 text-sm text-base-content/60">
                        Choose your preferred booking method at{" "}
                        {dealership.name}.
                    </p>
                </div>

                <div className="grid gap-4 md:grid-cols-3">
                    {flowOptions.map((option) => {
                        const Icon = option.icon;

                        return (
                            <button
                                key={option.id}
                                type="button"
                                onClick={() => selectFlow(option.id)}
                                className="group rounded-xl border border-base-300 p-5 text-left transition-all hover:-translate-y-0.5 hover:border-primary hover:bg-primary/5 hover:shadow-md"
                            >
                                <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10 text-primary group-hover:bg-primary group-hover:text-primary-content">
                                    <Icon size={24} />
                                </div>

                                <h3 className="font-semibold text-base-content">
                                    {option.title}
                                </h3>

                                <p className="mt-2 text-sm text-base-content/60">
                                    {option.description}
                                </p>
                            </button>
                        );
                    })}
                </div>
            </section>
        );
    }

    return (
        <div className="space-y-6">
            <div className="rounded-xl border border-base-300 bg-base-100 p-5 shadow-sm">
                <div className="flex flex-wrap items-center justify-between gap-3">
                    <div>
                        <p className="text-xs font-medium uppercase tracking-wide text-primary">
                            Booking Appointment
                        </p>

                        <h2 className="mt-1 text-lg font-bold text-base-content">
                            {dealership.name}
                        </h2>
                    </div>

                    <button
                        type="button"
                        className="btn btn-ghost btn-sm"
                        onClick={() => {
                            setFlow(null);
                            setStepIndex(0);
                                            }}
                    >
                        Change booking method
                    </button>
                </div>

                <div className="mt-6 flex flex-wrap items-center gap-2">
                    {steps.map((step, index) => {
                        const isCurrent = index === stepIndex;
                        const isCompleted = index < stepIndex;

                        return (
                            <div
                                key={step}
                                className={`flex items-center gap-2 rounded-full px-3 py-2 text-xs font-medium ${isCurrent
                                    ? "bg-primary text-primary-content"
                                    : isCompleted
                                        ? "bg-success/10 text-success"
                                        : "bg-base-200 text-base-content/50"
                                    }`}
                            >
                                {isCompleted ? (
                                    <CheckCircle2 size={14} />
                                ) : (
                                    <span>{index + 1}</span>
                                )}

                                <span>{STEP_LABELS[step]}</span>
                            </div>
                        );
                    })}
                </div>
            </div>

            {currentStep === "TECHNICIAN" && (
                isTechniciansError ? (
                    <div className="rounded-xl border border-error/30 bg-base-100 p-6">
                        <p className="text-error">
                            Unable to load technicians.
                        </p>

                        <button
                            type="button"
                            className="btn btn-outline btn-primary mt-3"
                            onClick={() => refetchTechnicians()}
                        >
                            Try Again
                        </button>
                    </div>
                ) : (
                    <TechnicianSelector
                        technicians={technicians}
                        selectedTechnicianId={booking.technicianId}
                        onSelect={selectTechnician}
                        isLoading={isLoadingTechnicians}
                    />
                )
            )}

            {currentStep === "SERVICE_TYPE" && (
                <ServiceTypeSelector
                    serviceTypes={serviceTypes}
                    selectedServiceTypeId={booking.serviceTypeId}
                    qualifiedServiceTypeIds={qualifiedServiceTypeIds}
                    onSelect={selectServiceType}
                />
            )}

            {currentStep === "CALENDAR" && (
                <BookingCalendar
                    workingHours={dealership.workingHours}
                    dateOverrides={dealership.dateOverrides}
                    timeZone={dealership.timeZone}
                    selectedDate={booking.selectedDate}
                    onSelectDate={selectDate}
                />
            )}

            {currentStep === "AVAILABILITY" &&
                booking.serviceTypeId &&
                booking.selectedDate && (
                    <TechnicianAvailability
                        dealershipId={dealership.id}
                        serviceTypeId={booking.serviceTypeId}
                        selectedDate={booking.selectedDate}
                        technicianId={
                            flow === "TECHNICIAN_FIRST"
                                ? booking.technicianId
                                : undefined
                        }
                        selectedTechnicianId={booking.technicianId}
                        selectedStartTime={booking.startTime}
                        onSelectSlot={selectSlot}
                    />
                )}

            {currentStep === "CONFIRMATION" &&
                booking.serviceTypeId &&
                booking.selectedDate &&
                booking.technicianId &&
                booking.startTime &&
                selectedService && (
                    <BookingConfirmation
                        customerId={customerId}
                        vehicleId={vehicleId}
                        dealership={{
                            id: dealership.id,
                            name: dealership.name,
                            timeZone: dealership.timeZone,
                        }}
                        serviceType={selectedService}
                        technician={{
                            id: booking.technicianId,
                            fullName:
                                selectedTechnician?.fullName ??
                                "Selected Technician",
                        }}
                        selectedDate={booking.selectedDate}
                        startTime={booking.startTime}
                        onBack={previousStep}
                        onSuccess={onBookingSuccess}
                    />
                )}

            {currentStep !== "CONFIRMATION" && (
                <div className="flex items-center justify-between gap-3">
                    <button
                        type="button"
                        className="btn btn-outline"
                        onClick={() => {
                            if (stepIndex === 0) {
                                setFlow(null);
                            } else {
                                previousStep();
                            }
                        }}
                    >
                        <ArrowLeft size={17} />
                        Back
                    </button>

                    <button
                        type="button"
                        className="btn btn-primary"
                        disabled={!canContinue}
                        onClick={nextStep}
                    >
                        Continue
                        <ArrowRight size={17} />
                    </button>
                </div>
            )}
        </div>
    );
}

