package com.keyloop.scheduler.persistence.seeddata;

import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerSeeder {

    public static final UUID CUSTOMER_001 =
        UUID.fromString("30000000-0000-0000-0000-000000000001");

    public static final UUID CUSTOMER_002 =
        UUID.fromString("30000000-0000-0000-0000-000000000002");

    public void seed(JdbcTemplate jdbc) {
        seedCustomers(jdbc);
    }

    private void seedCustomers(JdbcTemplate jdbc) {
        jdbc.update("""
            INSERT INTO customer (
                id,
                full_name,
                email,
                phone
            )
            VALUES
                (?, 'Nguyen Van An', 'an.nguyen@example.com', '0901234567'),
                (?, 'Tran Thi Binh', 'binh.tran@example.com', '0912345678')
            ON CONFLICT (id) DO NOTHING
            """,
            CUSTOMER_001,
            CUSTOMER_002
        );
    }
}
