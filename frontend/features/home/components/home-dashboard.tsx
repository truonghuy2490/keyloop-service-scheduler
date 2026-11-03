"use client";

import { useMemo } from "react";
import { skipToken } from "@reduxjs/toolkit/query";
import { RefreshCw, UserRound } from "lucide-react";

import { useAppSelector } from "@/lib/redux/hooks";
import { useGetCustomerAppointmentsQuery } from "@/features/appointments/appointment.api";
import { useGetVehiclesQuery } from "@/features/vehicles/vehicle.api";
import { useGetDealershipsQuery } from "@/features/dealerships/dealership.api";

import HomeHero from "@/features/home/components/home-hero";
import NextAppointmentCard from "@/features/home/components/next-appointment-card";
import MyVehiclesCard from "@/features/home/components/my-vehicle-card";
import ExploreDealerships from "@/features/home/components/explore-dealerships";

export default function HomeDashboard() {
    const currentCustomer = useAppSelector(
        (state) => state.customer.currentCustomer,
    );

    const customerId = currentCustomer?.id;

    // Fetch customer appointments
    const {
        data: appointmentData,
        isLoading: isAppointmentsLoading,
        isError: isAppointmentsError,
        refetch: refetchAppointments,
    } = useGetCustomerAppointmentsQuery(
        customerId
            ? {
                customerId,
                pageNumber: 1,
                pageSize: 100,
            }
            : skipToken,
    );

    // Fetch customer vehicles
    const {
        data: vehicleData,
        isLoading: isVehiclesLoading,
        isError: isVehiclesError,
        refetch: refetchVehicles,
    } = useGetVehiclesQuery(
        customerId
            ? {
                customerId,
                pageNumber: 1,
                pageSize: 2,
            }
            : skipToken,
    );

    // Fetch dealerships
    const {
        data: dealershipData,
        isLoading: isDealershipsLoading,
        isError: isDealershipsError,
        refetch: refetchDealerships,
    } = useGetDealershipsQuery({
        pageNumber: 1,
        pageSize: 3,
    });

    // Find the nearest upcoming appointment
    const nextAppointment = useMemo(() => {
        const now = Date.now();

        return [...(appointmentData?.items ?? [])]
            .filter((appointment) => {
                const status = appointment.status.toUpperCase();
                const startTime = Date.parse(
                    appointment.schedule.startTime,
                );

                return (
                    ["CONFIRMED", "PENDING"].includes(status) &&
                    startTime >= now
                );
            })
            .sort(
                (a, b) =>
                    Date.parse(a.schedule.startTime) -
                    Date.parse(b.schedule.startTime),
            )[0];
    }, [appointmentData?.items]);

    const vehicles = vehicleData?.items ?? [];

    const dealerships = dealershipData?.items ?? [];

    const firstName = currentCustomer?.fullName
        ?.trim()
        .split(/\s+/)
        .at(-1);

    return (
        <main className="min-h-screen bg-base-200 p-6 md:p-10">
            <div className="mx-auto max-w-7xl space-y-8">
                {/* Welcome section */}
                <HomeHero customerName={firstName} />

                {/* Customer dashboard */}
                {currentCustomer ? (
                    <div className="grid gap-6 lg:grid-cols-3">
                        {/* Next Appointment */}
                        <div className="lg:col-span-2">
                            {isAppointmentsLoading ? (
                                <div className="flex min-h-64 items-center justify-center rounded-2xl border border-base-300 bg-base-100">
                                    <span className="loading loading-spinner loading-lg text-primary" />
                                </div>
                            ) : isAppointmentsError ? (
                                <div className="flex min-h-64 flex-col items-center justify-center rounded-2xl border border-base-300 bg-base-100 p-6 text-center">
                                    <p className="font-semibold text-error">
                                        Unable to load appointments
                                    </p>

                                    <p className="mt-2 text-sm text-base-content/60">
                                        Something went wrong while retrieving
                                        your appointments.
                                    </p>

                                    <button
                                        type="button"
                                        className="btn btn-outline btn-sm mt-4"
                                        onClick={() => refetchAppointments()}
                                    >
                                        <RefreshCw size={15} />
                                        Retry
                                    </button>
                                </div>
                            ) : (
                                <NextAppointmentCard
                                    appointment={nextAppointment}
                                />
                            )}
                        </div>

                        {/* My Vehicles */}
                        <div>
                            {isVehiclesLoading ? (
                                <div className="flex min-h-64 items-center justify-center rounded-2xl border border-base-300 bg-base-100">
                                    <span className="loading loading-spinner loading-lg text-primary" />
                                </div>
                            ) : isVehiclesError ? (
                                <div className="flex min-h-64 flex-col items-center justify-center rounded-2xl border border-base-300 bg-base-100 p-6 text-center">
                                    <p className="font-semibold text-error">
                                        Unable to load vehicles
                                    </p>

                                    <button
                                        type="button"
                                        className="btn btn-outline btn-sm mt-4"
                                        onClick={() => refetchVehicles()}
                                    >
                                        <RefreshCw size={15} />
                                        Retry
                                    </button>
                                </div>
                            ) : (
                                <MyVehiclesCard vehicles={vehicles} />
                            )}
                        </div>
                    </div>
                ) : (
                    /* No selected customer */
                    <section className="rounded-2xl border border-base-300 bg-base-100 p-8 text-center shadow-sm">
                        <div className="mx-auto flex size-16 items-center justify-center rounded-full bg-primary/10 text-primary">
                            <UserRound size={30} />
                        </div>

                        <h2 className="mt-5 text-xl font-bold">
                            Let's get you started!
                        </h2>

                        <p className="mx-auto mt-2 max-w-md text-sm text-base-content/60">
                            Select a customer profile to view your
                            upcoming appointments and registered vehicles.
                        </p>

                        {/* TODO: Add existing profile selector here */}
                    </section>
                )}

                {/* Dealership discovery */}
                <ExploreDealerships
                    dealerships={dealerships}
                    isLoading={isDealershipsLoading}
                    isError={isDealershipsError}
                    onRetry={refetchDealerships}
                    />
            </div>
        </main>
    );
}
