package flashcardsapi.daotests;

import dao.JdbcUserDao;
import exception.DaoException;
import model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
    // TODO: create tests
// ############################################################################################################

// ############################################################################################################
// int deleteUserById(int userId)
    // TODO: create tests
// ############################################################################################################


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
// TODO: create tests for not happy path (hashed password isn't 60 chars, email is too short/long, email doesn't match formatting)

    // SEQUENCES ARE NOT ROLLED BACK WHEN A TRANSACTION GOES THROUGH A ROLLBACK. THEREFORE, THE USER_ID IS DEPENDENT
    // ON THE ORDER THAT TESTS RUN AND THE AMOUNT OF TESTS ATTEMPTING TO CREATE A USER THAT HAVE RUN PREVIOUSLY.
    private static int numberOfTestsThatAttemptToCreateNewUser = 0;

    @Test
    public void createUser_returns_correct_user_information_and_correctly_sets_given_hashed_password_when_user_and_login_are_successfully_created() {
        // SEQUENCES ARE NOT ROLLED BACK WHEN A TRANSACTION GOES THROUGH A ROLLBACK. THEREFORE, THE USER_ID IS DEPENDENT
        // ON THE ORDER THAT TESTS RUN AND THE AMOUNT OF TESTS ATTEMPTING TO CREATE A USER THAT HAVE RUN PREVIOUSLY.
        numberOfTestsThatAttemptToCreateNewUser++;

        // --------------------- Arrange -------------------------------------
        User testUser = new User();
        testUser.setUsername("newuser".repeat(9));
        testUser.setFirstName("new");
        testUser.setLastName("user");
        testUser.setEmail("newuser@example.com");
        testUser.setUserActive(true);

        // --------------------- Act -----------------------------------------
        // create new user in database
        User createdUser = sut.createUser(testUser, "3".repeat(60));
        int createdUserId = createdUser.getUserId();

        // The database generates the timestamp by default
        // Here we set the joined date for the original user to equal the date that was generated by the database.
        // Later we will test to make sure the date generated by the database is basically the same as LocalDateTime.now()
        testUser.setJoinedDate(createdUser.getJoinedDate());

        // SEQUENCES ARE NOT ROLLED BACK WHEN A TRANSACTION GOES THROUGH A ROLLBACK. THEREFORE, THE USER_ID IS DEPENDENT
        // ON THE ORDER THAT TESTS RUN AND THE AMOUNT OF TESTS ATTEMPTING TO CREATE A USER THAT HAVE RUN PREVIOUSLY.
        testUser.setUserId(1002 + numberOfTestsThatAttemptToCreateNewUser);

        // Retrieve the user that was just created from the database
        User retrievedUser = sut.getUserByUserId(createdUserId);

        // The function that we are testing creates a row in the users table and a row in the login table, so that needs to be tested as well
        String hashedPassword = sut.getHashedPasswordByUsername("newuser".repeat(9));

        // --------------------- Assert --------------------------------------
        assertUsersMatch(testUser, retrievedUser);

        Assert.assertEquals("3".repeat(60), hashedPassword);

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
        // SEQUENCES ARE NOT ROLLED BACK WHEN A TRANSACTION GOES THROUGH A ROLLBACK. THEREFORE, THE USER_ID IS DEPENDENT
        // ON THE ORDER THAT TESTS RUN AND THE AMOUNT OF TESTS ATTEMPTING TO CREATE A USER THAT HAVE RUN PREVIOUSLY.
        numberOfTestsThatAttemptToCreateNewUser++;

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
            Assert.assertEquals("Data integrity violation", e.getMessage());
            Assert.assertEquals("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: duplicate key value violates unique constraint \"uq_username\"\n" +
                    "  Detail: Key (username)=(usErName1) already exists.; nested exception is org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"uq_username\"\n" +
                    "  Detail: Key (username)=(usErName1) already exists.", e.getCause().getMessage());
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when duplicate username was added.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_username_is_empty_string() {
        // SEQUENCES ARE NOT ROLLED BACK WHEN A TRANSACTION GOES THROUGH A ROLLBACK. THEREFORE, THE USER_ID IS DEPENDENT
        // ON THE ORDER THAT TESTS RUN AND THE AMOUNT OF TESTS ATTEMPTING TO CREATE A USER THAT HAVE RUN PREVIOUSLY.
        numberOfTestsThatAttemptToCreateNewUser++;

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
            Assert.assertEquals("Data integrity violation", e.getMessage());
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: new row for relation \"users\" violates check constraint \"chk_username\"\n" +
                    "  Detail: Failing row contains (" + (1002 + numberOfTestsThatAttemptToCreateNewUser) + ", , new, user, newuser@example.com,"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: new row for relation \"users\" violates check constraint \"chk_username\""));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when username was empty string.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_first_name_is_null() {
        // SEQUENCES ARE NOT ROLLED BACK WHEN A TRANSACTION GOES THROUGH A ROLLBACK. THEREFORE, THE USER_ID IS DEPENDENT
        // ON THE ORDER THAT TESTS RUN AND THE AMOUNT OF TESTS ATTEMPTING TO CREATE A USER THAT HAVE RUN PREVIOUSLY.
        numberOfTestsThatAttemptToCreateNewUser++;

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
            Assert.assertEquals("Data integrity violation", e.getMessage());
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: null value in column \"first_name\" violates not-null constraint\n" +
                    "  Detail: Failing row contains (" + (1002 + numberOfTestsThatAttemptToCreateNewUser) + ", a, null, user, newuser@example.com,"));
            Assert.assertTrue(e.getCause().getMessage().contains("; nested exception is org.postgresql.util.PSQLException: ERROR: null value in column \"first_name\" violates not-null constraint"));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when first name was null.");
        }
    }

    @Test
    public void createUser_throws_data_integrity_violation_when_last_name_is_empty_string() {
        // SEQUENCES ARE NOT ROLLED BACK WHEN A TRANSACTION GOES THROUGH A ROLLBACK. THEREFORE, THE USER_ID IS DEPENDENT
        // ON THE ORDER THAT TESTS RUN AND THE AMOUNT OF TESTS ATTEMPTING TO CREATE A USER THAT HAVE RUN PREVIOUSLY.
        numberOfTestsThatAttemptToCreateNewUser++;

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
            Assert.assertEquals("Data integrity violation", e.getMessage());
            Assert.assertTrue(e.getCause().getMessage().contains("PreparedStatementCallback; SQL [INSERT INTO users (username, first_name, last_name, email) VALUES (?, ?, ?, ?) RETURNING user_id;]; ERROR: new row for relation \"users\" violates check constraint \"chk_last_name\"\n" +
                    "  Detail: Failing row contains (" + (1002 + numberOfTestsThatAttemptToCreateNewUser) + ", newusernewusernewusernewusernewusernewusernewusernewusernewuser, new, , newuser@example.com,"));
            Assert.assertTrue(e.getCause().getMessage().contains("nested exception is org.postgresql.util.PSQLException: ERROR: new row for relation \"users\" violates check constraint \"chk_last_name\""));
        } catch (Exception e) {
            Assert.fail("Method did not throw DaoException as expected when last name was empty string.");
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
