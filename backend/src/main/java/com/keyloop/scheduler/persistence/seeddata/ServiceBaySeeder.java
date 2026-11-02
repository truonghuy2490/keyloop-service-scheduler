package com.keyloop.scheduler.persistence.seeddata;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ServiceBaySeeder {

    public void seed(JdbcTemplate jdbc) {
        seedServiceBays(jdbc);
    }

    private void seedServiceBays(JdbcTemplate jdbc) {
        jdbc.update("""
            INSERT INTO service_bay (
                id,
                dealership_id,
                bay_code,
                active
            )
            VALUES
                -- Dealership 001: Keyloop HCMC Central
                (
                    '20000000-0000-0000-0000-000000000001',
                    ?, 'BAY-001', TRUE
                ),
                (
                    '20000000-0000-0000-0000-000000000002',
                    ?, 'BAY-002', TRUE
                ),

                -- Dealership 002: Keyloop Thu Duc
                (
                    '20000000-0000-0000-0000-000000000003',
                    ?, 'BAY-001', TRUE
                ),
                (
                    '20000000-0000-0000-0000-000000000004',
                    ?, 'BAY-002', FALSE
                ),

                -- Dealership 003: Keyloop Binh Thanh
                (
                    '20000000-0000-0000-0000-000000000005',
                    ?, 'BAY-001', TRUE
                ),
                (
                    '20000000-0000-0000-0000-000000000006',
                    ?, 'BAY-002', TRUE
                ),

                -- Dealership 004: Keyloop District 7
                (
                    '20000000-0000-0000-0000-000000000007',
                    ?, 'BAY-001', TRUE
                ),
                (
                    '20000000-0000-0000-0000-000000000008',
                    ?, 'BAY-002', FALSE
                ),

                -- Dealership 005: Keyloop Da Nang
                (
                    '20000000-0000-0000-0000-000000000009',
                    ?, 'BAY-001', TRUE
                ),
                (
                    '20000000-0000-0000-0000-000000000010',
                    ?, 'BAY-002', TRUE
                )

            ON CONFLICT (id) DO NOTHING
            """,
            DealershipSeeder.DEALERSHIP_001,
            DealershipSeeder.DEALERSHIP_001,
            DealershipSeeder.DEALERSHIP_002,
            DealershipSeeder.DEALERSHIP_002,
            DealershipSeeder.DEALERSHIP_003,
            DealershipSeeder.DEALERSHIP_003,
            DealershipSeeder.DEALERSHIP_004,
            DealershipSeeder.DEALERSHIP_004,
            DealershipSeeder.DEALERSHIP_005,
            DealershipSeeder.DEALERSHIP_005
        );
    }
}
