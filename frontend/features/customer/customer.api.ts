import { baseApiSlice } from "@/lib/api/base-api";
import { API_ENDPOINTS } from "@/lib/api/endpoints";

import type {
  PagedList,
  PaginationRequest,
} from "@/lib/api/base.types";

import type { Customer } from "./customer.types";

export const customerApi = baseApiSlice.injectEndpoints({
  endpoints: (builder) => ({
    // Get customer list
    getCustomers: builder.query<
      PagedList<Customer>,
      PaginationRequest
    >({
      query: (params) => ({
        url: API_ENDPOINTS.CUSTOMERS.BASE,
        method: "GET",
        params,
      }),
      providesTags: ["Customer"],
    }),

    // Get customer details by ID
    getCustomerById: builder.query<Customer, string>({
      query: (customerId) => ({
        url: API_ENDPOINTS.CUSTOMERS.BY_ID(customerId),
        method: "GET",
      }),
      providesTags: (_result, _error, customerId) => [
        { type: "Customer", id: customerId },
      ],
    }),
  }),
});

export const {
  useGetCustomersQuery,
  useGetCustomerByIdQuery,
} = customerApi;
