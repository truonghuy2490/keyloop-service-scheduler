export const API_ENDPOINTS = {
  APPOINTMENTS: {
    BASE: "/appointments",
    BY_ID: (id: string) => `/appointments/${id}`,
  },

  DEALERSHIPS: {
    BASE: "dealerships",
    BY_ID: (dealershipId: string) =>
      `dealerships/${dealershipId}`,
  },


  VEHICLES: {
    BASE: "/vehicles",
  },

  SERVICE_TYPES: {
    BASE: "/service-types",
    BY_ID: (id: string) => `/service-types/${id}`,
  },

  CUSTOMERS: {
    BASE: "/customers",
    BY_ID: (customerId: string) => `/customers/${customerId}`,
  },
  
} as const;