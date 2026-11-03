
"use client";

import { useMemo, useState } from "react";
import {
  Wrench,
  Clock3,
  CheckCircle2,
  AlertCircle,
} from "lucide-react";

import SearchInput from "@/features/shared/components/ui/Search";
import Pagination from "@/features/shared/components/ui/Pagination";

export interface SelectableServiceType {
  id: string;
  name: string;
  description: string | null;
  durationMinutes: number;
}

interface ServiceTypeSelectorProps {
  serviceTypes: SelectableServiceType[];
  selectedServiceTypeId: string | null;
  onSelect: (serviceTypeId: string) => void;
  qualifiedServiceTypeIds?: string[];
  isLoading?: boolean;
  disabled?: boolean;
}

const PAGE_SIZE = 10;

export default function ServiceTypeSelector({
  serviceTypes,
  selectedServiceTypeId,
  onSelect,
  qualifiedServiceTypeIds,
  isLoading = false,
  disabled = false,
}: ServiceTypeSelectorProps) {
  const [search, setSearch] = useState("");
  const [pageNumber, setPageNumber] = useState(1);

  // Search and filter across the entire service list.
  const filteredServices = useMemo(() => {
    const keyword = search.trim().toLowerCase();

    return serviceTypes
      .filter((service) => {
        if (!qualifiedServiceTypeIds) return true;

        return qualifiedServiceTypeIds.includes(service.id);
      })
      .filter((service) => {
        if (!keyword) return true;

        return (
          service.name.toLowerCase().includes(keyword) ||
          (service.description ?? "")
            .toLowerCase()
            .includes(keyword)
        );
      })
      .sort((a, b) => a.name.localeCompare(b.name));
  }, [serviceTypes, qualifiedServiceTypeIds, search]);

  // Calculate pagination after filtering.
  const totalCount = filteredServices.length;
  const totalPages = Math.ceil(totalCount / PAGE_SIZE);

  // Ensure current page stays valid when results change.
  const currentPage = Math.max(
    1,
    Math.min(pageNumber, totalPages || 1),
  );

  const paginatedServices = useMemo(() => {
    const startIndex = (currentPage - 1) * PAGE_SIZE;

    return filteredServices.slice(
      startIndex,
      startIndex + PAGE_SIZE,
    );
  }, [filteredServices, currentPage]);

  const handleSearchChange = (value: string) => {
    setSearch(value);
    setPageNumber(1);
  };

  return (
    <section className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
      <div className="border-b border-base-300 p-5">
        <div className="flex items-center gap-3">
          <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-primary/10 text-primary">
            <Wrench size={22} />
          </div>

          <div>
            <h2 className="font-semibold text-base-content">
              Select Service Type
            </h2>
            <p className="text-sm text-base-content/60">
              Choose the service your vehicle needs
            </p>
          </div>
        </div>
      </div>

      <div className="space-y-5 p-5">
        <SearchInput
          value={search}
          onChange={handleSearchChange}
          placeholder="Search services..."
        />

        {isLoading ? (
          <div className="flex min-h-40 items-center justify-center">
            <span className="loading loading-spinner loading-md text-primary" />
          </div>
        ) : totalCount === 0 ? (
          <div className="flex min-h-40 flex-col items-center justify-center gap-3 text-center">
            <AlertCircle
              size={28}
              className="text-base-content/40"
            />

            <p className="text-sm text-base-content/60">
              No matching services found.
            </p>
          </div>
        ) : (
          <>
            <div className="grid gap-3 sm:grid-cols-2">
              {paginatedServices.map((service) => {
                const isSelected =
                  selectedServiceTypeId === service.id;

                return (
                  <button
                    key={service.id}
                    type="button"
                    disabled={disabled}
                    onClick={() => onSelect(service.id)}
                    aria-pressed={isSelected}
                    className={`group flex min-h-36 flex-col rounded-xl border p-4 text-left transition-all ${
                      isSelected
                        ? "border-primary bg-primary/5 ring-1 ring-primary"
                        : "border-base-300 bg-base-100 hover:border-primary/50 hover:shadow-md"
                    } disabled:cursor-not-allowed disabled:opacity-50`}
                  >
                    <div className="flex w-full items-start justify-between gap-3">
                      <div
                        className={`flex h-10 w-10 items-center justify-center rounded-lg ${
                          isSelected
                            ? "bg-primary text-primary-content"
                            : "bg-base-200 text-base-content/60"
                        }`}
                      >
                        <Wrench size={20} />
                      </div>

                      {isSelected && (
                        <CheckCircle2
                          size={21}
                          className="text-primary"
                        />
                      )}
                    </div>

                    <h3 className="mt-4 font-semibold text-base-content">
                      {service.name}
                    </h3>

                    <p className="mt-1 line-clamp-2 text-sm text-base-content/60">
                      {service.description ||
                        "No description available"}
                    </p>

                    <div className="mt-auto flex items-center gap-2 pt-4 text-sm text-base-content/70">
                      <Clock3 size={15} />
                      <span>
                        {service.durationMinutes} minutes
                      </span>
                    </div>
                  </button>
                );
              })}
            </div>

            {totalPages > 1 && (
              <div className="border-t border-base-300 pt-4">
                <Pagination
                  pageNumber={currentPage}
                  pageSize={PAGE_SIZE}
                  totalPages={totalPages}
                  totalCount={totalCount}
                  hasPreviousPage={currentPage > 1}
                  hasNextPage={currentPage < totalPages}
                  onPageChange={setPageNumber}
                  btnPreviousPageTestId="btnPreviousServiceTypePage"
                  btnNextPageTestId="btnNextServiceTypePage"
                  btnCurrentPageTestId="btnCurrentServiceTypePage"
                />
              </div>
            )}
          </>
        )}
      </div>
    </section>
  );
}

