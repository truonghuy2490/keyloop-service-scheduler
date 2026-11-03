import DealershipList from "@/features/dealerships/components/dealership-list";

export default function BookAppointmentsPage() {
  return (
    <main className="min-h-screen bg-base-200 p-6 md:p-10">
      <div className="mx-auto max-w-7xl space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-base-content">
            Book a Service Appointment
          </h1>

          <p className="mt-2 text-sm text-base-content/60">
            Select a dealership to start your booking.
          </p>
        </div>

        <DealershipList />
      </div>
    </main>
  );
}