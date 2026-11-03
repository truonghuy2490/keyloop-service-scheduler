
"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  CalendarDays,
  ChevronDown,
  Home,
  Building2,
  Menu,
  Wrench,
} from "lucide-react";

import { useAppSelector } from "@/lib/redux/hooks";
import NavbarProfile from "./NavbarProfile";

interface NavigationItem {
  label: string;
  href: string;
  icon: React.ComponentType<{
    size?: number;
    className?: string;
  }>;
}

const navigationItems: NavigationItem[] = [
  {
    label: "Dashboard",
    href: "/dashboard",
    icon: Home,
  },
  {
    label: "Dealerships",
    href: "/dealerships",
    icon: Building2,
  },
  {
    label: "My Appointments",
    href: "/appointments",
    icon: CalendarDays,
  },
];

export default function Navbar() {
  const pathname = usePathname();

  const currentCustomer = useAppSelector(
    (state) => state.customer.currentCustomer,
  );

  // Check whether a customer profile is selected
  const hasSelectedCustomer = Boolean(currentCustomer?.id);

  const customerOnlyRoutes = [
    "/dashboard",
    "/appointments",
  ];

  const visibleNavigationItems = navigationItems.filter(
    (item) =>
      hasSelectedCustomer ||
      !customerOnlyRoutes.includes(item.href),
  );

  const firstName = currentCustomer?.fullName
    ?.trim()
    .split(/\s+/)
    .at(-1);

  const isActive = (href: string) => {
    if (href === "/") {
      return pathname === "/";
    }

    if (href === "/appointments") {
      return (
        pathname === "/appointments" ||
        (pathname.startsWith("/appointments/") &&
          !pathname.startsWith("/appointments/book"))
      );
    }

    return pathname === href || pathname.startsWith(`${href}/`);
  };

  return (
    <header className="sticky top-0 z-40 w-full border-b border-primary-content/10 bg-primary text-primary-content shadow-sm">
      <div className="navbar mx-auto min-h-17.5 max-w-7xl gap-4 px-4 sm:px-6 lg:px-8">
        {/* Left: Mobile menu + Brand */}
        <div className="flex min-w-0 flex-1 items-center gap-2 md:flex-none">
          <div className="dropdown md:hidden">
            <div
              tabIndex={0}
              role="button"
              aria-label="Open navigation menu"
              className="btn btn-ghost btn-square btn-sm border-0 text-primary-content hover:bg-primary-content/10"
            >
              <Menu size={22} />
            </div>

            <ul
              tabIndex={0}
              className="menu dropdown-content z-50 mt-3 w-64 rounded-xl border border-base-300 bg-base-100 p-2 text-base-content shadow-xl"
            >
              {visibleNavigationItems.map((item) => {
                const Icon = item.icon;
                const active = isActive(item.href);

                return (
                  <li key={item.href}>
                    <Link
                      href={item.href}
                      aria-current={active ? "page" : undefined}
                      className={
                        active
                          ? "bg-primary/10 font-semibold text-primary"
                          : ""
                      }
                    >
                      <Icon size={18} />
                      {item.label}
                    </Link>
                  </li>
                );
              })}
            </ul>
          </div>

          <Link
            href="/"
            className="flex min-w-0 items-center gap-3 rounded-lg transition-opacity hover:opacity-90"
            aria-label="Service Scheduler Home"
          >
            <div className="flex size-10 shrink-0 items-center justify-center rounded-xl bg-primary-content/15">
              <Wrench size={21} />
            </div>

            <div className="min-w-0 leading-tight">
              <span className="block text-base font-bold tracking-tight">
                Keyloop
              </span>

              <span className="block truncate text-xs text-primary-content/75">
                Service Scheduler
              </span>
            </div>
          </Link>
        </div>

        {/* Center: Desktop navigation */}
        <nav
          aria-label="Main navigation"
          className="hidden flex-1 items-center justify-center gap-1 md:flex"
        >
          {visibleNavigationItems.map((item) => {
            const Icon = item.icon;
            const active = isActive(item.href);

            return (
              <Link
                key={item.href}
                href={item.href}
                aria-current={active ? "page" : undefined}
                className={[
                  "flex items-center gap-2 rounded-lg px-4 py-2.5",
                  "text-sm font-medium transition-colors",
                  active
                    ? "bg-primary-content/15 text-primary-content"
                    : "text-primary-content/75 hover:bg-primary-content/10 hover:text-primary-content",
                ].join(" ")}
              >
                <Icon size={17} />
                {item.label}
              </Link>
            );
          })}
        </nav>

        {/* Right: Customer information */}
        <div className="flex shrink-0 items-center gap-3">
          {currentCustomer && (
            <div className="hidden text-right md:block">
              <p className="max-w-44 truncate text-sm font-semibold">
                Hello, {firstName || "Customer"}!
              </p>

              <p className="text-xs text-primary-content/70">
                Welcome back
              </p>
            </div>
          )}

          <NavbarProfile />
        </div>
      </div>
    </header>
  );
}
