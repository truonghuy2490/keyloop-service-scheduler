
"use client";

import { useEffect, useRef, useState } from "react";
import { Check, ChevronDown, UserRound } from "lucide-react";

import { useAppDispatch, useAppSelector } from "@/lib/redux/hooks";
import { setCurrentCustomer } from "@/features/customer/customer.slice";
import { useGetCustomersQuery } from "@/features/customer/customer.api";

import {
  enqueueToast,
  ToastType,
} from "@/features/shared/toast.slice";

export default function NavbarProfile() {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const dispatch = useAppDispatch();

  const currentCustomer = useAppSelector(
    (state) => state.customer.currentCustomer,
  );

  const {
    data,
    isLoading,
    isError,
    refetch,
  } = useGetCustomersQuery({
    pageNumber: 1,
    pageSize: 2,
  });

  const customers = data?.items ?? [];

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setIsOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  // Close dropdown when pressing Escape
  useEffect(() => {
    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        setIsOpen(false);
      }
    };

    document.addEventListener("keydown", handleEscape);

    return () => {
      document.removeEventListener("keydown", handleEscape);
    };
  }, []);

  const handleSwitchCustomer = (customerId: string) => {
    const selectedCustomer = customers.find(
      (customer) => customer.id === customerId,
    );

    if (!selectedCustomer) return;

    // Close dropdown without notification if already selected
    if (currentCustomer?.id === selectedCustomer.id) {
      setIsOpen(false);
      return;
    }

    // Update current customer
    dispatch(setCurrentCustomer(selectedCustomer));

    // Close dropdown
    setIsOpen(false);

    // Show success notification
    dispatch(
      enqueueToast({
        type: ToastType.Success,
        message: `Successfully switched to ${selectedCustomer.fullName}!`,
        duration: 3000,
        testId: "customer-profile-success-toast",
      }),
    );
  };

  return (
    <div ref={dropdownRef} className="relative">
      {/* Profile trigger */}
      <button
        type="button"
        onClick={() => setIsOpen((prev) => !prev)}
        className="btn btn-ghost text-primary-content hover:bg-white/10 normal-case"
        aria-label="Select customer profile"
        aria-expanded={isOpen}
        aria-haspopup="true"
      >
        <div className="flex items-center gap-2">
          <div className="flex size-9 items-center justify-center rounded-full bg-white/15">
            <UserRound size={19} />
          </div>

          <div className="hidden text-left sm:block">
            <p className="text-xs text-primary-content/70">
              Current profile
            </p>

            <p className="max-w-40 truncate font-semibold">
              {isLoading
                ? "Loading profiles..."
                : currentCustomer?.email ?? "Please select profile"}
            </p>
          </div>

          <ChevronDown
            size={16}
            className={`transition-transform duration-200 ${isOpen ? "rotate-180" : ""
              }`}
          />
        </div>
      </button>

      {/* Dropdown menu */}
      {isOpen && (
        <ul
          className="absolute right-0 z-50 mt-3 w-72 menu rounded-box border border-base-300 bg-base-100 p-2 text-base-content shadow-lg"
        >
          <li className="menu-title px-3 py-2">
            <span>Select customer profile</span>
          </li>

          {isLoading && (
            <li className="pointer-events-none">
              <span>
                <span className="loading loading-spinner loading-sm" />
                Loading customers...
              </span>
            </li>
          )}

          {isError && (
            <li>
              <button type="button" onClick={() => refetch()}>
                Unable to load customers. Retry
              </button>
            </li>
          )}

          {!isLoading && !isError && customers.length === 0 && (
            <li className="pointer-events-none">
              <span>No customers available</span>
            </li>
          )}

          {!isLoading &&
            !isError &&
            customers.map((customer) => {
              const isSelected = currentCustomer?.id === customer.id;

              return (
                <li key={customer.id}>
                  <button
                    type="button"
                    onClick={() => handleSwitchCustomer(customer.id)}
                    className={`flex items-center justify-between gap-3 ${isSelected
                      ? "bg-primary/10 text-primary"
                      : "hover:bg-base-200"
                      }`}
                  >
                    <div className="flex min-w-0 items-center gap-3">
                      <div className="flex size-9 shrink-0 items-center justify-center rounded-full bg-primary/10 text-primary">
                        <UserRound size={18} />
                      </div>

                      <div className="min-w-0 text-left">
                        <p className="truncate font-semibold">
                          {customer.fullName}
                        </p>

                        <p className="truncate text-xs opacity-60">
                          {customer.email}
                        </p>
                      </div>
                    </div>

                    {isSelected && (
                      <Check
                        size={17}
                        className="shrink-0 text-primary"
                      />
                    )}
                  </button>
                </li>
              );
            })}
        </ul>
      )}
    </div>
  );
}
