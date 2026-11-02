import TechnicianDetails from "@/features/technicians/components/technician-details";

interface TechnicianPageProps {
  params: Promise<{
    technicianId: string;
  }>;
}

export default async function TechnicianPage({
  params,
}: TechnicianPageProps) {
  const { technicianId } = await params;

  return (
    <main className="mx-auto w-full max-w-7xl space-y-6 p-4 sm:p-6">
      <TechnicianDetails technicianId={technicianId} />
    </main>
  );
}

