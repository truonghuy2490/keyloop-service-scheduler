package com.keyloop.scheduler.integration.fixture;

import java.util.UUID;

public final class BookingSeedData {

    private BookingSeedData() {
    }

    public static final UUID DEALERSHIP_1 =
            UUID.fromString(
                    "10000000-0000-0000-0000-000000000001"
            );

    public static final UUID JOHN_SMITH =
            UUID.fromString(
                    "40000000-0000-0000-0000-000000000001"
            );

    public static final UUID DAVID_WILSON =
            UUID.fromString(
                    "40000000-0000-0000-0000-000000000002"
            );

    public static final UUID CUSTOMER_1 =
            UUID.fromString(
                    "30000000-0000-0000-0000-000000000001"
            );

    public static final UUID CUSTOMER_2 =
            UUID.fromString(
                    "30000000-0000-0000-0000-000000000002"
            );

    public static final UUID VEHICLE_1 =
            UUID.fromString(
                    "20000000-0000-0000-0000-000000000001"
            );

    public static final UUID VEHICLE_4 =
            UUID.fromString(
                    "20000000-0000-0000-0000-000000000004"
            );

    public static final UUID WHEEL_ALIGNMENT =
            UUID.fromString(
                    "30000000-0000-0000-0000-000000000003"
            );
}
