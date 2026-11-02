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
