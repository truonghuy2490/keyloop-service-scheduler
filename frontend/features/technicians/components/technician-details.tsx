"use client";

import { CircleAlert } from "lucide-react";
import { useGetTechnicianDetailsQuery } from "@/features/technicians/technician.api";
import TechnicianProfile from "@/features/technicians/components/technician-profile";
import TechnicianQualificationSelect from "@/features/technicians/components/technician-qualification-select";
import TechnicianWeeklyCalendar from "@/features/technicians/components/technician-calendar";

interface TechnicianDetailsProps {
  technicianId: string;
}

export default function TechnicianDetails({ technicianId }: TechnicianDetailsProps) {
  const { data, isLoading, isError, refetch } = useGetTechnicianDetailsQuery(technicianId);

  if (isLoading) {
    return (
      <div className="flex min-h-80 items-center justify-center">
        <span className="loading loading-spinner loading-lg text-primary" />
      </div>
    );
  }

  if (isError || !data) {
    return (
      <div className="rounded-xl border border-error/30 bg-base-100 p-8 text-center">
        <CircleAlert size={32} className="mx-auto mb-3 text-error" />
        <h2 className="font-semibold">Unable to load technician details</h2>
        <p className="mt-1 text-sm text-base-content/60">Something went wrong while retrieving technician information.</p>
        <button type="button" className="btn btn-primary btn-sm mt-4" onClick={() => refetch()}>Try again</button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <TechnicianProfile
        id={data.id}
        fullName={data.fullName}
        active={data.active}
        dealership={data.dealership}
        qualificationCount={data.qualifications.length}
      />
      <TechnicianQualificationSelect qualifications={data.qualifications} />
      <TechnicianWeeklyCalendar
        workingHours={data.workingHours}
        dateOverrides={data.dateOverrides}
      />
    </div>
  );
}
