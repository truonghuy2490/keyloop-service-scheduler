package com.keyloop.scheduler.persistence.adapter;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.keyloop.scheduler.application.abstraction.dataaccess.DealershipRepository;
import com.keyloop.scheduler.domain.entity.Dealership;
import com.keyloop.scheduler.persistence.entity.DealershipJpaEntity;
import com.keyloop.scheduler.persistence.repository.DealershipJpaRepository;

@Repository
public class DealershipRepositoryAdapter
        extends BaseRepositoryAdapter<Dealership, DealershipJpaEntity, UUID>
        implements DealershipRepository {

    private final JdbcTemplate jdbcTemplate;

    public DealershipRepositoryAdapter(
            DealershipJpaRepository dealershipRepository,
            JdbcTemplate jdbcTemplate) {

        super(dealershipRepository);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DealershipDetailsData> findDetailsById(UUID dealershipId) {

        // 1. Get dealership information
        List<DealershipDetailsData> dealerships = jdbcTemplate.query(
                """
                        SELECT id, name, time_zone
                        FROM dealership
                        WHERE id = ?
                          AND is_deleted = false
                        """,
                (rs, rowNum) -> new DealershipDetailsData(
                        rs.getObject("id", UUID.class),
                        rs.getString("name"),
                        rs.getString("time_zone"),
                        List.of(),
                        List.of(),
                        new ResourceSummaryData(0, 0, 0)),
                dealershipId);

        if (dealerships.isEmpty()) {
            return Optional.empty();
        }

        DealershipDetailsData dealership = dealerships.get(0);

        // 2. Get weekly working hours
        List<WorkingHoursData> workingHours = jdbcTemplate.query(
                """
                        SELECT day_of_week, is_closed, opens_at, closes_at
                        FROM dealership_business_hours
                        WHERE dealership_id = ?
                          AND is_deleted = false
                        ORDER BY day_of_week
                        """,
                (rs, rowNum) -> new WorkingHoursData(
                        DayOfWeek.of(rs.getInt("day_of_week")),
                        rs.getBoolean("is_closed"),
                        toLocalTime(rs.getTime("opens_at")),
                        toLocalTime(rs.getTime("closes_at"))),
                dealershipId);

        // 3. Get date overrides
        List<DateOverrideData> dateOverrides = jdbcTemplate.query(
                """
                        SELECT business_date, is_closed, opens_at, closes_at, reason
                        FROM dealership_date_override
                        WHERE dealership_id = ?
                          AND is_deleted = false
                        ORDER BY business_date
                        """,
                (rs, rowNum) -> new DateOverrideData(
                        rs.getDate("business_date").toLocalDate(),
                        rs.getBoolean("is_closed"),
                        toLocalTime(rs.getTime("opens_at")),
                        toLocalTime(rs.getTime("closes_at")),
                        rs.getString("reason")),
                dealershipId);

        // 4. Get resource counts
        ResourceSummaryData resourceSummary = jdbcTemplate.queryForObject(
                """
                        SELECT
                            (
                                SELECT COUNT(*)
                                FROM service_bay
                                WHERE dealership_id = ?
                                AND is_deleted = false
                            ) AS service_bay_count,

                            (
                                SELECT COUNT(*)
                                FROM technician
                                WHERE dealership_id = ?
                                AND is_deleted = false
                            ) AS technician_count,

                            (
                                SELECT COUNT(DISTINCT st.id)
                                FROM service_type st
                                INNER JOIN technician_qualification tq
                                    ON tq.service_type_id = st.id
                                INNER JOIN technician t
                                    ON t.id = tq.technician_id
                                WHERE t.dealership_id = ?
                                AND t.is_deleted = false
                                AND t.active = true
                                AND st.is_deleted = false
                            ) AS service_count
                        """,
                (rs, rowNum) -> new ResourceSummaryData(
                        rs.getLong("service_bay_count"),
                        rs.getLong("technician_count"),
                        rs.getLong("service_count")),
                dealershipId,
                dealershipId,
                dealershipId);

        // 5. Build details response
        return Optional.of(new DealershipDetailsData(
                dealership.id(),
                dealership.name(),
                dealership.timeZone(),
                workingHours,
                dateOverrides,
                resourceSummary));
    }

    private LocalTime toLocalTime(Time time) {
        return time != null ? time.toLocalTime() : null;
    }

    @Override
    protected Dealership toDomain(DealershipJpaEntity source) {

        if (source == null) {
            return null;
        }

        return Dealership.builder()
                .id(source.getId())
                .isDeleted(source.isDeleted())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .name(source.getName())
                .timeZone(source.getTimeZone())
                .build();
    }

    @Override
    protected DealershipJpaEntity toPersistence(Dealership source) {

        if (source == null) {
            return null;
        }

        return DealershipJpaEntity.builder()
                .id(source.getId())
                .deleted(source.getIsDeleted())
                .name(source.getName())
                .timeZone(source.getTimeZone())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceTypeData> findActiveByDealershipId(UUID dealershipId) {

        return jdbcTemplate.query(
                """
                        SELECT DISTINCT
                            st.id,
                            st.name,
                            st.description,
                            st.duration_minutes
                        FROM service_type st
                        INNER JOIN technician_qualification tq
                            ON tq.service_type_id = st.id
                        INNER JOIN technician t
                            ON t.id = tq.technician_id
                        WHERE t.dealership_id = ?
                          AND t.is_deleted = false
                          AND t.active = true
                          AND st.is_deleted = false
                        ORDER BY st.name
                        """,
                (rs, rowNum) -> new ServiceTypeData(
                        rs.getObject("id", UUID.class),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getObject("duration_minutes", Integer.class)),
                dealershipId);
    }

}