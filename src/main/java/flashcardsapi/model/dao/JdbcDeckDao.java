package flashcardsapi.model.dao;

import flashcardsapi.exception.DaoException;
import flashcardsapi.model.models.Deck;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcDeckDao implements DeckDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcDeckDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Deck> getDecks(int userId) {
        List<Deck> decks = new ArrayList<>();
        String sql =
                "SELECT " +
                        "deck.deck_id, " +
                        "deck.owner_user_id, " +
                        "deck_name, " +
                        "deck_description, " +
                        "deck_created_date, " +
                        "is_deck_public, " +
                        "is_deck_deleted " +
                "FROM deck " +
                "JOIN deck_users ON deck.deck_id = deck_users.deck_id " +
                "WHERE user_id = ? AND is_deck_deleted = false;";
        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, userId);
            while (sqlRowSet.next()) {
                decks.add(mapRowToDeck(sqlRowSet));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
        return decks;
    }

    @Override
    public Deck getDeckById(long deckId) {
        Deck deck = null;
        String sql =
                "SELECT " +
                        "deck_id, " +
                        "owner_user_id, " +
                        "deck_name, " +
                        "deck_description, " +
                        "deck_created_date, " +
                        "is_deck_public, " +
                        "is_deck_deleted " +
                "FROM deck " +
                "WHERE deck_id = ? AND is_deck_deleted = false;";
        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, deckId);
            if (sqlRowSet.next()) {
                deck = mapRowToDeck(sqlRowSet);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
        return deck;
    }

    @Override
    public Deck createDeck(Deck deck) {
        Deck newDeck;
        String sql = "INSERT INTO deck (owner_user_id, deck_name, deck_description) " +
                "VALUES (?, ?, ?) " +
                "RETURNING deck_id;";
        try {
            Integer deckId = jdbcTemplate.queryForObject(sql, Integer.class, deck.getOwnerUserId(), deck.getDeckName(), deck.getDeckDescription());
            if (deckId == null) {
                throw new DaoException("Error creating deck");
            }
            newDeck = getDeckById(deckId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return newDeck;
    }

    @Override
    public Deck updateDeck(Deck deck, int ownerUserId) {
        Deck updatedDeck;
        String sql = "UPDATE deck (deck_name, deck_description, is_deck_public) " +
                "SET deck_name = ?, deck_description = ?, is_deck_public = ? " +
                "WHERE deck_id = ? AND owner_user_id = ? AND is_deck_deleted = false;";
        try {
            int rowsAffected = jdbcTemplate.update(
                    sql,
                    deck.getDeckName(),
                    deck.getDeckDescription(),
                    deck.isDeckPublic(),
                    deck.getDeckId(),
                    ownerUserId
            );
            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected one.");
            }
            updatedDeck = getDeckById(deck.getDeckId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return updatedDeck;
    }

    @Override
    public Deck updateIsDeckPublic(Deck deck, int ownerUserId){
        Deck updatedDeck;
        String sql = "UPDATE deck (is_deck_public) " +
                "SET is_deck_public = ? " +
                "WHERE deck_id = ? AND owner_user_id = ? AND is_deck_deleted = false;";
        try {
            int rowsAffected = jdbcTemplate.update(
                    sql,
                    deck.isDeckPublic(),
                    deck.getDeckId(),
                    ownerUserId
            );
            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected one.");
            }
            updatedDeck = getDeckById(deck.getDeckId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return updatedDeck;
    }

    @Override
    public int deleteDeckById(long deckId, int ownerUserId) {
        int rowsDeleted = 0;
        String sql = "DELETE FROM deck WHERE deck_id = ? AND owner_user_id = ?;";
        // String sql = "UPDATE deck SET is_deck_deleted = true WHERE deck_id = ?"; // This would be the SQL String if I were to use a boolean to "delete" decks instead of actually deleting them.
        try {
            rowsDeleted = jdbcTemplate.update(sql, deckId, ownerUserId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return rowsDeleted;
    }

    @Override
    public void linkDeckUser(long deckId, int userId) {
        String sql = "INSERT INTO deck_users (deck_id, user_id) VALUES (?, ?);";
        try {
            jdbcTemplate.update(sql, deckId, userId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public void unlinkDeckUser(long deckId, int userId) {
        // Deck-user pairings can only be removed from the deck_users table if the user
        // is NOT the owner of the deck. In that case, deleteDeckById() is used instead.
        String sql = "DELETE FROM deck_users " +
                "USING deck " +
                "WHERE deck.deck_id = deck_users.deck_id " +
                "AND deck.owner_user_id != ? " + // The USING line, the first WHERE line, and this line all relate to the comment directly above.
                "AND deck_users.user_id = ? " +
                "AND deck_users.deck_id = ?;";
        try {
            jdbcTemplate.update(sql, userId, userId, deckId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    private Deck mapRowToDeck(SqlRowSet sqlRowSet) {
        Deck deck = new Deck();
        deck.setDeckId(sqlRowSet.getLong("deck_id"));
        deck.setOwnerUserId(sqlRowSet.getInt("owner_user_id"));
        deck.setDeckName(sqlRowSet.getString("deck_name"));
        deck.setDeckDescription(sqlRowSet.getString("deck_description"));
        deck.setDeckCreatedDate(sqlRowSet.getTimestamp("deck_created_date"));
        deck.setDeckPublic(sqlRowSet.getBoolean("is_deck_public"));
        deck.setDeckDeleted(sqlRowSet.getBoolean("is_deck_deleted"));
        return deck;
    }
}
