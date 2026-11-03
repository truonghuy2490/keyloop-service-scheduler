// features/appointments/components/booking-page-content.tsx

"use client";

import { useEffect, useState } from "react";
import Image from "next/image";
import { skipToken } from "@reduxjs/toolkit/query";
import {
    AlertCircle,
    Building2,
    CarFront,
    CheckCircle2,
    RefreshCw,
    UserRound,
} from "lucide-react";

import { useAppSelector } from "@/lib/redux/hooks";
import { useGetDealershipByIdQuery } from "@/features/dealerships/dealership.api";
import { useGetVehiclesQuery } from "@/features/vehicles/vehicle.api";
import { useAllDealershipServiceTypes } from "@/features/service-types/hooks/use-all-dealership-service-types";
import BookingWizard from "./booking-wizard";


interface BookingPageContentProps {
    dealershipId: string;
}

export default function BookingPageContent({
    dealershipId,
}: BookingPageContentProps) {
    const currentCustomer = useAppSelector(
        (state) => state.customer.currentCustomer,
    );

    const [selectedVehicleId, setSelectedVehicleId] =
        useState<string | null>(null);

    const {
        data: dealership,
        isLoading: isLoadingDealership,
        isError: isDealershipError,
        refetch: refetchDealership,
    } = useGetDealershipByIdQuery(dealershipId);

    const {
        data: vehicleData,
        isLoading: isLoadingVehicles,
        isError: isVehiclesError,
        refetch: refetchVehicles,
    } = useGetVehiclesQuery(
        currentCustomer
            ? {
                customerId: currentCustomer.id,
                pageNumber: 1,
                pageSize: 100,
            }
            : skipToken,
    );

    const {
        serviceTypes,
        isLoading: isLoadingServiceTypes,
        isError: isServiceTypesError,
        refetch: refetchServiceTypes,
    } = useAllDealershipServiceTypes(dealershipId);

    const vehicles = vehicleData?.items ?? [];

    // Reset selected vehicle when customer profile changes.
    useEffect(() => {
        setSelectedVehicleId(null);
    }, [currentCustomer?.id]);

    // Verify the selected vehicle still belongs to the current result.
    const selectedVehicle = vehicles.find(
        (vehicle) => vehicle.id === selectedVehicleId,
    );

    const isLoading =
        isLoadingDealership ||
        (currentCustomer !== null && isLoadingVehicles);

    if (isLoading) {
        return (
            <div className="flex min-h-60 items-center justify-center">
                <span className="loading loading-spinner loading-lg text-primary" />
            </div>
        );
    }

    if (isDealershipError || isVehiclesError) {
        return (
            <div className="rounded-xl border border-error/30 bg-base-100 p-6">
                <div className="flex items-start gap-3">
                    <AlertCircle size={22} className="shrink-0 text-error" />

                    <div className="flex-1">
                        <h2 className="font-semibold text-base-content">
                            Unable to load booking information
                        </h2>

                        <p className="mt-1 text-sm text-base-content/60">
                            Please check your connection and try again.
                        </p>

                        <button
                            type="button"
                            className="btn btn-outline btn-primary btn-sm mt-4"
                            onClick={() => {
                                if (isDealershipError) refetchDealership();

                                if (isVehiclesError && currentCustomer) {
                                    refetchVehicles();
                                }

                            }}
                        >
                            <RefreshCw size={16} />
                            Retry
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    if (!dealership) {
        return (
            <div className="rounded-xl border border-base-300 bg-base-100 p-8 text-center">
                <Building2
                    size={36}
                    className="mx-auto mb-3 text-base-content/40"
                />

                <h2 className="font-semibold">
                    Dealership not found
                </h2>
            </div>
        );
    }

    if (!currentCustomer) {
        return (
            <div className="rounded-xl border border-warning/30 bg-base-100 p-8 text-center">
                <UserRound
                    size={36}
                    className="mx-auto mb-3 text-warning"
                />

                <h2 className="text-lg font-semibold">
                    Select a Customer Profile
                </h2>

                <p className="mt-2 text-sm text-base-content/60">
                    Please select your customer profile from the navbar
                    before booking an appointment.
                </p>
            </div>
        );
    }

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-bold text-base-content">
                    Book Service Appointment
                </h1>

                <p className="mt-2 text-sm text-base-content/60">
                    Schedule your vehicle service at {dealership.name}.
                </p>
            </div>

            {/* Current customer */}
            <section className="rounded-xl border border-base-300 bg-base-100 p-5 shadow-sm">
                <div className="flex items-center gap-3">
                    <div className="flex size-11 items-center justify-center rounded-xl bg-primary/10 text-primary">
                        <UserRound size={22} />
                    </div>

                    <div>
                        <h2 className="font-semibold text-base-content">
                            Customer Profile
                        </h2>

                        <p className="text-sm text-base-content/60">
                            Currently selected customer
                        </p>
                    </div>
                </div>

                <div className="mt-4 rounded-lg bg-base-200/50 p-4">
                    <p className="font-semibold">
                        {currentCustomer.fullName}
                    </p>

                    <p className="text-sm text-base-content/60">
                        {currentCustomer.email}
                    </p>
                </div>
            </section>

            {/* Vehicle selection */}
            <section className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
                <div className="border-b border-base-300 p-5">
                    <div className="flex items-center gap-3">
                        <div className="flex size-11 items-center justify-center rounded-xl bg-primary/10 text-primary">
                            <CarFront size={22} />
                        </div>

                        <div>
                            <h2 className="font-semibold text-base-content">
                                Select Your Vehicle
                            </h2>

                            <p className="text-sm text-base-content/60">
                                Choose the vehicle you want to service
                            </p>
                        </div>
                    </div>
                </div>

                <div className="p-5">
                    {vehicles.length === 0 ? (
                        <div className="rounded-xl border border-dashed border-base-300 p-8 text-center">
                            <CarFront
                                size={32}
                                className="mx-auto mb-3 text-base-content/40"
                            />

                            <p className="font-medium">
                                No vehicles available
                            </p>

                            <p className="mt-1 text-sm text-base-content/60">
                                No vehicles are associated with this customer.
                            </p>
                        </div>
                    ) : (
                        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                            {vehicles.map((vehicle) => {
                                const isSelected =
                                    selectedVehicleId === vehicle.id;

                                return (
                                    <button
                                        key={vehicle.id}
                                        type="button"
                                        onClick={() =>
                                            setSelectedVehicleId(vehicle.id)
                                        }
                                        aria-pressed={isSelected}
                                        className={`overflow-hidden rounded-xl border text-left transition-all ${isSelected
                                            ? "border-primary bg-primary/5 ring-1 ring-primary"
                                            : "border-base-300 hover:border-primary/50 hover:shadow-md"
                                            }`}
                                    >
                                        <div className="relative aspect-video bg-base-200">
                                            <Image
                                                src={
                                                    vehicle.imageUrl ||
                                                    "/images/sample-vehicle.jpg"
                                                }
                                                alt={`${vehicle.make} ${vehicle.model}`}
                                                fill
                                                sizes="(max-width: 640px) 100vw, 50vw"
                                                className="object-cover"
                                            />
                                        </div>

                                        <div className="p-4">
                                            <div className="flex items-start justify-between gap-2">
                                                <div>
                                                    <p className="text-xs text-base-content/50">
                                                        {vehicle.registrationNumber}
                                                    </p>

                                                    <h3 className="mt-1 font-semibold">
                                                        {vehicle.make} {vehicle.model}
                                                    </h3>

                                                    <p className="text-sm text-base-content/60">
                                                        {vehicle.modelYear ?? "Year not specified"}
                                                    </p>
                                                </div>

                                                {isSelected && (
                                                    <CheckCircle2
                                                        size={21}
                                                        className="shrink-0 text-primary"
                                                    />
                                                )}
                                            </div>
                                        </div>
                                    </button>
                                );
                            })}
                        </div>
                    )}
                </div>
            </section>

            {/* Service type errors are local to the booking wizard. */}
            {selectedVehicle && isServiceTypesError && (
                <div className="rounded-xl border border-error/30 bg-base-100 p-5">
                    <p className="text-sm text-error">Unable to load service types.</p>
                    <button
                        type="button"
                        className="btn btn-outline btn-primary btn-sm mt-3"
                        onClick={() => refetchServiceTypes()}
                    >
                        <RefreshCw size={16} /> Retry service types
                    </button>
                </div>
            )}

            {selectedVehicle && isLoadingServiceTypes && (
                <div className="flex min-h-40 items-center justify-center">
                    <span className="loading loading-spinner loading-md text-primary" />
                </div>
            )}

            {/* Booking wizard */}
            {selectedVehicle && !isServiceTypesError && !isLoadingServiceTypes && (
                <BookingWizard
                    key={`${currentCustomer.id}-${selectedVehicle.id}-${dealership.id}`}
                    dealership={{
                        id: dealership.id,
                        name: dealership.name,
                        timeZone: dealership.timeZone,
                        workingHours: dealership.workingHours,
                        dateOverrides: dealership.dateOverrides,
                    }}
                    serviceTypes={serviceTypes}
                    customerId={currentCustomer.id}
                    vehicleId={selectedVehicle.id}
                />
            )}
        </div>
    );
}
