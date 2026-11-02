"use client";

import { useEffect } from "react";
import Image from "next/image";
import { skipToken } from "@reduxjs/toolkit/query";
import { CarFront, RefreshCw } from "lucide-react";

import { useAppDispatch, useAppSelector } from "@/lib/redux/hooks";
import { useGetVehiclesQuery } from "@/features/vehicles/vehicle.api";
import {
  enqueueToast,
  ToastType,
} from "@/features/shared/toast.slice";

export default function UserVehicleGrid() {
  const dispatch = useAppDispatch();

  // Get selected customer from Redux Persist
  const currentCustomer = useAppSelector(
    (state) => state.customer.currentCustomer,
  );

  // Show toast if no customer is selected
  useEffect(() => {
    if (!currentCustomer) {
      dispatch(
        enqueueToast({
          message: "Please select a profile first to view your vehicles.",
          type: ToastType.Error,
        }),
      );
    }
  }, [currentCustomer, dispatch]);

  // Do not call API if no customer is selected
  const {
    data,
    isLoading,
    isFetching,
    isError,
    refetch,
  } = useGetVehiclesQuery(
    currentCustomer
      ? {
          customerId: currentCustomer.id,
          pageNumber: 1,
          pageSize: 100,
        }
      : skipToken,
  );

  // No selected profile
  if (!currentCustomer) {
    return null;
  }

  // Loading
  if (isLoading) {
    return (
      <div className="flex min-h-60 items-center justify-center">
        <span className="loading loading-spinner loading-lg text-primary" />
      </div>
    );
  }

  // API error
  if (isError) {
    return (
      <div className="alert alert-error">
        <div className="flex-1">
          <h3 className="font-semibold">Unable to load vehicles</h3>
          <p className="text-sm">
            Something went wrong while retrieving your vehicles.
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

  const vehicles = data?.items ?? [];

  // Empty state
  if (vehicles.length === 0) {
    return (
      <div className="flex min-h-60 flex-col items-center justify-center rounded-xl border border-dashed border-base-300 bg-base-100 p-8 text-center">
        <div className="mb-4 flex size-16 items-center justify-center rounded-full bg-base-200">
          <CarFront size={30} className="text-base-content/50" />
        </div>

        <h3 className="text-lg font-semibold">No vehicles found</h3>

        <p className="mt-1 max-w-sm text-sm text-base-content/60">
          There are no vehicles associated with{" "}
          <span className="font-medium text-base-content">
            {currentCustomer.fullName}
          </span>
          .
        </p>
      </div>
    );
  }

  return (
    <section className="space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="text-xl font-bold text-base-content">
            My Vehicles
          </h2>

          <p className="mt-1 text-sm text-base-content/60">
            Vehicles registered under {currentCustomer.fullName}
          </p>
        </div>

        <div className="flex items-center gap-2">
          <span className="badge badge-primary badge-outline">
            {vehicles.length} vehicles
          </span>

          {isFetching && (
            <span className="loading loading-spinner loading-sm text-primary" />
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 xl:grid-cols-3">
        {vehicles.map((vehicle) => (
          <article
            key={vehicle.id}
            className="card overflow-hidden border border-base-300 bg-base-100 shadow-sm transition-all duration-200 hover:-translate-y-1 hover:shadow-md"
          >
            <figure className="relative aspect-[16/9] bg-base-200">
              <Image
                src={
                  vehicle.imageUrl ||
                  "/images/sample-vehicle.jpg"
                }
                alt={`${vehicle.make} ${vehicle.model}`}
                fill
                sizes="(max-width: 640px) 100vw, (max-width: 1280px) 50vw, 33vw"
                className="object-cover"
              />
            </figure>

            <div className="card-body gap-3 p-5">
              <div>
                <p className="text-xs font-medium uppercase tracking-wider text-base-content/50">
                  {vehicle.registrationNumber}
                </p>

                <h3 className="mt-1 text-lg font-bold text-base-content">
                  {vehicle.make} {vehicle.model}
                </h3>

                <p className="text-sm text-base-content/60">
                  {vehicle.modelYear ?? "Year not specified"}
                </p>
              </div>

              <div className="divider my-0" />

              <div className="flex items-center justify-between">
                <span className="text-sm text-base-content/60">
                  Vehicle ID
                </span>

                <span
                  className="max-w-36 truncate text-xs font-medium text-base-content/70"
                  title={vehicle.id}
                >
                  {vehicle.id}
                </span>
              </div>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
