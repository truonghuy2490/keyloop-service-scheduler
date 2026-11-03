
"use client";

import { useState } from "react";
import ServiceTypeSelector, {
  type SelectableServiceType,
} from "@/features/appointments/components/service-type-selector";

interface DealershipServicesProps {
  serviceTypes: SelectableServiceType[];
  selectedServiceTypeId: string | null;
  onSelectServiceType: (serviceTypeId: string) => void;
  isLoading?: boolean;
}

export default function DealershipServices({
  serviceTypes,
  selectedServiceTypeId,
  onSelectServiceType,
  isLoading = false,
}: DealershipServicesProps) {
  return (
    <ServiceTypeSelector
      serviceTypes={serviceTypes}
      selectedServiceTypeId={selectedServiceTypeId}
      onSelect={onSelectServiceType}
      isLoading={isLoading}
    />
  );
}

