export interface Vehicle {
  id: string;
  customerId: string;
  registrationNumber: string;
  make: string;
  model: string;
  modelYear: number | null;
  imageUrl?: string | null;
}
