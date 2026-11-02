import { baseApiSlice } from "@/lib/api/base-api";
import { API_ENDPOINTS } from "@/lib/api/endpoints";
import type { DealershipTechnicians } from "./technician.types";

export const technicianApi = baseApiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getDealershipTechnicians: builder.query<
      DealershipTechnicians,
      string
    >({
      query: (dealershipId) => ({
        url: API_ENDPOINTS.DEALERSHIPS.TECHNICIANS(dealershipId),
        method: "GET",
      }),
      providesTags: (_result, _error, dealershipId) => [
        { type: "Technician", id: dealershipId },
      ],
    }),
  }),
});

export const {
  useGetDealershipTechniciansQuery,
} = technicianApi;
