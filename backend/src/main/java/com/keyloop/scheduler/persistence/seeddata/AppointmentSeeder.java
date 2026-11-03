package com.keyloop.scheduler.persistence.seeddata;

import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AppointmentSeeder {

    public static final UUID APPOINTMENT_001 =
            UUID.fromString("50000000-0000-0000-0000-000000000001");

    public static final UUID APPOINTMENT_002 =
            UUID.fromString("50000000-0000-0000-0000-000000000002");

    public static final UUID APPOINTMENT_003 =
            UUID.fromString("50000000-0000-0000-0000-000000000003");

    public static final UUID APPOINTMENT_004 =
            UUID.fromString("50000000-0000-0000-0000-000000000004");

    public static final UUID APPOINTMENT_005 =
            UUID.fromString("50000000-0000-0000-0000-000000000005");

    public static final UUID APPOINTMENT_006 =
            UUID.fromString("50000000-0000-0000-0000-000000000006");

    private static final UUID TECHNICIAN_JOHN =
            UUID.fromString("40000000-0000-0000-0000-000000000001");

    private static final UUID TECHNICIAN_DAVID =
            UUID.fromString("40000000-0000-0000-0000-000000000002");

    private static final UUID SERVICE_BAY_001 =
            UUID.fromString("20000000-0000-0000-0000-000000000001");

    private static final UUID SERVICE_BAY_002 =
            UUID.fromString("20000000-0000-0000-0000-000000000002");

    public void seed(JdbcTemplate jdbc) {
        seedAppointments(jdbc);
    }

    private void seedAppointments(JdbcTemplate jdbc) {
        jdbc.update("""
            INSERT INTO appointment (
                id,
                customer_id,
                vehicle_id,
                dealership_id,
                service_type_id,
                technician_id,
                service_bay_id,
                start_time,
                end_time,
                status,
                version,
                cancellation_reason
            )
            VALUES
                -- Appointment 001
                -- Nguyen Van An / John Smith / Oil Change
                (
                    ?, ?, ?, ?, ?,
                    ?, ?,
                    TIMESTAMPTZ '2026-11-04 09:00:00+07',
                    TIMESTAMPTZ '2026-11-04 09:45:00+07',
                    'CONFIRMED', 0, NULL
                ),

                -- Appointment 002
                -- Tran Thi Binh / David Wilson / Tire Rotation
                (
                    ?, ?, ?, ?, ?,
                    ?, ?,
                    TIMESTAMPTZ '2026-11-04 09:00:00+07',
                    TIMESTAMPTZ '2026-11-04 09:30:00+07',
                    'CONFIRMED', 0, NULL
                ),

                -- Appointment 003
                -- Nguyen Van An / John Smith / Brake Inspection
                (
                    ?, ?, ?, ?, ?,
                    ?, ?,
                    TIMESTAMPTZ '2026-11-05 09:00:00+07',
                    TIMESTAMPTZ '2026-11-05 09:45:00+07',
                    'CONFIRMED', 0, NULL
                ),

                -- Appointment 004
                -- Tran Thi Binh / David Wilson / Wheel Alignment
                (
                    ?, ?, ?, ?, ?,
                    ?, ?,
                    TIMESTAMPTZ '2026-11-05 10:00:00+07',
                    TIMESTAMPTZ '2026-11-05 11:00:00+07',
                    'CONFIRMED', 0, NULL
                ),

                -- Appointment 005
                -- Nguyen Van An / John Smith / Engine Diagnostics
                (
                    ?, ?, ?, ?, ?,
                    ?, ?,
                    TIMESTAMPTZ '2026-11-11 13:00:00+07',
                    TIMESTAMPTZ '2026-11-11 14:00:00+07',
                    'CONFIRMED', 0, NULL
                ),

                -- Appointment 006
                -- Tran Thi Binh / David Wilson / Suspension Inspection
                (
                    ?, ?, ?, ?, ?,
                    ?, ?,
                    TIMESTAMPTZ '2026-11-12 09:00:00+07',
                    TIMESTAMPTZ '2026-11-12 10:00:00+07',
                    'CONFIRMED', 0, NULL
                )

            ON CONFLICT (id) DO NOTHING
            """,

            // Appointment 001
            APPOINTMENT_001,
            CustomerSeeder.CUSTOMER_001,
            VehicleSeeder.VEHICLE_001,
            DealershipSeeder.DEALERSHIP_001,
            UUID.fromString("30000000-0000-0000-0000-000000000001"),
            TECHNICIAN_JOHN,
            SERVICE_BAY_001,

            // Appointment 002
            APPOINTMENT_002,
            CustomerSeeder.CUSTOMER_002,
            VehicleSeeder.VEHICLE_004,
            DealershipSeeder.DEALERSHIP_001,
            UUID.fromString("30000000-0000-0000-0000-000000000002"),
            TECHNICIAN_DAVID,
            SERVICE_BAY_002,

            // Appointment 003
            APPOINTMENT_003,
            CustomerSeeder.CUSTOMER_001,
            VehicleSeeder.VEHICLE_002,
            DealershipSeeder.DEALERSHIP_001,
            UUID.fromString("30000000-0000-0000-0000-000000000004"),
            TECHNICIAN_JOHN,
            SERVICE_BAY_001,

            // Appointment 004
            APPOINTMENT_004,
            CustomerSeeder.CUSTOMER_002,
            VehicleSeeder.VEHICLE_005,
            DealershipSeeder.DEALERSHIP_001,
            UUID.fromString("30000000-0000-0000-0000-000000000003"),
            TECHNICIAN_DAVID,
            SERVICE_BAY_002,

            // Appointment 005
            APPOINTMENT_005,
            CustomerSeeder.CUSTOMER_001,
            VehicleSeeder.VEHICLE_003,
            DealershipSeeder.DEALERSHIP_001,
            UUID.fromString("30000000-0000-0000-0000-000000000007"),
            TECHNICIAN_JOHN,
            SERVICE_BAY_001,

            // Appointment 006
            APPOINTMENT_006,
            CustomerSeeder.CUSTOMER_002,
            VehicleSeeder.VEHICLE_004,
            DealershipSeeder.DEALERSHIP_001,
            UUID.fromString("30000000-0000-0000-0000-000000000015"),
            TECHNICIAN_DAVID,
            SERVICE_BAY_002
        );
    }
}
