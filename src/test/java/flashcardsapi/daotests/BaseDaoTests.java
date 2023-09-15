package flashcardsapi.daotests;

import flashcardsapi.model.models.Deck;
import flashcardsapi.model.models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestingDatabaseConfig.class)
public abstract class BaseDaoTests {
    @Autowired
    protected DataSource dataSource;

    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }

    protected User TEST_USER_1;
    protected User TEST_USER_2;

    protected Deck TEST_DECK_1;
    protected Deck TEST_DECK_2;

    @Before
    public void createUsers() {
        TEST_USER_1 = new User();
        TEST_USER_1.setUserId(1001);
        TEST_USER_1.setUsername("usERnaME1"); // This intentionally has different casing than the test data... testing that citext works as expected
        TEST_USER_1.setFirstName("TestFirst1");
        TEST_USER_1.setLastName("TestLast1");
        TEST_USER_1.setEmail("testuser1@example.com");
        TEST_USER_1.setJoinedDate(Timestamp.valueOf(LocalDateTime.of(9999, 10, 29, 21, 57, 57)));
        TEST_USER_1.setUserActive(false);

        TEST_USER_2 = new User();
        TEST_USER_2.setUserId(1002);
        TEST_USER_2.setUsername("usERnaME2"); // This intentionally has different casing than the test data... testing that citext works as expected
        TEST_USER_2.setFirstName("TestFirst2");
        TEST_USER_2.setLastName("TestLast2");
        TEST_USER_2.setEmail("testuser2@example.com");
        TEST_USER_2.setJoinedDate(Timestamp.valueOf(LocalDateTime.of(8888, 1, 1, 1, 1, 1)));
        TEST_USER_2.setUserActive(true);
    }

    @Before
    public void createDecks() {
        TEST_DECK_1 = new Deck();
        TEST_DECK_1.setDeckId(2001);
        TEST_DECK_1.setOwnerUserId(1001);
        TEST_DECK_1.setDeckName("TestDeck1");
        TEST_DECK_1.setDeckDescription("TestDescription1");
        TEST_DECK_1.setDeckCreatedDate(Timestamp.valueOf(LocalDateTime.of(9999, 11, 30, 22, 58, 58)));
        TEST_DECK_1.setDeckPublic(true);
        TEST_DECK_1.setDeckDeleted(false);

        TEST_DECK_2 = new Deck();
        TEST_DECK_2.setDeckId(2002);
        TEST_DECK_2.setOwnerUserId(1002);
        TEST_DECK_2.setDeckName("TestDeck2");
        TEST_DECK_2.setDeckDescription("TestDescription2");
        TEST_DECK_2.setDeckCreatedDate(Timestamp.valueOf(LocalDateTime.of(8888, 2, 2, 2, 2, 2)));
        TEST_DECK_2.setDeckPublic(true);
        TEST_DECK_2.setDeckDeleted(false);
    }
}
