package flashcardsapi;

import model.User;
import org.junit.Assert;

public class JdbcUserDaoTests extends BaseDaoTests {
    // From BaseDaoTests.java
    //
    // protected User TEST_USER_1;
    // protected User TEST_USER_2;

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
