
import {
  Building2,
  CalendarClock,
  CheckCircle2,
} from "lucide-react";

const steps = [
  {
    number: "01",
    icon: Building2,
    title: "Choose a Dealership",
    description:
      "Explore available dealership locations and select where you'd like your vehicle serviced.",
  },
  {
    number: "02",
    icon: CalendarClock,
    title: "Select Your Service",
    description:
      "Choose your vehicle, service type, and preferred appointment time.",
  },
  {
    number: "03",
    icon: CheckCircle2,
    title: "Confirm Your Booking",
    description:
      "The scheduler checks technician and service bay availability before confirming.",
  },
];

export default function HowItWorks() {
  return (
    <section className="bg-base-200 py-20 sm:py-24">
      <div className="mx-auto max-w-7xl px-6 lg:px-8">
        <div className="text-center">
          <p className="text-sm font-bold uppercase tracking-[0.2em] text-primary">
            How It Works
          </p>

          <h2 className="mt-4 text-3xl font-extrabold tracking-tight sm:text-5xl">
            Three steps to better service.
          </h2>

          <p className="mx-auto mt-5 max-w-xl text-base text-base-content/60">
            Booking your next appointment doesn't
            have to be complicated.
          </p>
        </div>

        <div className="mt-14 grid gap-6 md:grid-cols-3">
          {steps.map((step) => {
            const Icon = step.icon;

            return (
              <article
                key={step.number}
                className="relative rounded-2xl border border-base-300 bg-base-100 p-8 shadow-sm"
              >
                <span className="absolute right-7 top-5 text-5xl font-black text-base-content/5">
                  {step.number}
                </span>

                <div className="flex size-14 items-center justify-center rounded-xl bg-primary text-primary-content">
                  <Icon size={25} />
                </div>

                <p className="mt-7 text-xs font-bold uppercase tracking-widest text-primary">
                  Step {step.number}
                </p>

                <h3 className="mt-2 text-xl font-bold">
                  {step.title}
                </h3>

                <p className="mt-3 text-sm leading-7 text-base-content/60">
                  {step.description}
                </p>
              </article>
            );
          })}
        </div>
      </div>
    </section>
  );
}
