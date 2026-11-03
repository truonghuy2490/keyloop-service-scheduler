import { baseApiSlice } from "@/lib/api/base.api";
import { API_ENDPOINTS } from "@/lib/api/endpoints";

import type {
  AppointmentAvailability,
  GetAppointmentAvailabilityRequest,
} from "./availability.types";

export const availabilityApi = baseApiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getAppointmentAvailability: builder.query<
      AppointmentAvailability,
      GetAppointmentAvailabilityRequest
    >({
      query: (params) => ({
        url: API_ENDPOINTS.APPOINTMENTS.AVAILABILITY,
        method: "GET",
        params,
      }),

      providesTags: [
        { type: "Appointment", id: "AVAILABILITY" },
      ],
    }),
  }),
});

export const { useGetAppointmentAvailabilityQuery } = availabilityApi;
