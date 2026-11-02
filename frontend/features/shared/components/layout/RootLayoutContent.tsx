"use client";

import Navbar from "@/features/shared/components/layout/navbar/Navbar";
import ToastContainer from "@/features/shared/components/layout/toast/ToastContainer";

interface RootLayoutContentProps {
  children: React.ReactNode;
}

export default function RootLayoutContent({
  children,
}: RootLayoutContentProps) {
  return (
    <div className="flex min-h-screen flex-col">
      {/* Navbar */}
      <Navbar />

      {/* Main Layout */}
      <div className="drawer md:drawer-open relative flex-1">
        <input
          id="app-drawer"
          type="checkbox"
          className="drawer-toggle"
        />

        {/* Main Content */}
        <div className="drawer-content flex min-h-full flex-col">
          <main className="w-full flex-1 px-4 py-6 sm:px-6 lg:px-8">
            {children}
          </main>
        </div>

      </div>

      {/* Global Components */}
      <ToastContainer />   
    </div>
  );
}
