
import {
  CalendarCheck2,
  ShieldCheck,
  Wrench,
} from "lucide-react";

const benefits = [
  {
    icon: CalendarCheck2,
    title: "Effortless Booking",
    description:
      "Schedule vehicle maintenance in just a few steps, without the back-and-forth.",
  },
  {
    icon: Wrench,
    title: "Qualified Technicians",
    description:
      "Service appointments are matched with technicians qualified for the work.",
  },
  {
    icon: ShieldCheck,
    title: "Smart Availability",
    description:
      "Available time slots account for both technician and service bay capacity.",
  },
];

export default function WhyChooseUs() {
  return (
    <section className="bg-base-100 py-20 sm:py-24">
      <div className="mx-auto max-w-7xl px-6 lg:px-8">
        <div className="max-w-2xl">
          <p className="text-sm font-bold uppercase tracking-[0.2em] text-primary">
            Why Service Scheduler
          </p>

          <h2 className="mt-4 text-3xl font-extrabold tracking-tight text-base-content sm:text-5xl">
            Car care, made simpler.
          </h2>

          <p className="mt-5 text-base leading-7 text-base-content/60">
            Less time arranging appointments. More
            confidence in your next service visit.
          </p>
        </div>

        <div className="mt-12 grid gap-6 md:grid-cols-3">
          {benefits.map((benefit) => {
            const Icon = benefit.icon;

            return (
              <article
                key={benefit.title}
                className="group rounded-2xl border border-base-300 bg-base-100 p-8 transition-all duration-200 hover:-translate-y-1 hover:shadow-lg"
              >
                <div className="flex size-14 items-center justify-center rounded-2xl bg-primary/10 text-primary">
                  <Icon size={27} strokeWidth={1.8} />
                </div>

                <h3 className="mt-7 text-xl font-bold text-base-content">
                  {benefit.title}
                </h3>

                <p className="mt-3 text-sm leading-7 text-base-content/60">
                  {benefit.description}
                </p>
              </article>
            );
          })}
        </div>
      </div>
    </section>
  );
}
