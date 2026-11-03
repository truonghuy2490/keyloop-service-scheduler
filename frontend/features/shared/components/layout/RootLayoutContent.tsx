"use client";

import { usePathname } from "next/navigation";

import Navbar from "@/features/shared/components/layout/navbar/Navbar";
import ToastContainer from "@/features/shared/components/layout/toast/ToastContainer";

interface RootLayoutContentProps {
  children: React.ReactNode;
}

export default function RootLayoutContent({
  children,
}: RootLayoutContentProps) {
  const pathname = usePathname();

  // Hide navbar on Home Landing Page
  const isLandingPage = pathname === "/";

  return (
    <div className="flex min-h-screen flex-col">
      {/* Global Navbar */}
      {!isLandingPage && <Navbar />}

      {/* Main Content */}
      <div className="min-w-0 flex-1">
        {children}
      </div>

      {/* Global Components */}
      <ToastContainer />
    </div>
  );
}
