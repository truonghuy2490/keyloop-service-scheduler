import BookingPageContent from "@/features/appointments/components/booking-page-content";

interface BookAppointmentPageProps {
  params: Promise<{
    dealershipId: string;
  }>;
}

export default async function BookAppointmentPage({
  params,
}: BookAppointmentPageProps) {
  const { dealershipId } = await params;

  return (
    <main className="min-h-screen bg-base-200 p-6 md:p-10">
      <div className="mx-auto max-w-7xl">
        <BookingPageContent dealershipId={dealershipId} />
      </div>
    </main>
  );
}
