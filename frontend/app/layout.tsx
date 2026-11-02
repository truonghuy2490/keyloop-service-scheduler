import type { Metadata } from "next";

import "./globals.css";
import RootLayoutContent from "@/features/shared/components/layout/RootLayoutContent";
import StoreProvider from "./StoreProvider";

export const metadata: Metadata = {
  title: {
    default: "Service Scheduler | Keyloop",
    template: "%s | Service Scheduler",
  },
  description: "Automotive Service Appointment Scheduling Platform",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" data-theme="keyloop">
      <body className="min-h-screen bg-base-200 text-base-content">
        <StoreProvider>
          <RootLayoutContent>{children}</RootLayoutContent>
        </StoreProvider>
      </body>
    </html>
  );
}
