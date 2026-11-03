
"use client";

import { useGetDealershipsQuery } from "@/features/dealerships/dealership.api";

import LandingHero from "./landing-hero";
import WhyChooseUs from "./why-choose-us";
import HowItWorks from "./how-it-works";
import ExploreDealerships from "./explore-dealerships";
import LandingFooter from "./landing-footer";

export default function HomeLanding() {
  const {
    data,
    isLoading,
    isError,
    refetch,
  } = useGetDealershipsQuery({
    pageNumber: 1,
    pageSize: 3,
  });

  return (
    <main className="min-h-screen bg-base-100">
      {/* Full-width hero */}
      <LandingHero />

      {/* Benefits */}
      <WhyChooseUs />

      {/* Booking process */}
      <HowItWorks />

      {/* Dealership discovery */}
      <section
        id="dealerships"
        className="scroll-mt-24 bg-base-100 py-20 sm:py-24"
      >
        <div className="mx-auto max-w-7xl px-6 lg:px-8">
          <ExploreDealerships
            dealerships={data?.items ?? []}
            isLoading={isLoading}
            isError={isError}
            onRetry={() => {
              void refetch();
            }}
          />
        </div>
      </section>

      {/* CTA and Footer */}
      <LandingFooter />
    </main>
  );
}
