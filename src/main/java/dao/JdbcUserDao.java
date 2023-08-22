package dao;

import exception.DaoException;
import model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcUserDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int getUserIdByUsername(String username) {
        int userId = 0;
        String sql = "SELECT user_id FROM users WHERE username = ?;";
        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, userId);
            if (sqlRowSet.next()) {
                userId = sqlRowSet.getInt("user_id");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
        return userId;
    }

    @Override
    public User getUserByUserId(int userId) {
        User user = null;
        String sql =
                "SELECT " +
                        "user_id, " +
                        "username, " +
                        "first_name, " +
                        "last_name, " +
                        "email, " +
                        "joined_date, " +
                        "is_user_active " +
                "FROM users " +
                "WHERE user_id = ? AND is_user_active = true;";
        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, userId);
            if (sqlRowSet.next()) {
                user = mapRowToUser(sqlRowSet);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        User updatedUser;
        String sql = "UPDATE users (username, first_name, last_name, email) " +
                "SET username = ?, first_name = ?, last_name = ?, email = ? " +
                "WHERE user_id = ? AND is_user_active = true;";
        try {
            int rowsAffected = jdbcTemplate.update(
                    sql,
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getUserId()
            );
            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected one.");
            }
            updatedUser = getUserByUserId(user.getUserId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return updatedUser;
    }

    @Override
    public int deleteUserById(int userId) {
        int rowsDeleted = 0;
        String sql = "DELETE FROM users WHERE user_id = ?;";
        // String sql = "UPDATE users SET is_user_active = false WHERE user_id = ?"; // This would be the SQL String if I were to use a boolean to "delete" users instead of actually deleting them.
        try {
            rowsDeleted = jdbcTemplate.update(sql, userId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return rowsDeleted;
    }

    private User mapRowToUser(SqlRowSet sqlRowSet) {
        User user = new User();
        user.setUserId(sqlRowSet.getInt("deck_id"));
        user.setUsername(sqlRowSet.getString("username"));
        user.setFirstName(sqlRowSet.getString("first_name"));
        user.setLastName(sqlRowSet.getString("last_name"));
        user.setEmail(sqlRowSet.getString("email"));
        user.setJoinedDate(sqlRowSet.getTimestamp("joined_date"));
        user.setUserActive(sqlRowSet.getBoolean("is_user_active"));
        return user;
    }
}