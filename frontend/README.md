# Service Scheduler — Frontend

A responsive web application for exploring automotive dealerships, reviewing service information, checking appointment availability, and booking vehicle service appointments.

Built with **Next.js**, **React**, **TypeScript**, **Redux Toolkit**, **RTK Query**, **Tailwind CSS**, and **DaisyUI**. This README documents the **frontend application only**.

## Overview

Service Scheduler provides a customer-facing booking experience for automotive maintenance and service appointments. Customers can browse dealerships, explore available services and technicians, select a preferred date and time slot, and confirm an appointment.

The frontend communicates with a separate REST API. Appointment availability and resource allocation are determined by the API rather than calculated or guaranteed in the browser.

## Tech Stack

| Category | Technology |
| --- | --- |
| Framework | Next.js 16 (App Router) |
| UI | React 19, TypeScript |
| Styling | Tailwind CSS 4, DaisyUI |
| State management | Redux Toolkit |
| API data fetching | RTK Query |
| Icons | Lucide React |

## Features

- **Landing page and navigation:** Responsive home experience, shared navbar, and customer profile selector.
- **Dealership discovery:** Browse dealership cards, view dealership details, and explore services and technicians.
- **Technician information:** View technician profiles, service qualifications, and weekly schedules.
- **Appointment booking wizard:** Guided service appointment booking with three entry points.
- **Availability selection:** Review time slots returned by the API and select a technician and appointment time.
- **Booking confirmation:** Review appointment details before submission and display the confirmed appointment after a successful request.
- **Customer appointments:** View appointment information associated with the selected customer profile.
- **User feedback:** Success and error notifications through a shared Redux toast system.
- **API error handling:** Normalize API errors and display meaningful messages from Spring Boot `ProblemDetail` responses.

## Booking Experience

The booking wizard supports three approaches:

| Flow | Steps |
| --- | --- |
| **Book by Service** | Service → Date → Available Time Slot → Confirmation |
| **Book by Date** | Date → Service → Available Time Slot → Confirmation |
| **Book by Technician** | Technician → Service → Date → Available Time Slot → Confirmation |

All flows converge on the same confirmation step. The application submits the selected customer, vehicle, dealership, service type, technician, and start time to the appointment API. The API validates availability and assigns the service bay; the frontend displays the returned booking result.

> **Important:** A time slot shown as available may become unavailable before confirmation. The API is the source of truth when an appointment is created.

## Project Structure

The frontend follows a feature-oriented structure. The tree below highlights the main areas rather than listing every file.

```text
frontend/
├── app/                         # Next.js App Router pages and layouts
│   ├── appointments/
│   │   └── book/                # Appointment booking page
│   ├── dashboard/               # Dashboard routes
│   ├── page.tsx                  # Home page
│   ├── layout.tsx                # Root layout
│   └── StoreProvider.tsx        # Redux provider
├── features/
│   ├── appointments/            # Booking UI, appointment API, types
│   ├── customer/                # Customer API and selected-profile state
│   ├── dealerships/             # Dealership browsing and details
│   ├── home/                    # Landing page components
│   ├── service-bay/             # Service bay API
│   ├── service-types/           # Service type API
│   ├── shared/                  # Shared layout, UI, and toast state
│   ├── technicians/             # Technician details and schedules
│   └── vehicles/                # Vehicle API
├── lib/
│   ├── api/
│   │   ├── base.api.ts          # Shared RTK Query API configuration
│   │   ├── base.types.ts        # Shared API and pagination types
│   │   └── error.utils.ts       # API error helpers
│   ├── config/
│   │   └── env.ts               # Environment configuration
│   └── redux/
│       ├── hooks.ts             # Typed Redux hooks
│       └── store.ts             # Redux store
├── public/
│   └── images/                  # Static assets
└── package.json
```

## Getting Started

### Prerequisites

- Node.js 22 or a compatible version supported by the project's Next.js installation
- npm
- Access to a running Service Scheduler API

### 1. Install dependencies

From the repository root:

```bash
cd frontend
npm install
```

### 2. Configure the API URL

Create a `.env.local` file inside `frontend/`:

```dotenv
NEXT_PUBLIC_API_URL=http://localhost:8081
```

`NEXT_PUBLIC_API_URL` is the **API server origin**, without the `/api/1.0` suffix. The shared RTK Query base query appends that prefix automatically.

If this variable is not set, the current frontend configuration defaults to `http://localhost:8081`.

### 3. Start the development server

```bash
npm run dev
```

Open [http://localhost:3000](http://localhost:3000).

> The API must be reachable from the browser and configured to allow requests from the frontend origin (for example, `http://localhost:3000`).

## API Integration

The application uses **RTK Query** for requests, caching, and mutation state.

- `lib/api/base.api.ts` defines the shared API slice and base URL.
- Feature-specific `*.api.ts` files define endpoints for appointments, availability, dealerships, customers, technicians, vehicles, services, and service bays.
- `lib/api/base.types.ts` defines common pagination and error response types.
- `lib/api/error.utils.ts` helps turn API errors into readable messages.

The configured API prefix is:

```text
{NEXT_PUBLIC_API_URL}/api/1.0/
```

### Error responses

The frontend supports structured error responses compatible with Spring Boot's `ProblemDetail` format:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Dealership ID must be a valid UUID.",
  "instance": "/api/1.0/dealerships/a",
  "errors": [
    {
      "code": "INVALID_DEALERSHIP_ID",
      "description": "Dealership ID must be a valid UUID.",
      "property": "dealershipId"
    }
  ]
}
```

The custom base query normalizes API errors into a shared `ApiErrorResponse` shape. Components can read the error description or fallback detail and display it through the application's toast system. Unexpected server errors should be presented using a safe, generic user-facing message.

## State Management

Redux Toolkit manages shared client state, including the currently selected customer profile and toast notifications. RTK Query manages API request state and caching. Local component state is used for temporary UI interactions such as booking wizard steps and selected dates.

## Available Commands

Run these commands from `frontend/`:

```bash
npm run dev       # Start the development server
npm run build     # Create a production build
npm run start     # Run the production build
npm run lint      # Run linting, if configured in package.json
```

Check `package.json` for the authoritative list of scripts available in your checkout.

## Production Deployment

The frontend can be deployed to a Next.js-compatible hosting platform such as Vercel.

1. Set the project root directory to `frontend` when deploying from the monorepo.
2. Configure `NEXT_PUBLIC_API_URL` with the public origin of the deployed API.
3. Ensure the API permits requests from the deployed frontend origin.
4. Build and deploy the frontend using the scripts defined in `package.json`.

Because `NEXT_PUBLIC_API_URL` is a public Next.js environment variable, **do not place secrets or credentials in it**.

## Scope

This README covers the Next.js frontend only. API implementation, database schema, appointment concurrency control, and server deployment belong in separate backend or repository-level documentation.
