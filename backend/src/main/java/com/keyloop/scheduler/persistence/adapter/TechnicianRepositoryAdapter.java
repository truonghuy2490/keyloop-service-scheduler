package com.keyloop.scheduler.persistence.adapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository;

@Repository
public class TechnicianRepositoryAdapter implements TechnicianRepository {

    private final JdbcTemplate jdbcTemplate;

    public TechnicianRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =========================================================
    // View technicians by dealership
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<TechnicianData> findDetailsByDealershipId(UUID dealershipId) {

        return jdbcTemplate.query(
                """
                SELECT
                    d.id AS dealership_id,
                    d.name AS dealership_name,
                    t.id AS technician_id,
                    t.full_name,
                    t.active,
                    st.id AS service_type_id,
                    st.name AS service_name
                FROM dealership d
                LEFT JOIN technician t
                    ON t.dealership_id = d.id
                    AND t.is_deleted = false
                LEFT JOIN technician_qualification tq
                    ON tq.technician_id = t.id
                LEFT JOIN service_type st
                    ON st.id = tq.service_type_id
                    AND st.is_deleted = false
                WHERE d.id = ?
                  AND d.is_deleted = false
                ORDER BY t.full_name, st.name
                """,
                (rs, rowNum) -> new TechnicianData(
                        rs.getObject("dealership_id", UUID.class),
                        rs.getString("dealership_name"),
                        rs.getObject("technician_id", UUID.class),
                        rs.getString("full_name"),
                        rs.getBoolean("active"),
                        rs.getObject("service_type_id", UUID.class),
                        rs.getString("service_name")
                ),
                dealershipId
        );
    }

    // =========================================================
    // View technician details
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Optional<TechnicianDetailsData> findDetailsByTechnicianId(
            UUID technicianId
    ) {

        // 1. Retrieve technician and dealership
        Optional<TechnicianDetailsData> technicianOptional =
                findTechnicianBaseDetails(technicianId);

        if (technicianOptional.isEmpty()) {
            return Optional.empty();
        }

        TechnicianDetailsData technician = technicianOptional.get();

        // 2. Retrieve qualifications
        List<QualificationData> qualifications =
                findQualifications(technicianId);

        // 3. Retrieve working hours
        List<WorkingHoursData> workingHours =
                findWorkingHours(technicianId);

        // 4. Retrieve date overrides
        List<DateOverrideData> dateOverrides =
                findDateOverrides(technicianId);

        // 5. Assemble technician details
        TechnicianDetailsData result = new TechnicianDetailsData(
                technician.id(),
                technician.fullName(),
                technician.active(),
                technician.dealershipId(),
                technician.dealershipName(),
                technician.createdAt(),
                technician.updatedAt(),
                qualifications,
                workingHours,
                dateOverrides
        );

        return Optional.of(result);
    }

    // =========================================================
    // Private query methods
    // =========================================================

    private Optional<TechnicianDetailsData> findTechnicianBaseDetails(
            UUID technicianId
    ) {

        List<TechnicianDetailsData> results = jdbcTemplate.query(
                """
                SELECT
                    t.id AS technician_id,
                    t.full_name,
                    t.active,
                    t.created_at AS technician_created_at,
                    t.updated_at AS technician_updated_at,
                    d.id AS dealership_id,
                    d.name AS dealership_name
                FROM technician t
                INNER JOIN dealership d
                    ON d.id = t.dealership_id
                    AND d.is_deleted = false
                WHERE t.id = ?
                  AND t.is_deleted = false
                """,
                (rs, rowNum) -> new TechnicianDetailsData(
                        rs.getObject("technician_id", UUID.class),
                        rs.getString("full_name"),
                        rs.getBoolean("active"),
                        rs.getObject("dealership_id", UUID.class),
                        rs.getString("dealership_name"),
                        getInstant(rs, "technician_created_at"),
                        getInstant(rs, "technician_updated_at"),
                        List.of(),
                        List.of(),
                        List.of()
                ),
                technicianId
        );

        return results.stream().findFirst();
    }

    private List<QualificationData> findQualifications(
            UUID technicianId
    ) {

        return jdbcTemplate.query(
                """
                SELECT
                    st.id AS service_type_id,
                    st.name,
                    st.description,
                    st.duration_minutes,
                    tq.qualified_at
                FROM technician_qualification tq
                INNER JOIN service_type st
                    ON st.id = tq.service_type_id
                    AND st.is_deleted = false
                WHERE tq.technician_id = ?
                ORDER BY st.name
                """,
                (rs, rowNum) -> new QualificationData(
                        rs.getObject("service_type_id", UUID.class),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("duration_minutes"),
                        getInstant(rs, "qualified_at")
                ),
                technicianId
        );
    }

    private List<WorkingHoursData> findWorkingHours(
            UUID technicianId
    ) {

        return jdbcTemplate.query(
                """
                SELECT
                    day_of_week,
                    starts_at,
                    ends_at
                FROM technician_working_hours
                WHERE technician_id = ?
                  AND is_deleted = false
                ORDER BY day_of_week, starts_at
                """,
                (rs, rowNum) -> new WorkingHoursData(
                        DayOfWeek.of(
                                rs.getInt("day_of_week")
                        ),
                        rs.getObject("starts_at", LocalTime.class),
                        rs.getObject("ends_at", LocalTime.class)
                ),
                technicianId
        );
    }

    private List<DateOverrideData> findDateOverrides(
            UUID technicianId
    ) {

        return jdbcTemplate.query(
                """
                SELECT
                    work_date,
                    is_unavailable,
                    starts_at,
                    ends_at,
                    reason
                FROM technician_date_override
                WHERE technician_id = ?
                  AND is_deleted = false
                ORDER BY work_date
                """,
                (rs, rowNum) -> new DateOverrideData(
                        rs.getObject("work_date", LocalDate.class),
                        rs.getBoolean("is_unavailable"),
                        rs.getObject("starts_at", LocalTime.class),
                        rs.getObject("ends_at", LocalTime.class),
                        rs.getString("reason")
                ),
                technicianId
        );
    }

    // =========================================================
    // ResultSet helpers
    // =========================================================

    private Instant getInstant(
            ResultSet rs,
            String columnName
    ) throws SQLException {

        var timestamp = rs.getTimestamp(columnName);

        return timestamp != null
                ? timestamp.toInstant()
                : null;
    }
}
