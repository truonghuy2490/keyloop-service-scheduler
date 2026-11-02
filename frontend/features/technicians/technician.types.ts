export interface TechnicianQualification {
  serviceTypeId: string;
  serviceName: string;
}

export interface TechnicianItem {
  id: string;
  fullName: string;
  active: boolean;
  qualifications: TechnicianQualification[];
}

export interface DealershipTechnicians {
  dealershipId: string;
  dealershipName: string;
  items: TechnicianItem[];
  totalItems: number;
}


export interface TechnicianQualification {
  serviceTypeId: string;
  serviceName: string;
}

export interface TechnicianItem {
  id: string;
  fullName: string;
  active: boolean;
  qualifications: TechnicianQualification[];
}

export interface DealershipTechnicians {
  dealershipId: string;
  dealershipName: string;
  items: TechnicianItem[];
  totalItems: number;
}

// Technician Details

export interface TechnicianDealership {
  id: string;
  name: string;
}

export interface TechnicianQualificationDetail {
  serviceTypeId: string;
  name: string;
  description: string;
  durationMinutes: number;
  qualifiedAt: string;
}

export interface TechnicianWorkingInterval {
  startsAt: string;
  endsAt: string;
}

export interface TechnicianWorkingHours {
  dayOfWeek:
    | "MONDAY"
    | "TUESDAY"
    | "WEDNESDAY"
    | "THURSDAY"
    | "FRIDAY"
    | "SATURDAY"
    | "SUNDAY";
  intervals: TechnicianWorkingInterval[];
}

export interface TechnicianDateOverride {
  workDate: string;
  isUnavailable: boolean;
  startsAt: string | null;
  endsAt: string | null;
  reason: string | null;
}

export interface TechnicianDetails {
  id: string;
  fullName: string;
  active: boolean;
  dealership: TechnicianDealership;
  qualifications: TechnicianQualificationDetail[];
  workingHours: TechnicianWorkingHours[];
  dateOverrides: TechnicianDateOverride[];
  createdAt: string;
  updatedAt: string;
}