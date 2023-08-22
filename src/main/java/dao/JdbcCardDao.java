package dao;

import exception.DaoException;
import model.Card;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcCardDao implements CardDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCardDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Card> getCards(long deckId) {
        List<Card> cards = new ArrayList<>();
        String sql =
                "SELECT " +
                        "card_id, " +
                        "deck_id, " +
                        "card_term, " +
                        "card_definition, " +
                        "card_score, " +
                        "card_created_date, " +
                        "is_card_deleted " +
                "FROM card " +
                "WHERE deck_id = ? AND is_card_deleted = false;";
        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, deckId);
            while (sqlRowSet.next()) {
                cards.add(mapRowToCard(sqlRowSet));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
        return cards;
    }

    @Override
    public Card getCardById(long cardId) {
        Card card = null;
        String sql =
                "SELECT " +
                        "card_id, " +
                        "deck_id, " +
                        "card_term, " +
                        "card_definition, " +
                        "card_score, " +
                        "card_created_date, " +
                        "is_card_deleted " +
                "FROM card " +
                "WHERE card_id = ? AND is_card_deleted = false;";
        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, cardId);
            if (sqlRowSet.next()) {
                card = mapRowToCard(sqlRowSet);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
        return card;
    }

    @Override
    public Card updateCard(Card card) {
        Card updatedCard;
        String sql = "UPDATE card (card_term, card_definition, card_score) " +
                "SET card_term = ?, card_definition = ?, card_score = ? " +
                "WHERE card_id = ? AND is_card_deleted = false;";
        try {
            int rowsAffected = jdbcTemplate.update(
                    sql,
                    card.getCardTerm(),
                    card.getCardDefinition(),
                    card.getCardScore(),
                    card.getCardId()
            );
            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected one.");
            }
            updatedCard = getCardById(card.getCardId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return updatedCard;
    }

    @Override
    public int deleteCardById(long cardId) {
        int rowsDeleted = 0;
        String sql = "DELETE FROM card WHERE card_id = ?;";
        // String sql = "UPDATE card SET is_card_deleted = true WHERE card_id = ?"; // This would be the SQL String if I were to use a boolean to "delete" cards instead of actually deleting them.
        try {
            rowsDeleted = jdbcTemplate.update(sql, cardId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return rowsDeleted;
    }

    private Card mapRowToCard(SqlRowSet sqlRowSet) {
        Card card = new Card();
        card.setCardId(sqlRowSet.getLong("card_id"));
        card.setDeckId(sqlRowSet.getLong("deck_id"));
        card.setCardTerm(sqlRowSet.getString("card_term"));
        card.setCardDefinition(sqlRowSet.getString("card_definition"));
        card.setCardScore(sqlRowSet.getInt("card_score"));
        card.setCardCreatedDate(sqlRowSet.getTimestamp("card_created_date"));
        card.setCardDeleted(sqlRowSet.getBoolean("is_card_public"));
        return card;
    }
}
