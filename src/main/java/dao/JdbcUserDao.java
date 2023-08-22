package dao;

import model.Deck;
import model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcUserDao implements UserDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // getUserById
    // updateUser
    // deleteUser

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