import { baseApiSlice } from "@/lib/api/base-api";
import { API_ENDPOINTS } from "@/lib/api/endpoints";

import type {
  CreateAppointmentRequest,
  CreateAppointmentResponse,
} from "./appointment.types";

export const appointmentApi = baseApiSlice.injectEndpoints({
  endpoints: (builder) => ({
    createAppointment: builder.mutation<
      CreateAppointmentResponse,
      CreateAppointmentRequest
    >({
      query: (request) => ({
        url: API_ENDPOINTS.APPOINTMENTS.BASE,
        method: "POST",
        body: request,
      }),

      invalidatesTags: [
        { type: "Appointment", id: "LIST" },
        { type: "Appointment", id: "AVAILABILITY" },
      ],
    }), 
  }),
});

export const { useCreateAppointmentMutation } = appointmentApi;
