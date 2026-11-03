import { CarFront } from "lucide-react";

export interface HomeVehicle {
  id: string;
  make: string;
  model: string;
  registrationNumber: string;
  year?: number;
}

interface Props {
  vehicles: HomeVehicle[];
}

export default function MyVehiclesCard({
  vehicles,
}: Props) {
  return (
    <section className="rounded-2xl border border-base-300 bg-base-100 p-6 shadow-sm">
      <h2 className="text-lg font-bold">My Vehicles</h2>

      {vehicles.length === 0 ? (
        <div className="mt-8 text-center">
          <CarFront
            size={36}
            className="mx-auto text-base-content/30"
          />

          <p className="mt-3 text-sm text-base-content/60">
            No vehicles found.
          </p>
        </div>
      ) : (
        <div className="mt-5 space-y-4">
          {vehicles.slice(0, 2).map((vehicle) => (
            <div
              key={vehicle.id}
              className="flex items-center gap-3 rounded-xl bg-base-200/50 p-3"
            >
              <div className="rounded-lg bg-primary/10 p-3 text-primary">
                <CarFront size={22} />
              </div>

              <div>
                <h3 className="font-semibold">
                  {vehicle.make} {vehicle.model}
                </h3>

                <p className="text-xs text-base-content/60">
                  {vehicle.year ? `${vehicle.year} · ` : ""}
                  {vehicle.registrationNumber}
                </p>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
