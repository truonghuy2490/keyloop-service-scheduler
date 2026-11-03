
import { baseApiSlice } from "@/lib/api/base.api";
import { API_ENDPOINTS } from "@/lib/api/endpoints";

import type {
  CreateAppointmentRequest,
  CreateAppointmentResponse,
  GetCustomerAppointmentsRequest,
  GetCustomerAppointmentsResponse,
} from "./appointment.types";

export const appointmentApi = baseApiSlice.injectEndpoints({
  endpoints: (builder) => ({
    // POST /api/1.0/appointments
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

    // GET /api/1.0/appointments/customer
    getCustomerAppointments: builder.query<
      GetCustomerAppointmentsResponse,
      GetCustomerAppointmentsRequest
    >({
      query: ({
        customerId,
        pageNumber = 1,
        pageSize = 10,
      }) => ({
        url: `${API_ENDPOINTS.APPOINTMENTS.BASE}/customer`,
        method: "GET",
        params: {
          customerId,
          pageNumber,
          pageSize,
        },
      }),

      providesTags: [
        { type: "Appointment", id: "LIST" },
      ],
    }),
  }),
});

export const {
  useCreateAppointmentMutation,
  useGetCustomerAppointmentsQuery,
  useLazyGetCustomerAppointmentsQuery,
} = appointmentApi;
