import { baseApiSlice } from "@/lib/api/base-api";
import { API_ENDPOINTS } from "@/lib/api/endpoints";

import type {
  GetDealershipServiceTypesRequest,
  PagedServiceTypesResponse,
} from "./service-type.types";

export const serviceTypeApi = baseApiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getDealershipServiceTypes: builder.query<
      PagedServiceTypesResponse,
      GetDealershipServiceTypesRequest
    >({
      query: ({ dealershipId, pageNumber, pageSize }) => ({
        url: API_ENDPOINTS.DEALERSHIPS.SERVICE_TYPES(dealershipId),
        method: "GET",
        params: {
          pageNumber,
          pageSize,
        },
      }),
    }),
  }),
});

export const {
  useGetDealershipServiceTypesQuery,
  useLazyGetDealershipServiceTypesQuery,
} = serviceTypeApi;
