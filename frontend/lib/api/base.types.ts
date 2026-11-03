
// ================= Pagination =================

export interface PaginationRequest {
  pageNumber?: number;
  pageSize?: number;
}

export interface PagedList<T> {
  items: T[];
  pageNumber: number;
  pageSize: number;
  totalPages: number;
  totalCount: number;
  hasPreviousPage?: boolean;
  hasNextPage?: boolean;
}

// ================= API Error =================

export interface ApiErrorItem {
  code: string;
  description: string;
  property: string | null;
}

export interface ApiErrorResponse {
  type: string;
  title: string;
  status?: number;
  detail: string;
  instance?: string;
  errors?: ApiErrorItem[];
}

// ================= Common Types =================

export enum SortDirection {
  Asc = "Asc",
  Desc = "Desc",
}
