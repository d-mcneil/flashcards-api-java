package flashcardsapi.daotests;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class TestingDatabaseConfig {
    private SingleConnectionDataSource adminDataSource;
    private JdbcTemplate adminJdbcTemplate;

    @Bean
    public DataSource dataSource() throws SQLException {
        // Drop and then recreate the testing database under separate "admin" connection
        adminDataSource = new SingleConnectionDataSource();
        adminDataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        adminDataSource.setUsername("postgres");
        adminDataSource.setPassword("postgres1");
        JdbcTemplate adminJdbcTemplate = new JdbcTemplate(adminDataSource);
        adminJdbcTemplate.update("DROP DATABASE IF EXISTS \"FlashcardsTesting\";");
        adminJdbcTemplate.update("CREATE DATABASE \"FlashcardsTesting\";");

        // Setup up the testing connection
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/FlashcardsTesting");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        dataSource.setAutoCommit(false); // So we can rollback after each test.

        // Refresh the testing database by running the setup script
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("database/flashcards.sql"));

        return dataSource;
    }

    @PreDestroy
    public void cleanup() {
        if (adminDataSource != null) {
            adminJdbcTemplate.update("DROP DATABASE \"FlashcardsTesting\";");
            adminDataSource.destroy();
        }
    }
}
