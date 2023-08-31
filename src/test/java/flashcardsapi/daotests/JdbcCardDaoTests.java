package flashcardsapi.daotests;

import model.Card;
import org.junit.Assert;
import org.junit.Before;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class JdbcCardDaoTests extends BaseDaoTests {

    private Card TEST_CARD_1;
    private Card TEST_CARD_2;

    @Before
    public void createCards() {
        TEST_CARD_1 = new Card();
        TEST_CARD_1.setCardId(3001);
        TEST_CARD_1.setDeckId(2001);
        TEST_CARD_1.setCardTerm("term1");
        TEST_CARD_1.setCardDefinition("definition1");
        TEST_CARD_1.setCardScore(0);
        TEST_CARD_1.setCardCreatedDate(Timestamp.valueOf(LocalDateTime.of(9999, 12, 31, 23, 59, 59)));

        TEST_CARD_2 = new Card();
        TEST_CARD_2.setCardId(3002);
        TEST_CARD_2.setDeckId(2002);
        TEST_CARD_2.setCardTerm("term2");
        TEST_CARD_2.setCardDefinition("definition2");
        TEST_CARD_2.setCardScore(-5);
        TEST_CARD_2.setCardCreatedDate(Timestamp.valueOf(LocalDateTime.of(8888, 3, 3, 3, 3, 3)));
        TEST_CARD_2.setCardDeleted(false);
    }

    private void assertCardsMatch(Card expected, Card actual) {
        Assert.assertEquals(expected.getCardId(), actual.getCardId());
        Assert.assertEquals(expected.getDeckId(), actual.getDeckId());
        Assert.assertEquals(expected.getCardTerm(), actual.getCardTerm());
        Assert.assertEquals(expected.getCardDefinition(), actual.getCardDefinition());
        Assert.assertEquals(expected.getCardScore(), actual.getCardScore());
        Assert.assertEquals(expected.getCardCreatedDate(), actual.getCardCreatedDate());
        Assert.assertEquals(expected.isCardDeleted(), actual.isCardDeleted());
    }
}

