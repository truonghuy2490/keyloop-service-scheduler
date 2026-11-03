import { baseApiSlice } from "@/lib/api/base.api";
import { API_ENDPOINTS } from "@/lib/api/endpoints";

import type {
  PagedList,
  PaginationRequest,
} from "@/lib/api/base.types";

import type {
  Dealership,
  DealershipDetails,
} from "./dealership.types";

export const dealershipApi = baseApiSlice.injectEndpoints({
  endpoints: (builder) => ({
    // Get dealership list
    getDealerships: builder.query<
      PagedList<Dealership>,
      PaginationRequest
    >({
      query: (params) => ({
        url: API_ENDPOINTS.DEALERSHIPS.BASE,
        method: "GET",
        params,
      }),
      providesTags: ["Dealership"],
    }),

    // Get dealership details by ID
    getDealershipById: builder.query<
      DealershipDetails,
      string
    >({
      query: (dealershipId) => ({
        url: API_ENDPOINTS.DEALERSHIPS.BY_ID(dealershipId),
        method: "GET",
      }),
      providesTags: (_result, _error, dealershipId) => [
        { type: "Dealership", id: dealershipId },
      ],
    }),
  }),
});

export const {
  useGetDealershipsQuery,
  useGetDealershipByIdQuery,
} = dealershipApi;
