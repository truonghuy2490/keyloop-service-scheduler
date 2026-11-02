package com.keyloop.scheduler.persistence.seeddata;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ServiceTypeSeeder {

    public void seed(JdbcTemplate jdbc) {
        seedServiceTypes(jdbc);
    }

    private void seedServiceTypes(JdbcTemplate jdbc) {
        jdbc.update("""
            INSERT INTO service_type (
                id,
                name,
                description,
                duration_minutes
            )
            VALUES
                (
                    '30000000-0000-0000-0000-000000000001',
                    'Oil Change',
                    'Engine oil and oil filter replacement.',
                    45
                ),
                (
                    '30000000-0000-0000-0000-000000000002',
                    'Tire Rotation',
                    'Rotate tires to promote even tire wear.',
                    30
                ),
                (
                    '30000000-0000-0000-0000-000000000003',
                    'Wheel Alignment',
                    'Inspect and adjust wheel alignment.',
                    60
                ),
                (
                    '30000000-0000-0000-0000-000000000004',
                    'Brake Inspection',
                    'Inspect brake pads, discs, and braking system.',
                    45
                ),
                (
                    '30000000-0000-0000-0000-000000000005',
                    'Brake Pad Replacement',
                    'Replace worn brake pads and inspect brake components.',
                    90
                ),
                (
                    '30000000-0000-0000-0000-000000000006',
                    'Battery Replacement',
                    'Test battery condition and replace if necessary.',
                    45
                ),
                (
                    '30000000-0000-0000-0000-000000000007',
                    'Engine Diagnostics',
                    'Run diagnostic scans to identify engine issues.',
                    60
                ),
                (
                    '30000000-0000-0000-0000-000000000008',
                    'Air Conditioning Service',
                    'Inspect and service the vehicle air conditioning system.',
                    90
                ),
                (
                    '30000000-0000-0000-0000-000000000009',
                    'Full Vehicle Inspection',
                    'Perform a comprehensive inspection of vehicle components.',
                    120
                ),
                (
                    '30000000-0000-0000-0000-000000000010',
                    'Engine Tune-up',
                    'Inspect and service engine components for optimal performance.',
                    120
                )

            ON CONFLICT (id) DO NOTHING
            """);
    }
}
