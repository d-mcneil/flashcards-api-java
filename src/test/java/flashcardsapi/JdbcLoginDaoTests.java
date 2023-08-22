package flashcardsapi;

import model.Login;
import org.junit.Assert;

public class JdbcLoginDaoTests extends BaseDaoTests {
    private void assertLoginsMatch(Login expected, Login actual) {
        Assert.assertEquals(expected.getHashedPassword(), actual.getHashedPassword());
    }
}
