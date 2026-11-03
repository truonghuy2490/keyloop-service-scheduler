export interface CreateAppointmentRequest {
  customerId: string;
  vehicleId: string;
  dealershipId: string;
  serviceTypeId: string;
  technicianId: string;
  startTime: string;
}

export interface AppointmentCustomer {
  id: string;
  fullName: string;
}

export interface AppointmentVehicle {
  id: string;
  registrationNumber: string;
  make: string;
  model: string;
  modelYear: number | null;
}

export interface AppointmentDealership {
  id: string;
  name: string;
  timeZone: string;
}

export interface AppointmentService {
  id: string;
  name: string;
  description: string | null;
  durationMinutes: number;
}

export interface AppointmentTechnician {
  id: string;
  fullName: string;
}

export interface AppointmentServiceBay {
  id: string;
  bayCode: string;
}

export interface AppointmentSchedule {
  startTime: string;
  endTime: string;
  durationMinutes: number;
  timeZone: string;
}

export interface Appointment {
  id: string;
  status: string;

  customer: AppointmentCustomer;
  vehicle: AppointmentVehicle;
  dealership: AppointmentDealership;
  service: AppointmentService;
  technician: AppointmentTechnician;
  serviceBay: AppointmentServiceBay;

  schedule: AppointmentSchedule;

  createdAt: string;
  updatedAt: string;
}

export type CreateAppointmentResponse = Appointment;

export interface CustomerAppointmentVehicle {
  registrationNumber: string;
  make: string;
  model: string;
}

export interface CustomerAppointmentDealership {
  name: string;
}

export interface CustomerAppointmentService {
  name: string;
  durationMinutes: number;
}

export interface CustomerAppointmentSchedule {
  startTime: string;
  endTime: string;
}

export interface CustomerAppointment {
  id: string;
  status: string;
  vehicle: CustomerAppointmentVehicle;
  dealership: CustomerAppointmentDealership;
  service: CustomerAppointmentService;
  schedule: CustomerAppointmentSchedule;
}

export interface GetCustomerAppointmentsRequest {
  customerId: string;
  pageNumber?: number;
  pageSize?: number;
}

export interface PagedList<T> {
  items: T[];
  pageNumber: number;
  pageSize: number;
  totalCount: number;
  totalPages: number;
  hasPreviousPage: boolean;
  hasNextPage: boolean;
}

export type GetCustomerAppointmentsResponse =
  PagedList<CustomerAppointment>;
  