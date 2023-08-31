package flashcardsapi.daotests;

import model.Login;
import org.junit.Assert;
import org.junit.Before;

public class JdbcLoginDaoTests extends BaseDaoTests {

    private Login TEST_LOGIN_1;
    private Login TEST_LOGIN_2;

    @Before
    public void createLogins() {
        TEST_LOGIN_1 = new Login();
        TEST_LOGIN_1.setHashedPassword("1".repeat(60));
        TEST_LOGIN_1.setUserId(1001);

        TEST_LOGIN_2 = new Login();
        TEST_LOGIN_2.setHashedPassword("2".repeat(60));
        TEST_LOGIN_2.setUserId(1002);
    }

    private void assertLoginsMatch(Login expected, Login actual) {
        Assert.assertEquals(expected.getHashedPassword(), actual.getHashedPassword());
    }
}
