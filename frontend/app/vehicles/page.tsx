import UserVehicleGrid from "@/features/vehicles/components/UserVehicleGrid";

export default function VehiclesPage() {
  return (
    <main className="space-y-6 p-6">
      <div>
        <h1 className="text-2xl font-bold text-base-content">
          Vehicles
        </h1>

        <p className="mt-1 text-sm text-base-content/60">
          Manage your registered vehicles.
        </p>
      </div>

      <UserVehicleGrid />
    </main>
  );
}
