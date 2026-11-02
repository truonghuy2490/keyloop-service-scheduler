
"use client";

import { useMemo, useState } from "react";
import { Building2, Search } from "lucide-react";

import { useGetDealershipsQuery } from "@/features/dealerships/dealership.api";
import DealershipCard from "./dealership-card";
import Pagination from "@/features/shared/components/ui/Pagination";
import SearchInput from "@/features/shared/components/ui/Search";

export default function DealershipList() {
  const [pageNumber, setPageNumber] = useState(1);
  const [keyword, setKeyword] = useState("");

  const pageSize = 5;

  const {
    data,
    error,
    isLoading,
    isFetching,
    refetch,
  } = useGetDealershipsQuery({ pageNumber, pageSize });

  const normalizedKeyword = keyword.trim().toLowerCase();

  const filteredDealerships = useMemo(() => {
    const dealerships = data?.items ?? [];

    if (!normalizedKeyword) {
      return dealerships;
    }

    return dealerships.filter((dealership) => {
      const searchableFields = [
        dealership.name,
        dealership.address,
        dealership.phone,
        dealership.email,
      ];

      return searchableFields.some((field) =>
        field?.toLowerCase().includes(normalizedKeyword),
      );
    });
  }, [data?.items, normalizedKeyword]);

  const handleSearch = (value: string) => {
    setKeyword(value);
    setPageNumber(1);
  };

  if (isLoading) {
    return (
      <div className="flex min-h-60 items-center justify-center">
        <span className="loading loading-spinner loading-lg text-primary" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-xl border border-error/30 bg-base-100 p-6">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h2 className="font-semibold text-error">
              Failed to load dealerships
            </h2>
            <p className="mt-1 text-sm text-base-content/60">
              Please check your connection and try again.
            </p>
          </div>

          <button
            className="btn btn-outline btn-error btn-sm"
            onClick={() => refetch()}
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <section className="space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-5 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <div className="flex items-center gap-2">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10 text-primary">
              <Building2 size={21} />
            </div>

            <h1 className="text-2xl font-bold tracking-tight text-base-content">
              Dealerships
            </h1>
          </div>

          <p className="mt-2 text-sm text-base-content/60">
            Explore and manage dealership locations.
          </p>
        </div>

        <div className="w-full lg:max-w-sm">
          <SearchInput
            value={keyword}
            onChange={handleSearch}
            placeholder="Search dealerships..."
          />
        </div>
      </div>

      {/* Result Summary */}
      <div className="flex min-h-8 items-center justify-between border-b border-base-300 pb-3">
        <div className="flex items-center gap-2 text-sm text-base-content/70">
          {normalizedKeyword ? (
            <>
              <Search size={15} />
              <span>
                {filteredDealerships.length} matching dealerships on this page
              </span>
            </>
          ) : (
            <span>
              {data?.totalCount ?? 0} dealerships
            </span>
          )}
        </div>

        {isFetching && (
          <span className="loading loading-spinner loading-sm text-primary" />
        )}
      </div>

      {/* Dealership Cards */}
      {filteredDealerships.length > 0 ? (
        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {filteredDealerships.map((dealership) => (
            <DealershipCard
              key={dealership.id}
              dealership={dealership}
            />
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center rounded-xl border border-dashed border-base-300 bg-base-100 px-6 py-16 text-center">
          <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-primary/10 text-primary">
            {normalizedKeyword ? (
              <Search size={25} />
            ) : (
              <Building2 size={25} />
            )}
          </div>

          <h3 className="font-semibold text-base-content">
            {normalizedKeyword
              ? "No matching dealerships"
              : "No dealerships found"}
          </h3>

          <p className="mt-2 max-w-sm text-sm text-base-content/60">
            {normalizedKeyword
              ? "Try searching with a different keyword."
              : "There are currently no dealerships available."}
          </p>

          {normalizedKeyword && (
            <button
              className="btn btn-outline btn-primary btn-sm mt-5"
              onClick={() => handleSearch("")}
            >
              Clear search
            </button>
          )}
        </div>
      )}

      {/* Pagination */}
      {!normalizedKeyword && data && data.totalPages > 1 && (
        <div className="flex flex-col gap-4 border-t border-base-300 pt-5 sm:flex-row sm:items-center sm:justify-between">
          <p className="text-sm text-base-content/60">
            Page {data.pageNumber} of {data.totalPages}
            <span className="mx-2">—</span>
            Total {data.totalCount} items
          </p>

          <Pagination
            pageNumber={data.pageNumber}
            totalPages={data.totalPages}
            pageSize={data.pageSize}
            totalCount={data.totalCount}
            hasPreviousPage={data.pageNumber > 1}
            hasNextPage={data.pageNumber < data.totalPages}
            onPageChange={setPageNumber}
          />
        </div>
      )}
    </section>
  );
}
