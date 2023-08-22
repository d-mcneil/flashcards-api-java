package dao;

import exception.DaoException;
import model.Login;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JdbcLoginDao implements LoginDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcLoginDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Login getLoginByUserId(int userId) {
        Login login = null;
        String sql = "SELECT hashed_password FROM login WHERE user_id = ?;";
        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, userId);
            if (sqlRowSet.next()) {
                login = mapRowToLogin(sqlRowSet);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
        return login;
    }

    private Login mapRowToLogin(SqlRowSet sqlRowSet) {
        Login login = new Login();
        login.setHashedPassword(sqlRowSet.getString("hashed_password"));
        return login;
    }
}
