package com.keyloop.scheduler.persistence.seeddata;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TechnicianWorkingHoursSeeder {

    public void seed(JdbcTemplate jdbc) {
        seedTechnicianWorkingHours(jdbc);
    }

    @Transactional
    private void seedTechnicianWorkingHours(JdbcTemplate jdbc) {

        // Remove old working hours of John Smith and David Wilson.
        jdbc.update("""
            DELETE FROM technician_working_hours
            WHERE technician_id IN (
                '40000000-0000-0000-0000-000000000001',
                '40000000-0000-0000-0000-000000000002'
            )
            """);

        // Insert the standard working schedule.
        jdbc.update("""
            INSERT INTO technician_working_hours (
                technician_id,
                day_of_week,
                starts_at,
                ends_at
            )
            SELECT
                technicians.technician_id,
                working_days.day_of_week,
                working_intervals.starts_at,
                working_intervals.ends_at
            FROM (
                VALUES
                    ('40000000-0000-0000-0000-000000000001'::UUID),
                    ('40000000-0000-0000-0000-000000000002'::UUID)
            ) AS technicians (technician_id)
            CROSS JOIN (
                VALUES
                    (1::SMALLINT),
                    (2::SMALLINT),
                    (3::SMALLINT),
                    (4::SMALLINT),
                    (5::SMALLINT)
            ) AS working_days (day_of_week)
            CROSS JOIN (
                VALUES
                    (TIME '09:00', TIME '12:00'),
                    (TIME '13:00', TIME '15:00')
            ) AS working_intervals (starts_at, ends_at)
            """);
    }
}