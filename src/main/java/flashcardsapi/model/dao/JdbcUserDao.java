package flashcardsapi.model.dao;

import flashcardsapi.exception.DaoException;
import flashcardsapi.exception.exceptionmessages.ExceptionMessages;
import flashcardsapi.model.models.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class JdbcUserDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int getUserIdByUsername(String username) {
        int userId = -1;
        String sql = "SELECT user_id FROM users WHERE username = ? AND is_user_active = true;";
        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, username);
            if (sqlRowSet.next()) {
                userId = sqlRowSet.getInt("user_id");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException(ExceptionMessages.CANNOT_GET_JDBC_CONNECTION_EXCEPTION_MESSAGE, e);
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
            throw new DaoException(ExceptionMessages.CANNOT_GET_JDBC_CONNECTION_EXCEPTION_MESSAGE, e);
        }
        return user;
    }


    @Override
    public User updateUser(User user) {
        User updatedUser;
        String sql = "UPDATE users " +
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
                throw new DaoException(ExceptionMessages.DAO_EXCEPTION_ZERO_ROWS_AFFECTED_MESSAGE);
            }
            updatedUser = getUserByUserId(user.getUserId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException(ExceptionMessages.CANNOT_GET_JDBC_CONNECTION_EXCEPTION_MESSAGE, e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException(ExceptionMessages.DATA_INTEGRITY_VIOLATION_EXCEPTION_MESSAGE, e);
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
            throw new DaoException(ExceptionMessages.CANNOT_GET_JDBC_CONNECTION_EXCEPTION_MESSAGE, e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException(ExceptionMessages.DATA_INTEGRITY_VIOLATION_EXCEPTION_MESSAGE, e);
        }
        return rowsDeleted;
    }

// ****************************************************************************************************************************************************************
// ********************************* Login Methods ****************************************************************************************************************
// ****************************************************************************************************************************************************************

    @Override
    @Transactional
    public User createUser(User user, String hashedPassword) {
        User newUser;
        String sqlCreateUser =
                "INSERT INTO users (username, first_name, last_name, email) " +
                "VALUES (?, ?, ?, ?) " +
                "RETURNING user_id;";
        String sqlCreateLogin =
                "INSERT INTO login (user_id, hashed_password) " +
                "VALUES (?, ?) " +
                "RETURNING user_id;";
        try {
            Integer userId = jdbcTemplate.queryForObject(
                    sqlCreateUser,
                    Integer.class,
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail()
            );
            if (userId == null) {
                throw new DaoException("Error creating user");
            }
            userId = jdbcTemplate.queryForObject(
                    sqlCreateLogin,
                    Integer.class,
                    userId,
                    hashedPassword
            );
            if (userId == null) {
                throw new DaoException("Error creating login");
            }
            newUser = getUserByUserId(userId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException(ExceptionMessages.CANNOT_GET_JDBC_CONNECTION_EXCEPTION_MESSAGE, e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException(ExceptionMessages.DATA_INTEGRITY_VIOLATION_EXCEPTION_MESSAGE, e);
        }
        return newUser;
    }

    @Override
    public String getHashedPasswordByUsername(String username) {
        String hashedPassword = null;
        String sql = "SELECT hashed_password " +
                "FROM login " +
                "JOIN users ON login.user_id = users.user_id " +
                "WHERE username = ? AND is_user_active = true;";
        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, username);
            if (sqlRowSet.next()) {
                hashedPassword = sqlRowSet.getString("hashed_password");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException(ExceptionMessages.CANNOT_GET_JDBC_CONNECTION_EXCEPTION_MESSAGE, e);
        }
        return hashedPassword;
    }

// ****************************************************************************************************************************************************************
// ********************************* Convenience Method ***********************************************************************************************************
// ****************************************************************************************************************************************************************

    private User mapRowToUser(SqlRowSet sqlRowSet) {
        User user = new User();
        user.setUserId(sqlRowSet.getInt("user_id"));
        user.setUsername(sqlRowSet.getString("username"));
        user.setFirstName(sqlRowSet.getString("first_name"));
        user.setLastName(sqlRowSet.getString("last_name"));
        user.setEmail(sqlRowSet.getString("email"));
        user.setJoinedDate(sqlRowSet.getTimestamp("joined_date"));
        user.setUserActive(sqlRowSet.getBoolean("is_user_active"));
        user.setAuthorities("USER");
        return user;
    }
}