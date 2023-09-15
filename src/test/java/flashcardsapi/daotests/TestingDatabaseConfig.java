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
        /*
        The following commented out portion would be used if we wanted to create a new database every time we run tests.
        However, testing for Flashcards API was having an issue with functions and triggers declared in the SQL script,
        so for that project, the decision was made to build the testing database on the server and leave it open between
        tests. All changes are rolled back after each test, but the testing database itself persists beyond testing.
         */
        // drop and recreate testing database under "admin" connection
        //        adminDataSource = new SingleConnectionDataSource();
        //        adminDataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        //        adminDataSource.setUsername("postgres");
        //        adminDataSource.setPassword("postgres1");
        //        adminJdbcTemplate = new JdbcTemplate(adminDataSource);
        //        adminJdbcTemplate.update("DROP DATABASE IF EXISTS \"<TestingDatabaseName>\";");
        //        adminJdbcTemplate.update("CREATE DATABASE \"<TestingDatabaseName>\";");

        // set up the testing connection
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/FlashcardsTesting");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        dataSource.setAutoCommit(false); // roll back after each test

        // refresh testing database with setup script and add test data
        /*
        The following commented out line would be used if we wanted to create a new database every time we run tests.
        However, testing for Flashcards API was having an issue with functions and triggers declared in the SQL script,
        so for that project, the decision was made to build the testing database on the server and leave it open between
        tests. All changes are rolled back after each test, but the testing database itself persists beyond testing.
        */
        //        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("<database-file-name>.sql"));
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-data.sql"));

        return dataSource;
    }

    /*
    The following commented out portion would be used if we wanted to create a new database every time we run tests.
    However, testing for Flashcards API was having an issue with functions and triggers declared in the SQL script,
    so for that project, the decision was made to build the testing database on the server and leave it open between
    tests. All changes are rolled back after each test, but the testing database itself persists beyond testing.
     */
    //    @PreDestroy
    //    public void cleanup() {
    //        if (adminDataSource != null) {
    //            adminJdbcTemplate.update("DROP DATABASE \"<TestingDatabaseName>\";");
    //            adminDataSource.destroy();
    //        }
    //    }
}
