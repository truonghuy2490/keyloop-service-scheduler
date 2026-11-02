
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
        CustomerSeeder customerSeeder,
        ServiceBaySeeder serviceBaySeeder,
        ServiceTypeSeeder serviceTypeSeeder,
        TechnicianSeeder technicianSeeder,
        TechnicianQualificationSeeder technicianQualificationSeeder
    ) {
        return args -> {
            dealershipSeeder.seed(jdbc);
            customerSeeder.seed(jdbc);
            vehicleSeeder.seed(jdbc);
            serviceBaySeeder.seed(jdbc);
            serviceTypeSeeder.seed(jdbc);
            technicianSeeder.seed(jdbc);
            technicianQualificationSeeder.seed(jdbc);
            
            System.out.println("Database seed completed.");
        };
    }
}
