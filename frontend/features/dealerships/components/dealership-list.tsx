
"use client";

import { useEffect, useMemo, useState } from "react";
import { Building2, Search } from "lucide-react";

import { useGetDealershipsQuery } from "@/features/dealerships/dealership.api";
import type { Dealership } from "@/features/dealerships/dealership.types";

import DealershipCard from "./dealership-card";
import Pagination from "@/features/shared/components/ui/Pagination";
import SearchInput from "@/features/shared/components/ui/Search";

const PAGE_SIZE = 5;
const FETCH_PAGE_SIZE = 100;

export default function DealershipList() {
  const [pageNumber, setPageNumber] = useState(1);
  const [keyword, setKeyword] = useState("");

  // API pagination state
  const [fetchPage, setFetchPage] = useState(1);

  const [loadedPages, setLoadedPages] = useState<
    Record<number, Dealership[]>
  >({});

  const {
    currentData: data,
    isLoading,
    isFetching,
    isError,
    refetch,
  } = useGetDealershipsQuery({
    pageNumber: fetchPage,
    pageSize: FETCH_PAGE_SIZE,
  });

  // Store each fetched API page.
  useEffect(() => {
    if (!data || isFetching) return;

    const responsePage = data.pageNumber;

    setLoadedPages((previous) => {
      if (previous[responsePage]) {
        return previous;
      }

      return {
        ...previous,
        [responsePage]: data.items,
      };
    });
  }, [data, isFetching]);

  // Total API pages.
  const apiTotalPages = data?.totalPages ?? 0;

  // Check whether all pages have been loaded.
  const isFullyLoaded =
    data !== undefined &&
    Array.from(
      { length: apiTotalPages },
      (_, index) => index + 1,
    ).every((page) => loadedPages[page] !== undefined);

  // Fetch next API page automatically.
  useEffect(() => {
    if (
      !data ||
      isFetching ||
      isFullyLoaded ||
      fetchPage >= data.totalPages
    ) {
      return;
    }

    // Wait until the current page has been stored.
    if (!loadedPages[fetchPage]) {
      return;
    }

    setFetchPage((previous) => previous + 1);
  }, [
    data,
    isFetching,
    isFullyLoaded,
    fetchPage,
    loadedPages,
  ]);

  // Combine all loaded API pages.
  const dealerships = useMemo(() => {
    const merged = new Map<string, Dealership>();

    Object.keys(loadedPages)
      .map(Number)
      .sort((a, b) => a - b)
      .forEach((page) => {
        loadedPages[page].forEach((dealership) => {
          merged.set(dealership.id, dealership);
        });
      });

    return Array.from(merged.values());
  }, [loadedPages]);

  // Search across the entire dealership list.
  const filteredDealerships = useMemo(() => {
    const normalizedKeyword = keyword.trim().toLowerCase();

    return dealerships
      .filter((dealership) => {
        if (!normalizedKeyword) return true;

        const searchableFields = [
          dealership.name,
          dealership.address,
          dealership.phone,
          dealership.email,
        ];

        return searchableFields.some((field) =>
          field
            ?.toLowerCase()
            .includes(normalizedKeyword),
        );
      })
      .sort((a, b) =>
        (a.name ?? "").localeCompare(b.name ?? ""),
      );
  }, [dealerships, keyword]);

  // Calculate pagination AFTER filtering.
  const totalCount = filteredDealerships.length;
  const totalPages = Math.ceil(totalCount / PAGE_SIZE);

  const currentPage = Math.max(
    1,
    Math.min(pageNumber, totalPages || 1),
  );

  const paginatedDealerships = useMemo(() => {
    const startIndex = (currentPage - 1) * PAGE_SIZE;

    return filteredDealerships.slice(
      startIndex,
      startIndex + PAGE_SIZE,
    );
  }, [filteredDealerships, currentPage]);

  const handleSearch = (value: string) => {
    setKeyword(value);
    setPageNumber(1);
  };

  const handleRetry = () => {
    // Retry the failed API page.
    refetch();
  };

  const isLoadingAll = isLoading || !isFullyLoaded;

  return (
    <section className="space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-5 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <div className="flex items-center gap-2">
            <div className="flex size-10 items-center justify-center rounded-xl bg-primary/10 text-primary">
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

      {/* Results Summary */}
      <div className="flex min-h-8 items-center justify-between border-b border-base-300 pb-3">
        <div className="flex items-center gap-2 text-sm text-base-content/70">
          {keyword.trim() ? (
            <>
              <Search size={15} />
              <span>
                {isFullyLoaded
                  ? `${totalCount} matching dealerships`
                  : "Loading dealerships..."}
              </span>
            </>
          ) : (
            <span>
              {isFullyLoaded
                ? `${totalCount} dealerships`
                : "Loading dealerships..."}
            </span>
          )}
        </div>

        {isLoadingAll && !isError && (
          <span className="loading loading-spinner loading-sm text-primary" />
        )}
      </div>

      {/* Content */}
      {isError ? (
        <div className="rounded-xl border border-error/30 bg-base-100 p-6">
          <h2 className="font-semibold text-error">
            Failed to load dealerships
          </h2>

          <p className="mt-1 text-sm text-base-content/60">
            Please check your connection and try again.
          </p>

          <button
            type="button"
            className="btn btn-outline btn-error btn-sm mt-4"
            onClick={handleRetry}
          >
            Retry
          </button>
        </div>
      ) : isLoadingAll ? (
        <div className="flex min-h-60 flex-col items-center justify-center gap-3">
          <span className="loading loading-spinner loading-lg text-primary" />

          <p className="text-sm text-base-content/60">
            Loading all dealerships...
          </p>
        </div>
      ) : paginatedDealerships.length > 0 ? (
        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {paginatedDealerships.map((dealership) => (
            <DealershipCard
              key={dealership.id}
              dealership={dealership}
            />
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center rounded-xl border border-dashed border-base-300 bg-base-100 px-6 py-16 text-center">
          <div className="mb-4 flex size-14 items-center justify-center rounded-2xl bg-primary/10 text-primary">
            {keyword.trim() ? (
              <Search size={25} />
            ) : (
              <Building2 size={25} />
            )}
          </div>

          <h3 className="font-semibold text-base-content">
            {keyword.trim()
              ? "No matching dealerships"
              : "No dealerships found"}
          </h3>

          <p className="mt-2 max-w-sm text-sm text-base-content/60">
            {keyword.trim()
              ? "Try searching with a different keyword."
              : "There are currently no dealerships available."}
          </p>

          {keyword.trim() && (
            <button
              type="button"
              className="btn btn-outline btn-primary btn-sm mt-5"
              onClick={() => handleSearch("")}
            >
              Clear search
            </button>
          )}
        </div>
      )}

      {/* Pagination */}
      {!isError && isFullyLoaded && totalPages > 1 && (
        <div className="flex flex-col gap-4 border-t border-base-300 pt-5 sm:flex-row sm:items-center sm:justify-between">
          <p className="text-sm text-base-content/60">
            Page {currentPage} of {totalPages}
            <span className="mx-2">—</span>
            Total {totalCount} items
          </p>

          <Pagination
            pageNumber={currentPage}
            pageSize={PAGE_SIZE}
            totalPages={totalPages}
            totalCount={totalCount}
            hasPreviousPage={currentPage > 1}
            hasNextPage={currentPage < totalPages}
            onPageChange={setPageNumber}
            btnPreviousPageTestId="btnPreviousDealershipPage"
            btnNextPageTestId="btnNextDealershipPage"
            btnCurrentPageTestId="btnCurrentDealershipPage"
          />
        </div>
      )}
    </section>
  );
}
