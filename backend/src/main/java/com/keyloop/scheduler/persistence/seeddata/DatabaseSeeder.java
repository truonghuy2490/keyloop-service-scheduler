
package com.keyloop.scheduler.persistence.seeddata;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseSeeder {

    @Bean
    ApplicationRunner seedDatabase(
        JdbcTemplate jdbc,
        DealershipSeeder dealershipSeeder,
        VehicleSeeder vehicleSeeder,
        CustomerSeeder customerSeeder
    ) {
        return args -> {
            dealershipSeeder.seed(jdbc);
            customerSeeder.seed(jdbc);
            vehicleSeeder.seed(jdbc);

            System.out.println("Database seed completed.");
        };
    }
}
