package dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcPracticeSettingsDao implements PracticeSettingsDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcPracticeSettingsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


}
