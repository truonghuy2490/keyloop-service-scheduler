package com.keyloop.scheduler.persistence.seeddata;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TechnicianDateOverrideSeeder {

    public void seed(JdbcTemplate jdbc) {
        seedTechnicianDateOverrides(jdbc);
    }

    @Transactional
    private void seedTechnicianDateOverrides(JdbcTemplate jdbc) {

        // Remove old date overrides of John Smith and David Wilson.
        jdbc.update("""
            DELETE FROM technician_date_override
            WHERE technician_id IN (
                '40000000-0000-0000-0000-000000000001',
                '40000000-0000-0000-0000-000000000002'
            )
            """);

        jdbc.update("""
            INSERT INTO technician_date_override (
                technician_id,
                work_date,
                is_unavailable,
                starts_at,
                ends_at,
                reason
            )
            VALUES
                -- John Smith: Full-day unavailable
                (
                    '40000000-0000-0000-0000-000000000001',
                    DATE '2026-11-03',
                    TRUE,
                    NULL,
                    NULL,
                    'Personal leave'
                ),
                (
                    '40000000-0000-0000-0000-000000000001',
                    DATE '2026-11-06',
                    TRUE,
                    NULL,
                    NULL,
                    'Medical appointment'
                ),
                (
                    '40000000-0000-0000-0000-000000000001',
                    DATE '2026-11-10',
                    TRUE,
                    NULL,
                    NULL,
                    'Sick leave'
                ),
                (
                    '40000000-0000-0000-0000-000000000001',
                    DATE '2026-11-16',
                    TRUE,
                    NULL,
                    NULL,
                    'Personal matters'
                ),
                (
                    '40000000-0000-0000-0000-000000000001',
                    DATE '2026-11-20',
                    TRUE,
                    NULL,
                    NULL,
                    'Annual leave'
                ),

                -- John Smith: Half-day working hours
                (
                    '40000000-0000-0000-0000-000000000001',
                    DATE '2026-11-05',
                    FALSE,
                    TIME '09:00',
                    TIME '12:00',
                    'Morning shift only'
                ),
                (
                    '40000000-0000-0000-0000-000000000001',
                    DATE '2026-11-12',
                    FALSE,
                    TIME '13:00',
                    TIME '15:00',
                    'Afternoon shift only'
                ),
                (
                    '40000000-0000-0000-0000-000000000001',
                    DATE '2026-11-18',
                    FALSE,
                    TIME '09:00',
                    TIME '12:00',
                    'Morning shift only'
                ),
                (
                    '40000000-0000-0000-0000-000000000001',
                    DATE '2026-11-25',
                    FALSE,
                    TIME '13:00',
                    TIME '15:00',
                    'Afternoon shift only'
                ),
                (
                    '40000000-0000-0000-0000-000000000001',
                    DATE '2026-11-27',
                    FALSE,
                    TIME '09:00',
                    TIME '12:00',
                    'Morning shift only'
                ),

                -- David Wilson: Full-day unavailable
                (
                    '40000000-0000-0000-0000-000000000002',
                    DATE '2026-11-04',
                    TRUE,
                    NULL,
                    NULL,
                    'Annual leave'
                ),
                (
                    '40000000-0000-0000-0000-000000000002',
                    DATE '2026-11-09',
                    TRUE,
                    NULL,
                    NULL,
                    'Medical appointment'
                ),
                (
                    '40000000-0000-0000-0000-000000000002',
                    DATE '2026-11-13',
                    TRUE,
                    NULL,
                    NULL,
                    'Sick leave'
                ),
                (
                    '40000000-0000-0000-0000-000000000002',
                    DATE '2026-11-17',
                    TRUE,
                    NULL,
                    NULL,
                    'Personal leave'
                ),
                (
                    '40000000-0000-0000-0000-000000000002',
                    DATE '2026-11-23',
                    TRUE,
                    NULL,
                    NULL,
                    'Family event'
                ),

                -- David Wilson: Half-day working hours
                (
                    '40000000-0000-0000-0000-000000000002',
                    DATE '2026-11-06',
                    FALSE,
                    TIME '13:00',
                    TIME '15:00',
                    'Afternoon shift only'
                ),
                (
                    '40000000-0000-0000-0000-000000000002',
                    DATE '2026-11-11',
                    FALSE,
                    TIME '09:00',
                    TIME '12:00',
                    'Morning shift only'
                ),
                (
                    '40000000-0000-0000-0000-000000000002',
                    DATE '2026-11-19',
                    FALSE,
                    TIME '13:00',
                    TIME '15:00',
                    'Afternoon shift only'
                ),
                (
                    '40000000-0000-0000-0000-000000000002',
                    DATE '2026-11-26',
                    FALSE,
                    TIME '09:00',
                    TIME '12:00',
                    'Morning shift only'
                ),
                (
                    '40000000-0000-0000-0000-000000000002',
                    DATE '2026-11-30',
                    FALSE,
                    TIME '13:00',
                    TIME '15:00',
                    'Afternoon shift only'
                )
            """);
    }
}
