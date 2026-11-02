export const APP_ROUTES = {
  HOME: "/",

  DEALERSHIPS: "/dealerships",
  APPOINTMENTS: "/appointments",
  VEHICLES: "/vehicles",
  SERVICE_TYPES: "/service-types",
  SERVICE_BAYS: "/service-bays",
  CUSTOMERS: "/customers",
} as const;

export const APP_LABELS: Record<
  string,
  { parentLabel: string; itemLabel: string }
> = {
  [APP_ROUTES.HOME]: {
    parentLabel: "Home",
    itemLabel: "Home",
  },

  [APP_ROUTES.DEALERSHIPS]: {
    parentLabel: "Dealerships",
    itemLabel: "Dealership",
  },

  [APP_ROUTES.APPOINTMENTS]: {
    parentLabel: "Appointments",
    itemLabel: "Appointment",
  },

  [APP_ROUTES.VEHICLES]: {
    parentLabel: "Vehicles",
    itemLabel: "Vehicle",
  },

  [APP_ROUTES.SERVICE_TYPES]: {
    parentLabel: "Service Types",
    itemLabel: "Service Type",
  },

  [APP_ROUTES.SERVICE_BAYS]: {
    parentLabel: "Service Bays",
    itemLabel: "Service Bay",
  },

  [APP_ROUTES.CUSTOMERS]: {
    parentLabel: "Customers",
    itemLabel: "Customer",
  },
};
