import { baseApiSlice } from "@/lib/api/base.api";
import { API_ENDPOINTS } from "@/lib/api/endpoints";
import type { PagedList, PaginationRequest } from "@/lib/api/base.types";
import type { ServiceBay } from "./servicebay.types";

export interface GetServiceBaysRequest extends PaginationRequest {
  dealershipId: string;
}

export const serviceBayApi = baseApiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getServiceBays: builder.query<
      PagedList<ServiceBay>,
      GetServiceBaysRequest
    >({
      query: ({ dealershipId, pageNumber, pageSize }) => ({
        url: API_ENDPOINTS.SERVICE_BAYS.BASE,
        method: "GET",
        params: {
          dealershipId,
          pageNumber,
          pageSize,
        },
      }),
      providesTags: (result) =>
        result
          ? [
              ...result.items.map(({ id }) => ({
                type: "ServiceBay" as const,
                id,
              })),
              { type: "ServiceBay" as const, id: "LIST" },
            ]
          : [{ type: "ServiceBay" as const, id: "LIST" }],
    }),
  }),
});

export const { useGetServiceBaysQuery } = serviceBayApi;
