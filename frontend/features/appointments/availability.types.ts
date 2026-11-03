export interface GetAppointmentAvailabilityRequest {
  dealershipId: string;
  serviceTypeId: string;
  date: string;
  technicianId?: string;
}

export interface AvailabilitySlot {
  startTime: string;
  endTime: string;
  available: boolean;
}

export interface TechnicianAvailabilityItem {
  id: string;
  fullName: string;
  active: boolean;
  availableSlots: AvailabilitySlot[];
}

export interface AppointmentAvailability {
  dealershipId: string;
  serviceTypeId: string;
  date: string;
  timeZone: string;
  durationMinutes: number;
  technicians: TechnicianAvailabilityItem[];
}
