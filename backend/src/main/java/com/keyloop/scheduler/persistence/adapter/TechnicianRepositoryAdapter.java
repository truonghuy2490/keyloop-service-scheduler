package com.keyloop.scheduler.persistence.adapter;

import java.util.List;
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

    @Override
    @Transactional(readOnly = true)
    public List<TechnicianDetailsData> findDetailsByDealershipId(UUID dealershipId) {

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
                (rs, rowNum) -> new TechnicianDetailsData(
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
}
