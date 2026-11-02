"use client";

import { useEffect, useRef, useState } from "react";
import { ChevronLeft, ChevronRight, X } from "lucide-react";

interface PaginationProps {
  pageNumber: number;
  totalPages: number;
  pageSize?: number;
  totalCount?: number;
  hasPreviousPage: boolean;
  hasNextPage: boolean;
  onPageChange: (page: number) => void;
  btnPreviousPageTestId?: string;
  btnNextPageTestId?: string;
  btnCurrentPageTestId?: string;
}

export default function Pagination({
  pageNumber,
  totalPages,
  pageSize,
  totalCount,
  hasPreviousPage,
  hasNextPage,
  onPageChange,
  btnPreviousPageTestId = "btnPreviousPage",
  btnNextPageTestId = "btnNextPage",
  btnCurrentPageTestId = "btnPage",
}: PaginationProps) {
  const [openDotsIndex, setOpenDotsIndex] = useState<number | null>(null);
  const [goToPage, setGoToPage] = useState(String(pageNumber));
  const popupRef = useRef<HTMLDivElement | null>(null);

  const getPaginationItems = (
    currentPage: number,
    totalPages: number,
    siblingCount = 0,
  ): (number | "...")[] => {
    const totalVisiblePages = siblingCount * 2 + 5;

    if (totalPages <= totalVisiblePages) {
      return Array.from({ length: totalPages }, (_, i) => i + 1);
    }

    const leftSibling = Math.max(currentPage - siblingCount, 1);
    const rightSibling = Math.min(currentPage + siblingCount, totalPages);

    const showLeftDots = leftSibling > 2;
    const showRightDots = rightSibling < totalPages - 1;

    if (!showLeftDots && showRightDots) {
      const leftRange = Array.from(
        { length: 3 + siblingCount * 2 },
        (_, i) => i + 1,
      );

      return [...leftRange, "...", totalPages];
    }

    if (showLeftDots && !showRightDots) {
      const rightRange = Array.from(
        { length: 3 + siblingCount * 2 },
        (_, i) => totalPages - (3 + siblingCount * 2) + 1 + i,
      );

      return [1, "...", ...rightRange];
    }

    const middleRange = Array.from(
      { length: rightSibling - leftSibling + 1 },
      (_, i) => leftSibling + i,
    );

    return [1, "...", ...middleRange, "...", totalPages];
  };

  const shouldShowPagination = totalPages > 1;

  const handleGoToPage = () => {
    const page = Number(goToPage);

    if (
      !Number.isInteger(page) ||
      page < 1 ||
      page > totalPages
    ) {
      setGoToPage(String(pageNumber));
      setOpenDotsIndex(null);
      return;
    }

    onPageChange(page);
    setOpenDotsIndex(null);
  };

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        popupRef.current &&
        !popupRef.current.contains(event.target as Node)
      ) {
        setOpenDotsIndex(null);
      }
    };

    if (openDotsIndex !== null) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [openDotsIndex]);

  useEffect(() => {
    setGoToPage(String(pageNumber));
    setOpenDotsIndex(null);
  }, [pageNumber]);

  if (totalPages < 1 || totalCount === 0) {
    return null;
  }

  return (
    <div className="mt-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      {typeof totalCount === "number" &&
        typeof pageSize === "number" && (
          <p className="text-sm text-base-content/60">
            <span className="hidden sm:inline">
              Page {pageNumber} of {totalPages} — Total {totalCount} items
            </span>

            <span className="sm:hidden">
              {pageNumber} / {totalPages} · {totalCount} items
            </span>
          </p>
        )}

      {shouldShowPagination && (
        <div className="flex w-full justify-center sm:w-auto sm:justify-end">
          <div className="join max-w-full">
            <button
              type="button"
              className="join-item btn btn-sm shrink-0"
              disabled={!hasPreviousPage}
              onClick={() => {
                setOpenDotsIndex(null);
                onPageChange(pageNumber - 1);
              }}
              data-testid={btnPreviousPageTestId}
            >
              <ChevronLeft className="h-4 w-4 sm:hidden" />
              <span className="hidden sm:inline">Previous</span>
            </button>

            {getPaginationItems(pageNumber, totalPages).map(
              (item, index) => {
                if (item === "...") {
                  return (
                    <div
                      key={`dots-${index}`}
                      className="join-item relative shrink-0"
                    >
                      <button
                        type="button"
                        className="btn btn-sm rounded-none"
                        onClick={() => {
                          setOpenDotsIndex(
                            openDotsIndex === index ? null : index,
                          );
                          setGoToPage(String(pageNumber));
                        }}
                      >
                        ...
                      </button>

                      {openDotsIndex === index && (
                        <div
                          ref={popupRef}
                          className="absolute bottom-full left-1/2 z-50 mb-2 w-64 -translate-x-1/2 rounded-lg border border-base-300 bg-base-100 p-4 shadow-lg"
                        >
                          <div className="mb-3 flex items-center justify-between">
                            <p className="text-lg font-medium">
                              Go to page
                            </p>

                            <button
                              type="button"
                              className="btn btn-ghost btn-xs"
                              onClick={() => setOpenDotsIndex(null)}
                              data-testid="btnCloseGoToPage"
                              aria-label="Close go to page popup"
                            >
                              <X className="h-4 w-4" />
                            </button>
                          </div>

                          <div className="flex gap-2">
                            <input
                              type="number"
                              min={1}
                              max={totalPages}
                              value={goToPage}
                              onChange={(e) =>
                                setGoToPage(e.target.value)
                              }
                              onKeyDown={(e) => {
                                if (e.key === "Enter") {
                                  handleGoToPage();
                                }

                                if (e.key === "Escape") {
                                  setOpenDotsIndex(null);
                                }
                              }}
                              className="input input-bordered input-sm w-full"
                              data-testid="txtGoToPage"
                            />

                            <button
                              type="button"
                              className="btn btn-sm btn-primary"
                              onClick={handleGoToPage}
                              data-testid="btnGoToPage"
                            >
                              Go
                            </button>
                          </div>

                          <p className="mt-3 text-sm text-base-content/60">
                            Page 1 - {totalPages}
                          </p>
                        </div>
                      )}
                    </div>
                  );
                }

                return (
                  <button
                    key={item}
                    type="button"
                    onClick={() => {
                      setOpenDotsIndex(null);
                      onPageChange(item);
                    }}
                    className={`join-item btn btn-sm shrink-0 ${
                      pageNumber === item ? "btn-primary" : ""
                    }`}
                    data-testid={
                      pageNumber === item
                        ? btnCurrentPageTestId
                        : `btnPage${item}`
                    }
                  >
                    {item}
                  </button>
                );
              },
            )}

            <button
              type="button"
              className="join-item btn btn-sm shrink-0"
              disabled={!hasNextPage}
              onClick={() => {
                setOpenDotsIndex(null);
                onPageChange(pageNumber + 1);
              }}
              data-testid={btnNextPageTestId}
            >
              <span className="hidden sm:inline">Next</span>
              <ChevronRight className="h-4 w-4 sm:hidden" />
            </button>
          </div>
        </div>
      )}
    </div>
  );
}