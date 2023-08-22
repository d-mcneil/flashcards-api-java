package flashcardsapi;

import model.Deck;
import model.User;
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
        TEST_USER_1.setUsername("username1");
        TEST_USER_1.setFirstName("TestFirst1");
        TEST_USER_1.setLastName("TestLast1");
        TEST_USER_1.setEmail("testuser1@example.com");
        TEST_USER_1.setJoinedDate(Timestamp.valueOf(LocalDateTime.of(2022, 12, 31, 13, 59, 59)));
        TEST_USER_1.setUserActive(true);

        TEST_USER_2 = new User();
        TEST_USER_2.setUsername("username2");
        TEST_USER_2.setFirstName("TestFirst2");
        TEST_USER_2.setLastName("TestLast2");
        TEST_USER_2.setEmail("testuser2@example.com");
        TEST_USER_2.setJoinedDate(Timestamp.valueOf(LocalDateTime.of(2021, 6, 18, 16, 29, 29)));
        TEST_USER_2.setUserActive(false);
    }

    @Before
    public void createDecks() {
        TEST_DECK_1 = new Deck();
        TEST_DECK_1.setOwnerUserId(1);
        TEST_DECK_1.setDeckName("TestDeck1");
        TEST_DECK_1.setDeckDescription("TestDescription1");
        TEST_DECK_1.setDeckCreatedDate(Timestamp.valueOf(LocalDateTime.of(2020, 1, 1, 1, 1, 1)));
        TEST_DECK_1.setDeckPublic(true);
        TEST_DECK_1.setDeckDeleted(false);

        TEST_DECK_2 = new Deck();
        TEST_DECK_2.setOwnerUserId(2);
        TEST_DECK_2.setDeckName("TestDeck2");
        TEST_DECK_2.setDeckDescription("TestDescription2");
        TEST_DECK_2.setDeckCreatedDate(Timestamp.valueOf(LocalDateTime.of(2019, 2, 2, 2, 2, 2)));
        TEST_DECK_2.setDeckPublic(true);
        TEST_DECK_2.setDeckDeleted(false);
    }
}
