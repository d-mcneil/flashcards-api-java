package dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcUserDeckPracticeSettingsDao implements UserDeckPracticeSettingsDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcUserDeckPracticeSettingsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


}
