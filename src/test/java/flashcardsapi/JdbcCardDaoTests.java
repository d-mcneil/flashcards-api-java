package flashcardsapi;

import model.Card;
import org.junit.Assert;
import org.junit.Before;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class JdbcCardDaoTests extends BaseDaoTests {
    protected Card TEST_CARD_1;
    protected Card TEST_CARD_2;

    @Before
    public void createCards() {
        TEST_CARD_1 = new Card();
        TEST_CARD_1.setDeckId(1);
        TEST_CARD_1.setCardTerm("TestCard1");
        TEST_CARD_1.setCardDefinition("TestDefinition1");
        TEST_CARD_1.setCardScore(1);
        TEST_CARD_1.setCardCreatedDate(Timestamp.valueOf(LocalDateTime.of(2018, 3, 3, 3, 3, 3)));
        TEST_CARD_1.setCardDeleted(false);

        TEST_CARD_2 = new Card();
        TEST_CARD_2.setDeckId(2);
        TEST_CARD_2.setCardTerm("TestCard2");
        TEST_CARD_2.setCardDefinition("TestDefinition2");
        TEST_CARD_2.setCardScore(2);
        TEST_CARD_2.setCardCreatedDate(Timestamp.valueOf(LocalDateTime.of(2017, 4, 4, 4, 4, 4)));
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

