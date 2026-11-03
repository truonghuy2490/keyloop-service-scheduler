
import Image from "next/image";
import Link from "next/link";
import { ArrowRight, CalendarDays, MapPin } from "lucide-react";

export default function LandingHero() {
    return (
        <section className="relative isolate flex min-h-[620px] items-center overflow-hidden bg-slate-950 sm:min-h-[680px] lg:min-h-[760px]">
            <Image
                src="/images/service-hero.png"
                alt="Modern automotive service center"
                fill
                priority
                sizes="100vw"
                quality={85}
                className="object-cover object-center"
            />

            {/* Dark overlay */}
            <div className="absolute inset-0 bg-gradient-to-r from-slate-950/90 via-slate-950/65 to-slate-950/15" />
            <div className="absolute inset-0 bg-gradient-to-t from-slate-950/45 via-transparent to-transparent" />

            <div className="relative z-10 mx-auto w-full max-w-7xl px-6 py-24 sm:px-10 lg:px-12">
                <div className="max-w-2xl text-white">
                    <div className="mb-7 inline-flex items-center gap-2 rounded-full border border-white/25 bg-white/10 px-4 py-2 text-xs font-semibold uppercase tracking-[0.2em] backdrop-blur-sm">
                        <span className="size-2 rounded-full bg-sky-400" />
                        Automotive Service Scheduler
                    </div>

                    <h1 className="text-5xl font-extrabold leading-[1.08] tracking-tight sm:text-6xl lg:text-7xl">
                        Better Care.
                        <br />
                        <span className="text-sky-300">
                            Every Mile.
                        </span>
                    </h1>

                    <p className="mt-7 max-w-xl text-base leading-8 text-white/80 sm:text-lg">
                        Your vehicle deserves a service experience
                        that fits your schedule. Find a dealership,
                        choose your service, and book with confidence.
                    </p>

                    <div className="mt-9 flex flex-wrap gap-3">
                        <Link
                            href="/appointments/book"
                            className="inline-flex min-h-13 items-center justify-center gap-3 rounded-full bg-white px-7 py-3 font-semibold text-slate-950 transition-all hover:-translate-y-0.5 hover:bg-sky-100"
                        >
                            <CalendarDays size={19} />
                            Book a Service
                            <ArrowRight size={18} />
                        </Link>

                        <Link
                            href="/dealerships"
                            className="inline-flex min-h-13 items-center justify-center gap-2 rounded-full border border-white/40 bg-white/10 px-7 py-3 font-semibold text-white backdrop-blur-sm transition-colors hover:bg-white/20"
                        >
                            <MapPin size={18} />
                            Explore Dealerships
                        </Link>
                    </div>
                </div>
            </div>

            <div className="absolute bottom-8 left-6 z-10 hidden items-center gap-3 text-xs font-medium uppercase tracking-[0.2em] text-white/70 sm:flex lg:left-12">
                <span className="h-px w-10 bg-white/60" />
                Your journey starts here
            </div>
        </section>
    );
}
