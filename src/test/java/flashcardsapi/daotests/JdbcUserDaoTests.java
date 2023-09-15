package flashcardsapi.daotests;

import flashcardsapi.model.dao.JdbcUserDao;
import flashcardsapi.exception.DaoException;
import flashcardsapi.model.models.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

public class JdbcUserDaoTests extends BaseDaoTests {
    // From BaseDaoTests.java
    // protected User TEST_USER_1; <-- inactive
    // protected User TEST_USER_2; <-- active

    private JdbcUserDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcUserDao(jdbcTemplate);
    }

// ############################################################################################################
// int getUserIdByUsername(String username)
// ############################################################################################################

    @Test
    public void getUserIdByUsername_returns_correct_user_id_for_user_that_is_active() {
        int userId = sut.getUserIdByUsername(TEST_USER_2.getUsername());
        Assert.assertEquals(TEST_USER_2.getUserId(), userId);
    }

    @Test
    public void getUserIdByUsername_returns_negative_1_for_user_that_is_not_active() {
        int nonExistentUserId = sut.getUserIdByUsername(TEST_USER_1.getUsername());
        Assert.assertEquals(-1, nonExistentUserId);
    }

    @Test
    public void getUserIdByUsername_returns_negative_1_for_user_that_does_not_exist() {
        int nonExistentUserId = sut.getUserIdByUsername("xxxxxxxxx");
        Assert.assertEquals(-1, nonExistentUserId);
    }

    @Test
    public void getUserIdByUsername_returns_negative_1_for_empty_string_username() {
        int nonExistentUserId = sut.getUserIdByUsername("");
        Assert.assertEquals(-1, nonExistentUserId);
    }

    @Test
    public void getUserIdByUsername_returns_negative_1_for_null_username() {
        int nonExistentUserId = sut.getUserIdByUsername(null);
        Assert.assertEquals(-1, nonExistentUserId);
    }

// ############################################################################################################
// User getUserByUserId(int userId)
// ############################################################################################################

    @Test
    public void getUserByUserId_returns_correct_user_information_for_user_that_is_active() {
        User user2 = sut.getUserByUserId(TEST_USER_2.getUserId());
        assertUsersMatch(TEST_USER_2, user2);
    }

    @Test
    public void getUserByUserId_returns_null_for_user_that_is_not_active() {
        User user1 = sut.getUserByUserId(TEST_USER_1.getUserId());
        Assert.assertNull(user1);
    }

    @Test
    public void getUserByUserId_returns_null_for_user_that_does_not_exist() {
        User nullUser = sut.getUserByUserId(-1);
        Assert.assertNull(nullUser);
    }

// ############################################################################################################
// User updateUser(User user)
// ############################################################################################################

    @Test
    public void updateUser_returns_correct_user_information_and_user_has_correct_information_when_retrieved() {
        User userToUpdate = sut.getUserByUserId(1002);
        userToUpdate.setEmail("updated@example.com");
        userToUpdate.setUsername("updatedUsername");
        userToUpdate.setFirstName("updatedFirst");
        userToUpdate.setLastName("updatedLast");

        User updatedUser = sut.updateUser(userToUpdate);

        User retrievedUser = sut.getUserByUserId(1002);

        assertUsersMatch(userToUpdate, retrievedUser);
        assertUsersMatch(userToUpdate, updatedUser);
    }

    @Test
    public void updateUser_throws_dao_exception_when_user_is_not_active() {
        User userToUpdate = new User();
        userToUpdate.setUserId(1001);
        userToUpdate.setEmail("updated@example.com");
        userToUpdate.setUsername("updatedUsername");
        userToUpdate.setFirstName("updatedFirst");
        userToUpdate.setLastName("updatedLast");

        try {
            sut.updateUser(userToUpdate);
            Assert.fail("Method did not throw exception as expected when user was inactive.");
        } catch (DaoException e) {
            Assert.assertFalse(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertFalse(e.getCause() instanceof CannotGetJdbcConnectionException);
            Assert.assertNull(e.getCause());
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when user was inactive.");
        }
    }

    @Test
    public void updateUser_throws_dao_exception_when_user_does_not_exist() {
        User userToUpdate = new User();
        userToUpdate.setUserId(-1);
        userToUpdate.setEmail("updated@example.com");
        userToUpdate.setUsername("updatedUsername");
        userToUpdate.setFirstName("updatedFirst");
        userToUpdate.setLastName("updatedLast");

        try {
            sut.updateUser(userToUpdate);
            Assert.fail("Method did not throw exception as expected when user was inactive.");
        } catch (DaoException e) {
            Assert.assertFalse(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertFalse(e.getCause() instanceof CannotGetJdbcConnectionException);
            Assert.assertNull(e.getCause());
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when user was inactive.");
        }
    }

    @Test
    public void updateUser_throws_data_integrity_violation_when_field_violates_constraint() {
        User userToUpdate = sut.getUserByUserId(1002);
        userToUpdate.setEmail("updated@example.com");
        userToUpdate.setUsername("updatedUsername");
        userToUpdate.setFirstName("updatedFirst");
        userToUpdate.setLastName(null);

        try {
            sut.updateUser(userToUpdate);
            Assert.fail("Method did not throw exception as expected when last name was null.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [UPDATE users SET username = ?, first_name = ?, last_name = ?, email = ? WHERE user_id = ? AND is_user_active = true;]; ERROR: null value in column \"last_name\" of relation \"users\" violates not-null constraint\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: null value in column \"last_name\" of relation \"users\" violates not-null constraint"));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when last name was null.");
        }
    }

// ############################################################################################################
// int deleteUserById(int userId)
// ############################################################################################################

    @Test
    public void deleteUserById_returns_1_when_user_successfully_deleted_and_deleted_user_cannot_be_retrieved() {
        int rowsDeleted = sut.deleteUserById(1001);
        User retrievedUser = sut.getUserByUserId(1001);

        Assert.assertNull("Deleted user can still be retrieved.", retrievedUser);
        Assert.assertEquals(1, rowsDeleted);
    }

// ****************************************************************************************************************************************************************
// ********************************* Login Methods ****************************************************************************************************************
// ****************************************************************************************************************************************************************

// #################################################################
// String getHashedPasswordByUsername(String username)
// #################################################################

    @Test
    public void getHashedPasswordByUsername_returns_correct_hash_for_user_that_is_active() {
        String hash2 = sut.getHashedPasswordByUsername("username2");
        Assert.assertEquals("2".repeat(60), hash2);
    }

    @Test
    public void getHashedPasswordByUsername_returns_null_for_user_that_is_not_active() {
        String nullHash = sut.getHashedPasswordByUsername("username1");
        Assert.assertNull(nullHash);
    }

    @Test
    public void getHashedPasswordByUsername_returns_null_for_user_that_does_not_exist() {
        String nullHash = sut.getHashedPasswordByUsername("xxxxxxxxx");
        Assert.assertNull(nullHash);
    }

    @Test
    public void getHashedPasswordByUsername_returns_null_for_null_username() {
        String nullHash = sut.getHashedPasswordByUsername(null);
        Assert.assertNull(nullHash);
    }

    @Test
    public void getHashedPasswordByUsername_returns_null_for_empty_string_username() {
        String nullHash = sut.getHashedPasswordByUsername("");
        Assert.assertNull(nullHash);
    }

// #################################################################
// User createUser(User user, String hashedPassword)
// #################################################################

    @Test
    public void createUser_returns_correct_user_information_and_correctly_sets_hashed_password_and_user_has_correct_information_when_retrieved() {
        // --------------------- Arrange -------------------------------------
        User testUser = new User();
        testUser.setUsername("newuser".repeat(9));
        testUser.setFirstName("new");
        testUser.setLastName("user");
        testUser.setEmail("a@b.c");
        testUser.setUserActive(true);

        // --------------------- Act -----------------------------------------
        // create new user in database
        User createdUser = sut.createUser(testUser, "3".repeat(60));
        int createdUserId = createdUser.getUserId();

        // The testing database will persist between tests, so the user_id of the createdUser is not predictable. // TODO: this isn't true if testing database is destroyed after running tests (for meals app)
        // Here we set the user id for the original user to equal the id that was generated by the database.
        // Later we will test to make sure it is greater than the minimum id for users.
        testUser.setUserId(createdUserId);

        // The database generates the timestamp by default.
        // Here we set the joined date for the original user to equal the date that was generated by the database.
        // Later we will test to make sure the date generated by the database is basically the same as LocalDateTime.now()
        testUser.setJoinedDate(createdUser.getJoinedDate());

        // Retrieve the user that was just created from the database
        User retrievedUser = sut.getUserByUserId(createdUserId);

        // The function that we are testing creates a row in the users table and a row in the login table, so that needs to be tested as well
        String hashedPassword = sut.getHashedPasswordByUsername("newuser".repeat(9));

        // --------------------- Assert --------------------------------------
        assertUsersMatch(testUser, retrievedUser);
        assertUsersMatch(createdUser, retrievedUser);

        Assert.assertEquals("3".repeat(60), hashedPassword);

        // here we are checking to make sure the id generated by the database is greater than the minimum possible
        Assert.assertTrue(createdUserId > 1002);

        // here we are checking to make sure the timestamp generated by the database is actually valid by comparing it to LocalDateTime.now()
        LocalDateTime rightNow = LocalDateTime.now();
        LocalDateTime createdUserTimestamp = createdUser.getJoinedDate().toLocalDateTime();
        if (createdUserTimestamp.getYear() != rightNow.getYear() ||
                createdUserTimestamp.getMonth() != rightNow.getMonth() ||
                createdUserTimestamp.getDayOfMonth() != rightNow.getDayOfMonth() ||
                createdUserTimestamp.getHour() != rightNow.getHour() ||
                createdUserTimestamp.getMinute() != rightNow.getMinute() ||
                createdUserTimestamp.getSecond() != rightNow.getSecond()
        ) {
            Assert.fail("Timestamp generated by database does not match timestamp generated by server.");
        }

    }

    @Test
    public void createUser_throws_data_integrity_violation_when_case_insensitive_username_already_exists() {
        User testUser = new User();
        testUser.setUsername("usErName1");
        testUser.setFirstName("new");
        testUser.setLastName("user");
        testUser.setEmail("newuser@example.com");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when duplicate username was added.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertEquals("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: duplicate key value violates unique constraint \"uq_username\"\n" +
                    "  Detail: Key (username)=(usErName1) already exists.; nested exception is org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"uq_username\"\n" +
                    "  Detail: Key (username)=(usErName1) already exists.", e.getCause().getMessage());
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when duplicate username was added.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_username_is_null() {
        User testUser = new User();
        testUser.setUsername(null);
        testUser.setFirstName("new");
        testUser.setLastName("user");
        testUser.setEmail("newuser@example.com");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when username was null.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: null value in column \"username\" of relation \"users\" violates not-null constraint\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: null value in column \"username\" of relation \"users\" violates not-null constraint"));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when username was null.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_username_is_empty_string() {
        User testUser = new User();
        testUser.setUsername("");
        testUser.setFirstName("new");
        testUser.setLastName("user");
        testUser.setEmail("newuser@example.com");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when username was empty string.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: new row for relation \"users\" violates check constraint \"chk_username\"\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: new row for relation \"users\" violates check constraint \"chk_username\""));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when username was empty string.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_username_is_more_than_63_characters() {
        User testUser = new User();
        testUser.setUsername("a".repeat(64));
        testUser.setFirstName("new");
        testUser.setLastName("user");
        testUser.setEmail("newuser@example.com");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when username was more than 63 characters.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: new row for relation \"users\" violates check constraint \"chk_username\"\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: new row for relation \"users\" violates check constraint \"chk_username\""));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when username was more than 63 characters.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_first_name_is_null() {
        User testUser = new User();
        testUser.setUsername("a");
        testUser.setFirstName(null);
        testUser.setLastName("user");
        testUser.setEmail("newuser@example.com");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when first name was null.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: null value in column \"first_name\" of relation \"users\" violates not-null constraint\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: null value in column \"first_name\" of relation \"users\" violates not-null constraint"));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when first name was null.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_first_name_is_empty_string() {
        User testUser = new User();
        testUser.setUsername("newuser".repeat(9));
        testUser.setFirstName("");
        testUser.setLastName("user");
        testUser.setEmail("newuser@example.com");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when first name was empty string.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: new row for relation \"users\" violates check constraint \"chk_first_name\"\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: new row for relation \"users\" violates check constraint \"chk_first_name\""));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when first name was empty string.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_first_name_is_more_than_63_characters() {
        User testUser = new User();
        testUser.setUsername("a");
        testUser.setFirstName("a".repeat(64));
        testUser.setLastName("user");
        testUser.setEmail("newuser@example.com");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when first name was more than 63 characters.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertEquals("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: value too long for type character varying(63); nested exception is org.postgresql.util.PSQLException: ERROR: value too long for type character varying(63)", e.getCause().getMessage());
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when first name was more than 63 characters.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_last_name_is_null() {
        User testUser = new User();
        testUser.setUsername("a");
        testUser.setFirstName("new");
        testUser.setLastName(null);
        testUser.setEmail("newuser@example.com");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when last name was null.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: null value in column \"last_name\" of relation \"users\" violates not-null constraint\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: null value in column \"last_name\" of relation \"users\" violates not-null constraint"));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when last name was null.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_last_name_is_empty_string() {
        User testUser = new User();
        testUser.setUsername("newuser".repeat(9));
        testUser.setFirstName("new");
        testUser.setLastName("");
        testUser.setEmail("newuser@example.com");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when last name was empty string.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: new row for relation \"users\" violates check constraint \"chk_last_name\"\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: new row for relation \"users\" violates check constraint \"chk_last_name\""));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when last name was empty string.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_last_name_is_more_than_63_characters() {
        User testUser = new User();
        testUser.setUsername("a");
        testUser.setFirstName("a");
        testUser.setLastName("a".repeat(64));
        testUser.setEmail("newuser@example.com");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when last name was more than 63 characters.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertEquals("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: value too long for type character varying(63); nested exception is org.postgresql.util.PSQLException: ERROR: value too long for type character varying(63)", e.getCause().getMessage());
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when last name was more than 63 characters.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_email_is_null() {
        User testUser = new User();
        testUser.setUsername("a");
        testUser.setFirstName("new");
        testUser.setLastName("user");
        testUser.setEmail(null);
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when email was null.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: null value in column \"email\" of relation \"users\" violates not-null constraint\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("; nested exception is org.postgresql.util.PSQLException: ERROR: null value in column \"email\" of relation \"users\" violates not-null constraint"));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when email was null.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_email_is_less_than_5_characters() {
        User testUser = new User();
        testUser.setUsername("a");
        testUser.setFirstName("a");
        testUser.setLastName("a");
        testUser.setEmail("a@b.");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when email was less than 5 characters.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: new row for relation \"users\" violates check constraint \"chk_email\"\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: new row for relation \"users\" violates check constraint \"chk_email\""));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when email was less than 5 characters.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_email_is_more_than_127_characters() {
        User testUser = new User();
        testUser.setUsername("a");
        testUser.setFirstName("a");
        testUser.setLastName("a");
        testUser.setEmail("a@b.c" + "a".repeat(123));
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when email was more than 127 characters.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertEquals("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: value too long for type character varying(127); nested exception is org.postgresql.util.PSQLException: ERROR: value too long for type character varying(127)", e.getCause().getMessage());
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when email was more than 127 characters.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_email_has_no_at_sign() {
        User testUser = new User();
        testUser.setUsername("a");
        testUser.setFirstName("a");
        testUser.setLastName("a");
        testUser.setEmail("aa.aa");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when email didn't have at sign.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: new row for relation \"users\" violates check constraint \"chk_email\"\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: new row for relation \"users\" violates check constraint \"chk_email\""));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when email didn't have at sign.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_email_has_no_period() {
        User testUser = new User();
        testUser.setUsername("a");
        testUser.setFirstName("a");
        testUser.setLastName("a");
        testUser.setEmail("aa@aa");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when email didn't have period.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: new row for relation \"users\" violates check constraint \"chk_email\"\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: new row for relation \"users\" violates check constraint \"chk_email\""));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when email didn't have period.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_email_has_nothing_after_period() {
        User testUser = new User();
        testUser.setUsername("a");
        testUser.setFirstName("a");
        testUser.setLastName("a");
        testUser.setEmail("aa@a.");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when email didn't have anything after period.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: new row for relation \"users\" violates check constraint \"chk_email\"\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: new row for relation \"users\" violates check constraint \"chk_email\""));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when email didn't have anything after period.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_email_has_nothing_before_at_sign() {
        User testUser = new User();
        testUser.setUsername("a");
        testUser.setFirstName("a");
        testUser.setLastName("a");
        testUser.setEmail("@a.aa");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when email didn't have anything before at sign.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: new row for relation \"users\" violates check constraint \"chk_email\"\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: new row for relation \"users\" violates check constraint \"chk_email\""));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when email didn't have anything before at sign.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_email_has_nothing_between_at_sign_and_period() {
        User testUser = new User();
        testUser.setUsername("a");
        testUser.setFirstName("a");
        testUser.setLastName("a");
        testUser.setEmail("aa@.a");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(60));
            Assert.fail("Method did not throw exception as expected when email didn't have anything between at sign and period.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: new row for relation \"users\" violates check constraint \"chk_email\"\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: new row for relation \"users\" violates check constraint \"chk_email\""));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when email didn't have anything between at sign and period.");
        }
    }

    //    This test does not test that the user wasn't created if the hashed password wasn't 60 characters (i.e., it
    //    doesn't test to see that the transaction was rolled back). I did test the transaction with Postman, and it
    //    did rollback, but adding that check into the test breaks the test because once an exception happens during
    //    the test, it says the transaction has been aborted and won't let any more queries be run against the
    //    database until the block ends (this is true whether the DAO method createUser is labelled transactional,
    //    which tells me that it is the Spring transaction and not the DAO method transaction that is causing the problem,
    //    and the exception it throws is: org.springframework.jdbc.UncategorizedSQLException). Adding this check to the
    //    test over-complicates the process, and I know it works, so I don't think this check should be included in the test.
    @Test
    // TODO: Include in the test that the user wasn't actually created (see note above).
    public void createUser_throws_data_integrity_violation_when_hashed_password_is_not_60_characters() {
        User testUser = new User();
        testUser.setUsername("newuser".repeat(9));
        testUser.setFirstName("new");
        testUser.setLastName("user");
        testUser.setEmail("a@b.c");
        testUser.setUserActive(true);

        try {
            sut.createUser(testUser, "3".repeat(59));
            Assert.fail("Method did not throw exception as expected when hashed password was not 60 characters.");
        } catch (DaoException e) {
            Assert.assertTrue(e.getCause() instanceof DataIntegrityViolationException);
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO login (user_id, hashed_password) VALUES (?, ?) RETURNING user_id;]; ERROR: new row for relation \"login\" violates check constraint \"chk_hashed_password\"\n"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: new row for relation \"login\" violates check constraint \"chk_hashed_password\""));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when hashed password was not 60 characters.");
        }
    }

// ****************************************************************************************************************************************************************
// ********************************* Convenience Method ***********************************************************************************************************
// ****************************************************************************************************************************************************************

    private void assertUsersMatch(User expected, User actual) {
        Assert.assertEquals(expected.getUserId(), actual.getUserId());
        Assert.assertEquals(expected.getUsername(), actual.getUsername());
        Assert.assertEquals(expected.getFirstName(), actual.getFirstName());
        Assert.assertEquals(expected.getLastName(), actual.getLastName());
        Assert.assertEquals(expected.getEmail(), actual.getEmail());
        Assert.assertEquals(expected.getJoinedDate(), actual.getJoinedDate());
        Assert.assertEquals(expected.isUserActive(), actual.isUserActive());
    }
}
