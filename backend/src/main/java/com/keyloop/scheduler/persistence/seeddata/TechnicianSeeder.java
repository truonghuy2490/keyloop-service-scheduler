
package com.keyloop.scheduler.persistence.seeddata;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TechnicianSeeder {

    public void seed(JdbcTemplate jdbc) {
        seedTechnicians(jdbc);
    }

    private void seedTechnicians(JdbcTemplate jdbc) {
        jdbc.update("""
            INSERT INTO technician (
                id,
                dealership_id,
                full_name,
                active
            )
            VALUES
                (
                    '40000000-0000-0000-0000-000000000001',
                    '10000000-0000-0000-0000-000000000001',
                    'John Smith',
                    TRUE
                ),
                (
                    '40000000-0000-0000-0000-000000000002',
                    '10000000-0000-0000-0000-000000000001',
                    'David Wilson',
                    TRUE
                ),
                (
                    '40000000-0000-0000-0000-000000000003',
                    '10000000-0000-0000-0000-000000000002',
                    'Michael Brown',
                    TRUE
                ),
                (
                    '40000000-0000-0000-0000-000000000004',
                    '10000000-0000-0000-0000-000000000002',
                    'James Anderson',
                    FALSE
                ),
                (
                    '40000000-0000-0000-0000-000000000005',
                    '10000000-0000-0000-0000-000000000003',
                    'Robert Taylor',
                    TRUE
                ),
                (
                    '40000000-0000-0000-0000-000000000006',
                    '10000000-0000-0000-0000-000000000004',
                    'William Thomas',
                    TRUE
                ),
                (
                    '40000000-0000-0000-0000-000000000007',
                    '10000000-0000-0000-0000-000000000005',
                    'Daniel Jackson',
                    TRUE
                ),
                (
                    '40000000-0000-0000-0000-000000000008',
                    '10000000-0000-0000-0000-000000000006',
                    'Matthew White',
                    TRUE
                ),
                (
                    '40000000-0000-0000-0000-000000000009',
                    '10000000-0000-0000-0000-000000000007',
                    'Christopher Harris',
                    FALSE
                ),
                (
                    '40000000-0000-0000-0000-000000000010',
                    '10000000-0000-0000-0000-000000000008',
                    'Andrew Martin',
                    TRUE
                )

            ON CONFLICT (id) DO NOTHING
            """);
    }
}

