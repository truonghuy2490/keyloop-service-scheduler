package com.keyloop.scheduler.persistence.seeddata;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VehicleSeeder {

    public static final UUID VEHICLE_001 =
        UUID.fromString("20000000-0000-0000-0000-000000000001");
    public static final UUID VEHICLE_002 =
        UUID.fromString("20000000-0000-0000-0000-000000000002");
    public static final UUID VEHICLE_003 =
        UUID.fromString("20000000-0000-0000-0000-000000000003");
    public static final UUID VEHICLE_004 =
        UUID.fromString("20000000-0000-0000-0000-000000000004");
    public static final UUID VEHICLE_005 =
        UUID.fromString("20000000-0000-0000-0000-000000000005");

    // Existing customers
    public static final UUID CUSTOMER_001 =
        UUID.fromString("30000000-0000-0000-0000-000000000001");
    public static final UUID CUSTOMER_002 =
        UUID.fromString("30000000-0000-0000-0000-000000000002");

    public void seed(JdbcTemplate jdbc) {
        seedVehicles(jdbc);
    }

    private void seedVehicles(JdbcTemplate jdbc) {
        jdbc.update("""
            INSERT INTO vehicle (
                id,
                customer_id,
                registration_number,
                make,
                model,
                model_year
            )
            VALUES
                (?, ?, '51A-12345', 'Toyota', 'Camry', 2024),
                (?, ?, '51B-67890', 'Honda', 'Civic', 2023),
                (?, ?, '51C-24680', 'Mazda', 'CX-5', 2022),
                (?, ?, '51D-13579', 'Ford', 'Ranger', 2025),
                (?, ?, '16E-98765', 'Ferrari', 'SF75', 2022)
            ON CONFLICT (id) DO NOTHING
            """,
            VEHICLE_001, CUSTOMER_001,
            VEHICLE_002, CUSTOMER_001,
            VEHICLE_003, CUSTOMER_001,
            VEHICLE_004, CUSTOMER_002,
            VEHICLE_005, CUSTOMER_002
        );
    }
}
