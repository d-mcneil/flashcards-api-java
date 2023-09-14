package flashcardsapi.daotests;

import flashcardsapi.model.models.Deck;
import org.junit.Assert;

public class JdbcDeckDaoTests extends BaseDaoTests {
    // From BaseDaoTests.java
    //
    // protected User TEST_USER_1;
    // protected User TEST_USER_2;
    // protected User TEST_DECK_1;
    // protected User TEST_DECK_2;

    private void assertDecksMatch(Deck expected, Deck actual) {
        Assert.assertEquals(expected.getDeckId(), actual.getDeckId());
        Assert.assertEquals(expected.getOwnerUserId(), actual.getOwnerUserId());
        Assert.assertEquals(expected.getDeckName(), actual.getDeckName());
        Assert.assertEquals(expected.getDeckDescription(), actual.getDeckDescription());
        Assert.assertEquals(expected.getDeckCreatedDate(), actual.getDeckCreatedDate());
        Assert.assertEquals(expected.isDeckPublic(), actual.isDeckPublic());
        Assert.assertEquals(expected.isDeckDeleted(), actual.isDeckDeleted());
    }
}
