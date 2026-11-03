
import Link from "next/link";
import {
  ArrowRight,
  CalendarDays,
  Wrench,
} from "lucide-react";

export default function LandingFooter() {
  return (
    <>
      {/* Final CTA */}
      <section className="bg-primary py-20 text-primary-content sm:py-24">
        <div className="mx-auto max-w-7xl px-6 text-center lg:px-8">
          <p className="text-sm font-bold uppercase tracking-[0.2em] text-primary-content/70">
            Ready when you are
          </p>

          <h2 className="mx-auto mt-5 max-w-3xl text-3xl font-extrabold tracking-tight sm:text-5xl">
            Your next service is just a few clicks away.
          </h2>

          <p className="mx-auto mt-5 max-w-xl text-base leading-7 text-primary-content/75">
            Find your preferred dealership and schedule
            an appointment at a time that works for you.
          </p>

          <Link
            href="/appointments/book"
            className="mt-9 inline-flex min-h-13 items-center justify-center gap-3 rounded-full bg-primary-content px-8 py-3 font-semibold text-primary transition-opacity hover:opacity-90"
          >
            <CalendarDays size={19} />
            Book Your Service
            <ArrowRight size={18} />
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-neutral py-12 text-neutral-content">
        <div className="mx-auto flex max-w-7xl flex-col justify-between gap-8 px-6 md:flex-row lg:px-8">
          <div className="max-w-sm">
            <Link
              href="/"
              className="inline-flex items-center gap-3 text-lg font-bold"
            >
              <Wrench size={23} />
              Keyloop · Service Scheduler
            </Link>

            <p className="mt-4 text-sm leading-6 text-neutral-content/60">
              A demonstration automotive appointment
              scheduling platform.
            </p>
          </div>

          <div className="flex flex-wrap gap-x-8 gap-y-3 text-sm">
            <Link href="/" className="hover:underline">
              Home
            </Link>
            <Link
              href="/appointments/book"
              className="hover:underline"
            >
              Dealerships
            </Link>
            <Link
              href="/appointments"
              className="hover:underline"
            >
              My Appointments
            </Link>
          </div>
        </div>

        <div className="mx-auto mt-10 max-w-7xl border-t border-neutral-content/15 px-6 pt-6 text-xs text-neutral-content/50 lg:px-8">
          Service Scheduler Demo · Built for automotive service booking
        </div>
      </footer>
    </>
  );
}
