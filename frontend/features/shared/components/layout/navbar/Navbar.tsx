"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

import { APP_ROUTES, APP_LABELS } from "@/lib/api/routes";

import { useAppSelector } from "@/lib/redux/hooks";

import { Home } from "lucide-react";
import NavbarProfile from "./NavbarProfile";

interface BreadcrumbItem {
  label: string;
  href: string;
}

const getBreadcrumbs = (path: string): BreadcrumbItem[] => {
  if (!path || path === APP_ROUTES.HOME) {
    return [{ label: "Home", href: APP_ROUTES.HOME }];
  }

  const parentRoute = Object.values(APP_ROUTES).find(
    (route) =>
      route !== APP_ROUTES.HOME &&
      path.startsWith(route) &&
      (path === route || path.startsWith(`${route}/`)),
  );

  if (!parentRoute) {
    return [{ label: "Home", href: APP_ROUTES.HOME }];
  }

  const routeConfig = APP_LABELS[parentRoute];

  const breadcrumbs: BreadcrumbItem[] = [
    {
      label: routeConfig?.parentLabel ?? "Section",
      href: parentRoute,
    },
  ];

  const lowerPath = path.toLowerCase();

  if (lowerPath.includes("/create")) {
    breadcrumbs.push({
      label: `Create New ${routeConfig?.itemLabel ?? "Item"}`,
      href: path,
    });
  } else if (lowerPath.includes("/edit")) {
    breadcrumbs.push({
      label: `Edit ${routeConfig?.itemLabel ?? "Item"}`,
      href: path,
    });
  } else if (path !== parentRoute) {
    breadcrumbs.push({
      label: routeConfig?.itemLabel ?? "Details",
      href: path,
    });
  }

  return breadcrumbs;
};

export default function Navbar() {
  const pathname = usePathname();
  const breadcrumbs = getBreadcrumbs(pathname || "");
  // Get selected customer from Redux Persist
  const currentCustomer = useAppSelector(
    (state) => state.customer.currentCustomer,
  );
  return (
    <header className="navbar sticky top-0 z-40 h-17.5 w-full justify-between bg-primary px-4 text-primary-content shadow-sm sm:px-8">
      {/* Left - Breadcrumbs */}
      <div className="breadcrumbs min-w-0 text-sm font-semibold sm:text-base">
        <ul className="flex-nowrap">
          {breadcrumbs.map((crumb, index) => {
            const isLast = index === breadcrumbs.length - 1;

            return (
              <li key={`${crumb.href}-${index}`}>
                {isLast ? (
                  <span className="flex items-center gap-2">
                    {index === 0 && <Home size={16} />}
                    {crumb.label}
                  </span>
                ) : (
                  <Link
                    href={crumb.href}
                    prefetch={false}
                    className="text-primary-content/70 transition-colors hover:text-primary-content"
                  >
                    {crumb.label}
                  </Link>
                )}
              </li>
            );
          })}
        </ul>
      </div>

      {/* Right - Customer Information */}
      <div className="flex shrink-0 items-center gap-3">
        {currentCustomer && (
          <div className="hidden text-right sm:block">
            <p className="max-w-52 truncate text-sm font-medium">
              Hello, {currentCustomer.fullName}!
            </p>
          </div>
        )}

        <NavbarProfile />
      </div>
    </header>
  );
}
