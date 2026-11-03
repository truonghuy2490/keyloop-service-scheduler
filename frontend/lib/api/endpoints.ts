export const API_ENDPOINTS = {
  APPOINTMENTS: {
    BASE: "/appointments",
    BY_ID: (id: string) => `/appointments/${id}`,
    AVAILABILITY: "/appointments/availability",
  },

  DEALERSHIPS: {
    BASE: "/dealerships",

    BY_ID: (id: string) => `/dealerships/${id}`,

    SERVICE_TYPES: (dealershipId: string) =>
      `/dealerships/${dealershipId}/service-types`,

    TECHNICIANS: (dealershipId: string) =>
      `/dealerships/${dealershipId}/technicians`,
  },

  TECHNICIANS: {
    BASE: "/technicians",
    BY_ID: (technicianId: string) =>
      `/technicians/${technicianId}`,
  },

  SERVICE_BAYS: {
    BASE: "/service-bays",
    BY_ID: (serviceBayId: string) => `/service-bays/${serviceBayId}`,
  },

  VEHICLES: {
    BASE: "/vehicles",
  },

  CUSTOMERS: {
    BASE: "/customers",
    BY_ID: (customerId: string) => `/customers/${customerId}`,
  },
  
} as const;