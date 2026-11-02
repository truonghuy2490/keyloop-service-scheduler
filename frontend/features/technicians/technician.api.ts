import { baseApiSlice } from "@/lib/api/base-api";
import { API_ENDPOINTS } from "@/lib/api/endpoints";

import type {
  DealershipTechnicians,
  TechnicianDetails,
} from "./technician.types";

export const technicianApi = baseApiSlice.injectEndpoints({
  endpoints: (builder) => ({
    // Get technicians belonging to a dealership
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

    // Get technician details
    getTechnicianDetails: builder.query<
      TechnicianDetails,
      string
    >({
      query: (technicianId) => ({
        url: API_ENDPOINTS.TECHNICIANS.BY_ID(technicianId),
        method: "GET",
      }),
      providesTags: (_result, _error, technicianId) => [
        { type: "Technician", id: technicianId },
      ],
    }),
  }),
});

export const {
  useGetDealershipTechniciansQuery,
  useGetTechnicianDetailsQuery,
} = technicianApi;
