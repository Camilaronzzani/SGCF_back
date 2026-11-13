package sgcf_back.camilaronzzani.com.github.sgcf_back.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Slf4j
public class DataFixConfiguration {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void fixEnumCaseData() {
        try {
            log.info("Fixing enum case mismatches in database...");
            
            // Drop check constraints
            jdbcTemplate.execute("ALTER TABLE customer DROP CONSTRAINT IF EXISTS customer_country_of_customer_check");
            jdbcTemplate.execute("ALTER TABLE tour DROP CONSTRAINT IF EXISTS tour_country_of_tour_check");
            jdbcTemplate.execute("ALTER TABLE payment DROP CONSTRAINT IF EXISTS payment_status_check");
            jdbcTemplate.execute("ALTER TABLE reservation DROP CONSTRAINT IF EXISTS reservation_status_check");
            jdbcTemplate.execute("ALTER TABLE \"user\" DROP CONSTRAINT IF EXISTS user_permission_check");
            jdbcTemplate.execute("ALTER TABLE clientes_languages DROP CONSTRAINT IF EXISTS clientes_languages_language_check");
            jdbcTemplate.execute("ALTER TABLE languages_funcionario DROP CONSTRAINT IF EXISTS languages_funcionario_language_check");
            
            // Fix customer table
            jdbcTemplate.execute(
                "UPDATE customer SET country_of_customer = CASE " +
                "WHEN LOWER(country_of_customer) = 'brazil' THEN 'BRAZIL' " +
                "WHEN LOWER(country_of_customer) = 'paraguay' THEN 'PARAGUAY' " +
                "WHEN LOWER(country_of_customer) = 'united_states' THEN 'UNITED_STATES' " +
                "WHEN LOWER(country_of_customer) = 'europe' THEN 'EUROPE' " +
                "ELSE country_of_customer END " +
                "WHERE country_of_customer IS NOT NULL"
            );
            
            // Fix tour table
            jdbcTemplate.execute(
                "UPDATE tour SET country_of_tour = CASE " +
                "WHEN LOWER(country_of_tour) = 'argentina' THEN 'ARGENTINA' " +
                "WHEN LOWER(country_of_tour) = 'brazil' THEN 'BRAZIL' " +
                "WHEN LOWER(country_of_tour) = 'paraguay' THEN 'PARAGUAY' " +
                "ELSE country_of_tour END " +
                "WHERE country_of_tour IS NOT NULL"
            );
            
            // Fix payment table
            jdbcTemplate.execute(
                "UPDATE payment SET status = CASE " +
                "WHEN LOWER(status) = 'pending' THEN 'PENDING' " +
                "WHEN LOWER(status) = 'cancelled' THEN 'CANCELLED' " +
                "WHEN LOWER(status) = 'confirmed' THEN 'CONFIRMED' " +
                "ELSE status END " +
                "WHERE status IS NOT NULL"
            );
            
            // Fix reservation table
            jdbcTemplate.execute(
                "UPDATE reservation SET status = CASE " +
                "WHEN LOWER(status) = 'pending' THEN 'PENDING' " +
                "WHEN LOWER(status) = 'cancelled' THEN 'CANCELLED' " +
                "WHEN LOWER(status) = 'confirmed' THEN 'CONFIRMED' " +
                "ELSE status END " +
                "WHERE status IS NOT NULL"
            );
            
            // Fix user table
            jdbcTemplate.execute(
                "UPDATE \"user\" SET permission = CASE " +
                "WHEN LOWER(permission) = 'manager' THEN 'MANAGER' " +
                "WHEN LOWER(permission) = 'employee' THEN 'EMPLOYEE' " +
                "ELSE permission END " +
                "WHERE permission IS NOT NULL"
            );
            
            // Fix clientes_languages table
            jdbcTemplate.execute(
                "UPDATE clientes_languages SET language = CASE " +
                "WHEN LOWER(language) = 'spanish' THEN 'SPANISH' " +
                "WHEN LOWER(language) = 'portuguese' THEN 'PORTUGUESE' " +
                "WHEN LOWER(language) = 'english' THEN 'ENGLISH' " +
                "ELSE language END " +
                "WHERE language IS NOT NULL"
            );
            
            // Fix languages_funcionario table
            jdbcTemplate.execute(
                "UPDATE languages_funcionario SET language = CASE " +
                "WHEN LOWER(language) = 'spanish' THEN 'SPANISH' " +
                "WHEN LOWER(language) = 'portuguese' THEN 'PORTUGUESE' " +
                "WHEN LOWER(language) = 'english' THEN 'ENGLISH' " +
                "ELSE language END " +
                "WHERE language IS NOT NULL"
            );
            
            // Recreate check constraints with correct values
            jdbcTemplate.execute("ALTER TABLE customer ADD CONSTRAINT customer_country_of_customer_check CHECK (country_of_customer IN ('BRAZIL', 'PARAGUAY', 'UNITED_STATES', 'EUROPE'))");
            jdbcTemplate.execute("ALTER TABLE tour ADD CONSTRAINT tour_country_of_tour_check CHECK (country_of_tour IN ('ARGENTINA', 'BRAZIL', 'PARAGUAY'))");
            jdbcTemplate.execute("ALTER TABLE payment ADD CONSTRAINT payment_status_check CHECK (status IN ('PENDING', 'CANCELLED', 'CONFIRMED'))");
            jdbcTemplate.execute("ALTER TABLE reservation ADD CONSTRAINT reservation_status_check CHECK (status IN ('PENDING', 'CANCELLED', 'CONFIRMED'))");
            jdbcTemplate.execute("ALTER TABLE \"user\" ADD CONSTRAINT user_permission_check CHECK (permission IN ('MANAGER', 'EMPLOYEE'))");
            jdbcTemplate.execute("ALTER TABLE clientes_languages ADD CONSTRAINT clientes_languages_language_check CHECK (language IN ('SPANISH', 'PORTUGUESE', 'ENGLISH'))");
            jdbcTemplate.execute("ALTER TABLE languages_funcionario ADD CONSTRAINT languages_funcionario_language_check CHECK (language IN ('SPANISH', 'PORTUGUESE', 'ENGLISH'))");
            
            log.info("Enum case fix completed successfully");
        } catch (Exception e) {
            log.error("Failed to fix enum case data", e);
        }
    }
}
