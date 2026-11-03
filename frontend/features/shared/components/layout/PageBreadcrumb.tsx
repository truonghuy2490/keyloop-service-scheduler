
"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { ChevronRight, Home } from "lucide-react";

interface BreadcrumbItem {
  label: string;
  href: string;
}

const routeLabels: Record<string, string> = {
  appointments: "My Appointments",
  book: "Book a Service",
  dealerships: "Dealerships",
  vehicles: "My Vehicles",
  create: "Create",
  edit: "Edit",
};

export default function PageBreadcrumb() {
  const pathname = usePathname();

  if (pathname === "/") {
    return null;
  }

  const segments = pathname.split("/").filter(Boolean);

  const breadcrumbs: BreadcrumbItem[] = [
    { label: "Home", href: "/" },
  ];

  segments.forEach((segment, index) => {
    const href = "/" + segments.slice(0, index + 1).join("/");

    breadcrumbs.push({
      label: routeLabels[segment] ?? "Details",
      href,
    });
  });

  return (
    <nav aria-label="Breadcrumb" className="text-sm">
      <ol className="flex flex-wrap items-center gap-2 text-base-content/60">
        {breadcrumbs.map((item, index) => {
          const isLast = index === breadcrumbs.length - 1;

          return (
            <li
              key={item.href}
              className="flex items-center gap-2"
            >
              {index > 0 && (
                <ChevronRight
                  size={14}
                  className="text-base-content/35"
                />
              )}

              {isLast ? (
                <span
                  aria-current="page"
                  className="font-semibold text-base-content"
                >
                  {item.label}
                </span>
              ) : (
                <Link
                  href={item.href}
                  className="flex items-center gap-1.5 transition-colors hover:text-primary"
                >
                  {index === 0 && <Home size={14} />}
                  {item.label}
                </Link>
              )}
            </li>
          );
        })}
      </ol>
    </nav>
  );
}

