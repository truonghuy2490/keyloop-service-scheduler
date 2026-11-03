
"use client";

import { useEffect, useMemo, useState } from "react";
import { skipToken } from "@reduxjs/toolkit/query";
import {
    CalendarDays,
    CarFront,
    ChevronDown,
    ChevronUp,
    Clock3,
    MapPin,
    RefreshCw,
    Wrench,
    AlertCircle,
    Inbox,
    ArrowDownUp,
    Ban,
    UserRound,
    Plus,
} from "lucide-react";

import { useAppSelector } from "@/lib/redux/hooks";
import { useGetCustomerAppointmentsQuery } from "@/features/appointments/appointment.api";
import type { CustomerAppointment } from "@/features/appointments/appointment.types";
import Pagination from "@/features/shared/components/ui/Pagination";
import ConfirmModal from "@/features/shared/components/ui/ConfirmModal";
import Link from "next/link";

const PAGE_SIZE = 10;
type AppointmentSort = "upcoming" | "newest" | "oldest";

function sortAppointments(
    items: CustomerAppointment[],
    sort: AppointmentSort,
    now: number,
): CustomerAppointment[] {
    return [...items].sort((a, b) => {
        const aTime = Date.parse(a.schedule.startTime);
        const bTime = Date.parse(b.schedule.startTime);

        if (sort === "newest") return bTime - aTime;
        if (sort === "oldest") return aTime - bTime;

        const aUpcoming = aTime >= now;
        const bUpcoming = bTime >= now;
        if (aUpcoming !== bUpcoming) return aUpcoming ? -1 : 1;
        return aUpcoming ? aTime - bTime : bTime - aTime;
    });
}

function formatDateTime(value: string) {
    return new Intl.DateTimeFormat("en-GB", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        hour12: false,
    }).format(new Date(value));
}

function formatTime(value: string) {
    return new Intl.DateTimeFormat("en-GB", {
        hour: "2-digit",
        minute: "2-digit",
        hour12: false,
    }).format(new Date(value));
}

function getStatusClass(status: string) {
    switch (status.toUpperCase()) {
        case "CONFIRMED":
            return "badge-success badge-outline";
        case "PENDING":
            return "badge-warning badge-outline";
        case "COMPLETED":
            return "badge-info badge-outline";
        case "CANCELLED":
            return "badge-error badge-outline";
        default:
            return "badge-ghost";
    }
}

function AppointmentRow({
    appointment,
    onCancelClick,
}: {
    appointment: CustomerAppointment;
    onCancelClick: (appointment: CustomerAppointment) => void;
}) {
    const [expanded, setExpanded] = useState(false);

    const { vehicle, dealership, service, schedule } = appointment;

    return (
        <article className="border-b border-base-300 last:border-b-0">
            <button
                type="button"
                onClick={() => setExpanded((previous) => !previous)}
                aria-expanded={expanded}
                className={`flex w-full flex-col gap-3 px-5 py-4 text-left transition-colors hover:bg-base-200/60 md:flex-row md:items-center md:gap-5 ${expanded ? "bg-primary/5" : ""
                    }`}
            >
                <div className="flex min-w-0 flex-1 items-start gap-3">
                    <div className="flex size-10 shrink-0 items-center justify-center rounded-full bg-primary/10 text-primary">
                        <Wrench size={19} />
                    </div>

                    <div className="min-w-0 flex-1">
                        <div className="flex flex-wrap items-center gap-2">
                            <h3 className="truncate font-semibold text-base-content">
                                {service.name}
                            </h3>

                            <span
                                className={`badge badge-sm ${getStatusClass(appointment.status)}`}
                            >
                                {appointment.status}
                            </span>
                        </div>

                        <p className="mt-1 truncate text-sm text-base-content/70">
                            {vehicle.make} {vehicle.model}
                            <span className="mx-2 text-base-content/30">·</span>
                            {vehicle.registrationNumber}
                        </p>

                        <p className="mt-1 truncate text-xs text-base-content/50">
                            {dealership.name}
                        </p>
                    </div>
                </div>

                <div className="flex items-center justify-between gap-3 pl-13 md:justify-end md:pl-0">
                    <div className="text-left md:text-right">
                        <p className="text-sm font-medium text-base-content">
                            {formatDateTime(schedule.startTime)}
                        </p>

                        <p className="mt-1 text-xs text-base-content/50">
                            {service.durationMinutes} minutes
                        </p>
                    </div>

                    {expanded ? (
                        <ChevronUp size={18} className="shrink-0 text-base-content/50" />
                    ) : (
                        <ChevronDown size={18} className="shrink-0 text-base-content/50" />
                    )}
                </div>
            </button>

            {expanded && (
                <div className="border-t border-base-300 bg-base-200/30 px-5 py-5">
                    <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
                        <div className="space-y-2">
                            <div className="flex items-center gap-2 text-sm font-semibold">
                                <CarFront size={17} className="text-primary" />
                                Vehicle
                            </div>

                            <p className="text-sm">
                                {vehicle.make} {vehicle.model}
                            </p>

                            <p className="text-sm text-base-content/60">
                                Registration: {vehicle.registrationNumber}
                            </p>
                        </div>

                        <div className="space-y-2">
                            <div className="flex items-center gap-2 text-sm font-semibold">
                                <MapPin size={17} className="text-primary" />
                                Dealership
                            </div>

                            <p className="text-sm">{dealership.name}</p>
                        </div>

                        <div className="space-y-2">
                            <div className="flex items-center gap-2 text-sm font-semibold">
                                <Wrench size={17} className="text-primary" />
                                Service
                            </div>

                            <p className="text-sm">{service.name}</p>

                            <p className="text-sm text-base-content/60">
                                Duration: {service.durationMinutes} minutes
                            </p>
                        </div>

                        <div className="space-y-2">
                            <div className="flex items-center gap-2 text-sm font-semibold">
                                <CalendarDays size={17} className="text-primary" />
                                Appointment Schedule
                            </div>

                            <p className="text-sm">
                                Start: {formatDateTime(schedule.startTime)}
                            </p>

                            <p className="text-sm">
                                End: {formatDateTime(schedule.endTime)}
                            </p>
                        </div>

                        <div className="space-y-2">
                            <div className="flex items-center gap-2 text-sm font-semibold">
                                <Clock3 size={17} className="text-primary" />
                                Appointment Status
                            </div>

                            <span
                                className={`badge ${getStatusClass(appointment.status)}`}
                            >
                                {appointment.status}
                            </span>
                        </div>

                        <div className="space-y-2">
                            <p className="text-sm font-semibold">Appointment ID</p>

                            <p className="break-all font-mono text-xs text-base-content/60">
                                {appointment.id}
                            </p>
                        </div>
                    </div>

                    {!(["CANCELLED", "COMPLETED"].includes(appointment.status.toUpperCase())) && (
                        <div className="mt-5 flex justify-end border-t border-base-300 pt-4">
                            <button
                                type="button"
                                className="btn btn-outline btn-error btn-sm gap-2"
                                onClick={() => onCancelClick(appointment)}
                                aria-label={`Cancel appointment for ${service.name}`}
                            >
                                <Ban size={15} />
                                Cancel Appointment
                            </button>
                        </div>
                    )}
                </div>
            )}
        </article>
    );
}

export default function CustomerAppointmentList() {
    const currentCustomer = useAppSelector(
        (state) => state.customer.currentCustomer,
    );

    const [showProfileModal, setShowProfileModal] = useState(true);
    const [pageNumber, setPageNumber] = useState(1);
    const [sort, setSort] = useState<AppointmentSort>("upcoming");
    const [cancelNoticeAppointment, setCancelNoticeAppointment] = useState<CustomerAppointment | null>(null);
    const now = Date.now();

    useEffect(() => {
        setPageNumber(1);
    }, [currentCustomer?.id]);

    const {
        currentData: data,
        isLoading,
        isFetching,
        isError,
        refetch,
    } = useGetCustomerAppointmentsQuery(
        currentCustomer
            ? {
                customerId: currentCustomer.id,
                pageNumber,
                pageSize: PAGE_SIZE,
            }
            : skipToken,
        {
            refetchOnMountOrArgChange: true,
        },
    );

    // Hooks must be called before any conditional return.
    // Sorting is limited to the appointments on the current server page.
    const appointments = useMemo(
        () => sortAppointments(data?.items ?? [], sort, now),
        [data?.items, sort, now],
    );

    if (!currentCustomer) {
        return (
            <>
                <section className="flex min-h-80 flex-col items-center justify-center rounded-2xl border border-base-300 bg-base-100 p-8 text-center shadow-sm">
                    <div className="flex size-16 items-center justify-center rounded-full bg-primary/10 text-primary">
                        <UserRound size={30} />
                    </div>
                    <h2 className="mt-5 text-xl font-bold">No Customer Profile Selected</h2>
                    <p className="mt-2 max-w-md text-sm text-base-content/60">
                        Please select a customer profile from the navigation bar
                        to view your appointments.
                    </p>
                    <button
                        type="button"
                        className="btn btn-primary mt-5"
                        onClick={() => setShowProfileModal(true)}
                    >
                        How to Select a Profile
                    </button>
                </section>

                <ConfirmModal
                    isOpen={showProfileModal}
                    onClose={() => setShowProfileModal(false)}
                    variant="info"
                    title="Select a Customer Profile"
                    body={
                        <div className="space-y-3">
                            <p>
                                To view your service appointments, please select a
                                customer profile first.
                            </p>
                            <p>
                                Click the profile icon in the top-right corner
                                of the navigation bar to choose your profile.
                            </p>
                        </div>
                    }
                    yesButtonLabel="Got It"
                    noButtonLabel="Not Now"
                    onYes={() => setShowProfileModal(false)}
                    onNo={() => setShowProfileModal(false)}
                    modalTestId="dlgSelectCustomerProfile"
                />
            </>
        );
    }

    if (isLoading || (isFetching && !data)) {
        return (
            <div className="flex min-h-60 items-center justify-center">
                <span className="loading loading-spinner loading-lg text-primary" />
            </div>
        );
    }

    if (isError) {
        return (
            <div className="alert alert-error">
                <AlertCircle size={22} />

                <div className="flex-1">
                    <h3 className="font-semibold">Unable to load appointments</h3>

                    <p className="text-sm">
                        Something went wrong while retrieving your appointments.
                    </p>
                </div>

                <button
                    type="button"
                    className="btn btn-sm btn-outline"
                    onClick={() => refetch()}
                >
                    <RefreshCw size={15} />
                    Retry
                </button>
            </div>
        );
    }

    return (
        <section className="space-y-5">
            <div className="flex flex-wrap items-center justify-between gap-3">
                <div>
                    <h2 className="text-xl font-bold text-base-content">
                        My Appointments
                    </h2>

                    <p className="mt-1 text-sm text-base-content/60">
                        Service appointments belonging to {currentCustomer.fullName}
                    </p>
                </div>

                <div className="flex flex-wrap items-center gap-3">
                    <Link
                        href="/appointments/book"
                        className="btn btn-primary btn-sm gap-2"
                    >
                        <Plus size={16} />
                        Book New Appointment
                    </Link>
                    <label className="flex items-center gap-2 text-sm text-base-content/70">
                        <ArrowDownUp size={16} aria-hidden="true" />
                        <span className="sr-only">Sort appointments</span>
                        <select
                            className="select select-bordered select-sm"
                            aria-label="Sort appointments"
                            value={sort}
                            onChange={(event) =>
                                setSort(event.target.value as AppointmentSort)
                            }
                        >
                            <option value="upcoming">Upcoming first</option>
                            <option value="newest">Newest date first</option>
                            <option value="oldest">Oldest date first</option>
                        </select>
                    </label>
                    <span className="badge badge-primary badge-outline">
                        {data?.totalCount ?? 0} appointments
                    </span>

                    <button
                        type="button"
                        className="btn btn-ghost btn-sm btn-square"
                        onClick={() => refetch()}
                        disabled={isFetching}
                        aria-label="Refresh appointments"
                    >
                        <RefreshCw
                            size={17}
                            className={isFetching ? "animate-spin" : ""}
                        />
                    </button>
                </div>
            </div>

            {appointments.length === 0 ? (
                <div className="flex min-h-60 flex-col items-center justify-center rounded-xl border border-dashed border-base-300 bg-base-100 p-8 text-center">
                    <Inbox size={36} className="mb-4 text-base-content/40" />

                    <h3 className="text-lg font-semibold">
                        No appointments found
                    </h3>

                    <p className="mt-2 max-w-sm text-sm text-base-content/60">
                        You have no service appointments associated with your
                        selected profile.
                    </p>
                </div>
            ) : (
                <div className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
                    <div className="flex items-center justify-between border-b border-base-300 bg-base-200/40 px-5 py-3">
                        <span className="text-xs font-medium uppercase tracking-wider text-base-content/60">
                            Appointments
                        </span>

                        <span className="text-xs text-base-content/50">
                            Select an appointment to view details
                        </span>
                    </div>

                    <div>
                        {appointments.map((appointment) => (
                            <AppointmentRow
                                key={appointment.id}
                                appointment={appointment}
                                onCancelClick={setCancelNoticeAppointment}
                            />
                        ))}
                    </div>
                </div>
            )}

            <ConfirmModal
                isOpen={cancelNoticeAppointment !== null}
                onClose={() => setCancelNoticeAppointment(null)}
                variant="info"
                title="Cancellation Coming Soon"
                body={
                    <div className="space-y-3">
                        <p>
                            Online appointment cancellation is currently under development.
                            This feature will be available soon.
                        </p>
                        {cancelNoticeAppointment && (
                            <p className="rounded-lg bg-base-200 p-3 text-sm">
                                <span className="font-semibold">Appointment:</span>{" "}
                                {cancelNoticeAppointment.service.name} — {cancelNoticeAppointment.vehicle.make}{" "}
                                {cancelNoticeAppointment.vehicle.model}
                            </p>
                        )}
                        <p>
                            If you need to cancel or change your appointment now, please
                            contact the dealership directly.
                        </p>
                    </div>
                }
                modalTestId="dlgCancellationComingSoon"
                confirmBtnTestId="btnDismissCancellationNotice"
            />

            {data && data.totalPages > 1 && (
                <Pagination
                    pageNumber={data.pageNumber}
                    pageSize={data.pageSize}
                    totalPages={data.totalPages}
                    totalCount={data.totalCount}
                    hasPreviousPage={data.hasPreviousPage}
                    hasNextPage={data.hasNextPage}
                    onPageChange={setPageNumber}
                    btnPreviousPageTestId="btnPreviousAppointmentPage"
                    btnNextPageTestId="btnNextAppointmentPage"
                    btnCurrentPageTestId="btnCurrentAppointmentPage"
                />
            )}
        </section>
    );
}
