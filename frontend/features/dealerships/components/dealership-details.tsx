"use client";

import Image from "next/image";
import { useMemo, useState, type ElementType } from "react";
import { useRouter } from "next/navigation";

import {
  MapPin,
  Phone,
  Mail,
  Clock,
  Globe,
  Building2,
  Wrench,
  Users,
  Warehouse,
  CalendarDays,
  ArrowRight,
  CalendarPlus,
} from "lucide-react";

import { useAppSelector } from "@/lib/redux/hooks";

import { useGetDealershipByIdQuery } from "@/features/dealerships/dealership.api";

import type {
  DayOfWeek,
  DealershipWorkingHours,
} from "@/features/dealerships/dealership.types";

import DealershipMonthCalendar from "./dealership-month-calendar";

import DealershipServiceBays from "@/features/service-bay/components/dealership-service-bays";

import DealershipTechnicians from "@/features/technicians/components/dealership-technicians";
import { useGetDealershipTechniciansQuery } from "@/features/technicians/technician.api";
import SearchInput from "@/features/shared/components/ui/Search";
import Pagination from "@/features/shared/components/ui/Pagination";

import ConfirmModal from "@/features/shared/components/ui/ConfirmModal";

interface DealershipDetailsProps {
  dealershipId: string;
}

// Sample information for fields not provided by the backend yet.
const SAMPLE_INFO = {
  address: "123 Nguyen Van Linh, District 7, Ho Chi Minh City, Vietnam",
  phone: "+84 28 1234 5678",
  email: "service@saigonautoservice.com",
};

const DAY_ORDER: DayOfWeek[] = [
  "MONDAY",
  "TUESDAY",
  "WEDNESDAY",
  "THURSDAY",
  "FRIDAY",
  "SATURDAY",
  "SUNDAY",
];

const DAY_LABELS: Record<DayOfWeek, string> = {
  MONDAY: "Monday",
  TUESDAY: "Tuesday",
  WEDNESDAY: "Wednesday",
  THURSDAY: "Thursday",
  FRIDAY: "Friday",
  SATURDAY: "Saturday",
  SUNDAY: "Sunday",
};

function getWorkingHoursSummary(
  workingHours: DealershipWorkingHours[],
): string {
  const openDays = workingHours.filter(
    (item) => !item.isClosed,
  );

  if (openDays.length === 0) {
    return "Currently no opening schedule";
  }

  const firstDay = DAY_LABELS[openDays[0].dayOfWeek];
  const lastDay =
    DAY_LABELS[openDays[openDays.length - 1].dayOfWeek];

  return openDays.length === 7
    ? "Open every day"
    : `${firstDay} - ${lastDay}`;
}

function SectionHeading({
  icon: Icon,
  title,
  description,
}: {
  icon: ElementType;
  title: string;
  description: string;
}) {
  return (
    <div className="flex items-center gap-3">
      <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary">
        <Icon size={21} strokeWidth={2} />
      </div>

      <div>
        <h2 className="font-semibold text-base-content">
          {title}
        </h2>

        <p className="text-sm text-base-content/60">
          {description}
        </p>
      </div>
    </div>
  );
}

function InfoItem({
  label,
  value,
  icon: Icon,
  href,
  isSample = false,
}: {
  label: string;
  value: string;
  icon: ElementType;
  href?: string;
  isSample?: boolean;
}) {
  return (
    <div className="flex min-w-0 items-start gap-3">
      <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-base-200 text-neutral">
        <Icon size={18} />
      </div>

      <div className="min-w-0 flex-1">
        <p className="text-sm text-base-content/60">
          {label}
        </p>

        {href ? (
          <a
            href={href}
            className="break-words font-medium text-primary transition-colors hover:text-accent"
          >
            {value}
          </a>
        ) : (
          <p className="break-words font-medium text-base-content">
            {value}
          </p>
        )}

        {isSample && (
          <span className="mt-1 inline-block text-xs text-warning">
            Sample information
          </span>
        )}
      </div>
    </div>
  );
}

export default function DealershipDetails({
  dealershipId,
}: DealershipDetailsProps) {
  const router = useRouter();

  // Current selected customer profile from Redux.
  const currentCustomer = useAppSelector(
    (state) => state.customer.currentCustomer,
  );

  // Customer profile modal state.
  const [showProfileModal, setShowProfileModal] =
    useState(false);

  const [showContactModal, setShowContactModal] = useState(false);
  const [selectedServiceTypeId, setSelectedServiceTypeId] = useState<string | null>(null);
  const [serviceSearch, setServiceSearch] = useState("");
  const [servicePage, setServicePage] = useState(1);

  // Same RTK Query cache is shared with the technician component.
  const {
    data: technicianData,
    isLoading: isTechniciansLoading,
    error: techniciansError,
    refetch: refetchTechnicians,
  } = useGetDealershipTechniciansQuery(dealershipId);

  // Service types inferred from qualifications (not a complete dealership catalog).
  const serviceTypes = useMemo(() => {
    const map = new Map<string, { id: string; name: string }>();
    for (const technician of technicianData?.items ?? []) {
      for (const qualification of technician.qualifications) {
        map.set(qualification.serviceTypeId, {
          id: qualification.serviceTypeId,
          name: qualification.serviceName,
        });
      }
    }
    return [...map.values()].sort((a, b) => a.name.localeCompare(b.name));
  }, [technicianData]);

  const filteredServices = useMemo(() => {
    const term = serviceSearch.trim().toLowerCase();
    return serviceTypes.filter((service) => service.name.toLowerCase().includes(term));
  }, [serviceTypes, serviceSearch]);

  const servicePageSize = 10;
  const serviceTotalPages = Math.ceil(filteredServices.length / servicePageSize);
  const currentServicePage = Math.max(1, Math.min(servicePage, serviceTotalPages || 1));
  const visibleServices = filteredServices.slice(
    (currentServicePage - 1) * servicePageSize,
    currentServicePage * servicePageSize,
  );

  // Fetch dealership details.
  const {
    data,
    error,
    isLoading,
    refetch,
  } = useGetDealershipByIdQuery(dealershipId);

  // Handle booking navigation.
  const handleBookService = () => {
    if (!currentCustomer) {
      setShowProfileModal(true);
      return;
    }

    if (!data?.id) return;

    router.push(
      `/appointments/book/${encodeURIComponent(data.id)}`,
    );
  };

  const handleCloseProfileModal = () => {
    setShowProfileModal(false);
  };

  // Loading state.
  if (isLoading) {
    return (
      <div className="flex min-h-60 items-center justify-center">
        <span className="loading loading-spinner loading-lg text-primary" />
      </div>
    );
  }

  // Error state.
  if (error) {
    return (
      <div className="rounded-xl border border-error/30 bg-base-100 p-6">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h2 className="font-semibold text-error">
              Failed to load dealership details
            </h2>

            <p className="mt-1 text-sm text-base-content/60">
              Please check your connection and try again.
            </p>
          </div>

          <button
            type="button"
            className="btn btn-outline btn-error btn-sm"
            onClick={() => refetch()}
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  // Empty state.
  if (!data) {
    return (
      <div className="rounded-xl border border-dashed border-base-300 bg-base-100 py-16 text-center">
        <Building2
          size={40}
          className="mx-auto mb-3 text-base-content/40"
        />

        <h2 className="font-semibold text-base-content">
          Dealership not found
        </h2>

        <p className="mt-1 text-sm text-base-content/60">
          The dealership may have been removed or does not exist.
        </p>
      </div>
    );
  }

  const address = SAMPLE_INFO.address;
  const phone = SAMPLE_INFO.phone;
  const email = SAMPLE_INFO.email;

  const sortedWorkingHours = [...data.workingHours].sort(
    (a, b) =>
      DAY_ORDER.indexOf(a.dayOfWeek) -
      DAY_ORDER.indexOf(b.dayOfWeek),
  );

  const workingHoursSummary = getWorkingHoursSummary(
    sortedWorkingHours,
  );

  const facilities = [
    {
      label: "Service Bays",
      value: data.resourceSummary.serviceBayCount,
      icon: Warehouse,
    },
    {
      label: "Technicians",
      value: data.resourceSummary.technicianCount,
      icon: Users,
    },
    {
      label: "Available Services",
      value: data.resourceSummary.serviceCount,
      icon: Wrench,
    },
  ];

  return (
    <section className="space-y-6">
      {/* Hero Banner */}
      <div className="overflow-hidden rounded-2xl border border-base-300 bg-base-100 shadow-sm">
        <div className="relative h-64 w-full md:h-80">
          <Image
            src="/images/dealership-profile.jpg"
            alt={`${data.name} dealership`}
            fill
            priority
            sizes="(max-width: 768px) 100vw, 1200px"
            className="object-cover"
          />

          <div className="absolute inset-0 bg-gradient-to-t from-primary/95 via-primary/30 to-transparent" />

          <div className="absolute inset-x-0 bottom-0 p-6 md:p-8">
            <span className="mb-3 inline-flex items-center rounded-full border border-white/30 bg-white/15 px-3 py-1 text-xs font-semibold text-white backdrop-blur-sm">
              DEALERSHIP
            </span>

            <h1 className="text-2xl font-bold text-white md:text-4xl">
              {data.name}
            </h1>

            <div className="mt-3 flex flex-col gap-2 text-sm text-white/85 sm:flex-row sm:flex-wrap sm:gap-5">
              <span className="flex items-center gap-2">
                <MapPin
                  size={16}
                  className="shrink-0 text-accent"
                />
                {address}
              </span>

              <span className="flex items-center gap-2">
                <Clock
                  size={16}
                  className="shrink-0 text-accent"
                />
                {workingHoursSummary}
              </span>
            </div>
          </div>
        </div>

        <div className="flex flex-col justify-between gap-4 p-5 sm:flex-row sm:items-center">
          <div>
            <p className="text-sm text-base-content/60">
              Welcome to
            </p>

            <p className="font-semibold text-base-content">
              {data.name}
            </p>
          </div>

          <button
            type="button"
            onClick={() => setShowContactModal(true)}
            className="btn btn-primary gap-2"
          >
            <Phone size={16} />
            Contact Dealership
          </button>
        </div>
      </div>

      {/* Sample Data Notice */}
      <div className="alert border border-warning/30 bg-warning/10 text-base-content">
        <div className="flex items-start gap-3">
          <span className="mt-0.5 text-warning">
            <Globe size={18} />
          </span>

          <div>
            <p className="text-sm font-semibold">
              Demonstration data
            </p>

            <p className="text-sm text-base-content/70">
              Address and contact information are sample data
              for demonstration purposes and are not retrieved
              from the backend.
            </p>
          </div>
        </div>
      </div>

      {/* General Information */}
      <div className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
        <div className="border-b border-base-300 p-5">
          <SectionHeading
            icon={Building2}
            title="General Information"
            description="Basic contact and location details"
          />
        </div>

        <div className="grid gap-6 p-5 md:grid-cols-2">
          <InfoItem
            label="Dealership Name"
            value={data.name}
            icon={Building2}
          />

          <InfoItem
            label="Address"
            value={address}
            icon={MapPin}
            isSample
          />

          <InfoItem
            label="Phone Number"
            value={phone}
            icon={Phone}
            href={`tel:${phone}`}
            isSample
          />

          <InfoItem
            label="Email Address"
            value={email}
            icon={Mail}
            href={`mailto:${email}`}
            isSample
          />

          <InfoItem
            label="Time Zone"
            value={data.timeZone}
            icon={Globe}
          />

          <InfoItem
            label="Dealership ID"
            value={data.id}
            icon={Building2}
          />
        </div>
      </div>

      {/* Monthly Business Calendar */}
      <DealershipMonthCalendar
        workingHours={data.workingHours}
        dateOverrides={data.dateOverrides}
        timeZone={data.timeZone}
      />

      {/* Facilities & Capacity */}
      <div className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
        <div className="border-b border-base-300 p-5">
          <SectionHeading
            icon={Warehouse}
            title="Facilities & Capacity"
            description="Dealership service resources"
          />
        </div>

        <div className="grid grid-cols-1 gap-4 p-5 sm:grid-cols-3">
          {facilities.map((facility) => {
            const Icon = facility.icon;

            return (
              <div
                key={facility.label}
                className="rounded-xl border border-base-300 bg-base-200/70 p-5 transition-colors hover:border-primary/30 hover:bg-base-200"
              >
                <div className="mb-4 flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary">
                  <Icon size={21} />
                </div>

                <p className="text-2xl font-bold text-base-content">
                  {facility.value}
                </p>

                <p className="mt-1 text-sm text-base-content/60">
                  {facility.label}
                </p>
              </div>
            );
          })}
        </div>
      </div>

      {/* Service Bays */}
      <DealershipServiceBays dealershipId={data.id} />

      {/* Services inferred from technician qualifications */}
      <section className="overflow-hidden rounded-xl border border-base-300 bg-base-100 shadow-sm">
        <div className="border-b border-base-300 p-5">
          <SectionHeading icon={Wrench} title="Available Services" description="Select a service to see qualified technicians" />
        </div>
        <div className="space-y-4 p-5">
          <SearchInput
            value={serviceSearch}
            onChange={(value: string) => { setServiceSearch(value); setServicePage(1); }}
            placeholder="Search services..."
          />
          {isTechniciansLoading ? (
            <div className="flex min-h-32 items-center justify-center">
              <span className="loading loading-spinner loading-md text-primary" />
            </div>
          ) : techniciansError ? (
            <div className="flex items-center justify-between gap-3">
              <p className="text-sm text-error">Unable to load services.</p>
              <button type="button" className="btn btn-outline btn-error btn-sm" onClick={() => refetchTechnicians()}>Retry</button>
            </div>
          ) : serviceTypes.length === 0 ? (
            <p className="py-8 text-center text-sm text-base-content/60">No qualified services found for this dealership.</p>
          ) : (
            <>
              <div className="grid gap-3 sm:grid-cols-2">
                {visibleServices.map((service) => {
                  const selected = selectedServiceTypeId === service.id;
                  return (
                    <button
                      key={service.id}
                      type="button"
                      aria-pressed={selected}
                      onClick={() => setSelectedServiceTypeId(selected ? null : service.id)}
                      className={`flex items-center justify-between gap-3 rounded-xl border p-4 text-left transition-colors ${selected ? "border-primary bg-primary/5 ring-1 ring-primary" : "border-base-300 hover:border-primary/50"}`}
                    >
                      <span className="flex min-w-0 items-center gap-3">
                        <Wrench size={19} className="shrink-0 text-primary" />
                        <span className="font-medium">{service.name}</span>
                      </span>
                      {selected && <span className="badge badge-primary">Selected</span>}
                    </button>
                  );
                })}
              </div>
              {visibleServices.length === 0 && (
                <p className="py-6 text-center text-sm text-base-content/60">No matching services found.</p>
              )}
              {serviceTotalPages > 1 && (
                <Pagination
                  pageNumber={currentServicePage}
                  pageSize={servicePageSize}
                  totalPages={serviceTotalPages}
                  totalCount={filteredServices.length}
                  hasPreviousPage={currentServicePage > 1}
                  hasNextPage={currentServicePage < serviceTotalPages}
                  onPageChange={setServicePage}
                  btnPreviousPageTestId="btnPreviousServicePage"
                  btnNextPageTestId="btnNextServicePage"
                  btnCurrentPageTestId="btnCurrentServicePage"
                />
              )}
            </>
          )}
        </div>
      </section>

      {/* Technicians filtered by selected service */}
      <DealershipTechnicians
        dealershipId={data.id}
        serviceTypeId={selectedServiceTypeId}
        serviceTypeName={serviceTypes.find((service) => service.id === selectedServiceTypeId)?.name}
      />

      {/* Booking Information */}
      <div className="overflow-hidden rounded-xl bg-primary text-primary-content shadow-sm">
        <div className="flex flex-col justify-between gap-5 p-6 sm:flex-row sm:items-center">
          <div>
            <div className="flex items-center gap-2">
              <CalendarDays
                size={21}
                className="text-accent"
              />

              <h2 className="font-semibold">
                Ready to service your vehicle?
              </h2>
            </div>

            <p className="mt-2 text-sm text-primary-content/75">
              Choose your service and find an available appointment.
            </p>
          </div>

          <button
            type="button"
            onClick={handleBookService}
            className="btn btn-accent shrink-0 gap-2"
            data-testid="btnBookDealershipService"
          >
            <CalendarPlus size={18} />
            Book Service
            <ArrowRight size={17} />
          </button>
        </div>
      </div>

      {/* Customer Profile Confirmation Modal */}
      <ConfirmModal
        isOpen={showProfileModal && !currentCustomer}
        onClose={handleCloseProfileModal}
        variant="warning"
        title="Customer Profile Required"
        body={
          <div className="space-y-3">
            <p>
              You need to select a customer profile before
              booking a service appointment.
            </p>

            <p>
              Click the profile icon in the top-right corner
              of the navigation bar to select your customer
              profile.
            </p>
          </div>
        }
        yesButtonLabel="Got It"
        noButtonLabel="Not Now"
        onYes={handleCloseProfileModal}
        onNo={handleCloseProfileModal}
        modalTestId="dlgBookingCustomerProfile"
        confirmBtnTestId="btnConfirmBookingProfile"
        cancelBtnTestId="btnCancelBookingProfile"
      />
      
      {/* Contact Dealership Coming Soon */}
      <ConfirmModal
        isOpen={showContactModal}
        onClose={() => setShowContactModal(false)}
        variant="info"
        title="Contact Dealership Coming Soon"
        body={
          <div className="space-y-3">
            <p>
              The Contact Dealership feature is currently
              under development and will be available soon.
            </p>

            <p>
              You can still book a service appointment
              through our online booking system.
            </p>
          </div>
        }
        modalTestId="dlgContactDealershipComingSoon"
        confirmBtnTestId="btnDismissContactNotice"
      />
    </section>
  );
}
