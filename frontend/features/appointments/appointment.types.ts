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
