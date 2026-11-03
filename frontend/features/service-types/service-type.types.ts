export interface ServiceType {
  id: string;
  name: string;
  description: string | null;
  durationMinutes: number;
}

export interface GetDealershipServiceTypesRequest {
  dealershipId: string;
  pageNumber: number;
  pageSize: number;
}

export interface PagedServiceTypesResponse {
  items: ServiceType[];
  pageNumber: number;
  pageSize: number;
  totalCount: number;
  totalPages: number;
  hasPreviousPage: boolean;
  hasNextPage: boolean;
}
