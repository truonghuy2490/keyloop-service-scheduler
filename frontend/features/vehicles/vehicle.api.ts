import { baseApiSlice } from "@/lib/api/base.api";
import { API_ENDPOINTS } from "@/lib/api/endpoints";

import type {
  PagedList,
  PaginationRequest,
} from "@/lib/api/base.types";

import type { Vehicle } from "./vehicle.types";

export interface GetVehiclesRequest extends PaginationRequest {
  customerId: string;
}

export const vehicleApi = baseApiSlice.injectEndpoints({
  endpoints: (builder) => ({
    // Get vehicles belonging to a customer
    getVehicles: builder.query<PagedList<Vehicle>, GetVehiclesRequest>({
      query: ({ customerId, pageNumber, pageSize }) => ({
        url: API_ENDPOINTS.VEHICLES.BASE,
        method: "GET",
        params: {
          customerId,
          pageNumber,
          pageSize,
        },
      }),
      providesTags: (result) =>
        result
          ? [
              ...result.items.map(({ id }) => ({
                type: "Vehicle" as const,
                id,
              })),
              { type: "Vehicle" as const, id: "LIST" },
            ]
          : [{ type: "Vehicle" as const, id: "LIST" }],
    }),
  }),
});

export const {
  useGetVehiclesQuery,
} = vehicleApi;
