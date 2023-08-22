package dao;

import exception.DaoException;
import model.PracticeSettings;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcPracticeSettingsDao implements PracticeSettingsDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPracticeSettingsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PracticeSettings getPracticeSettingsById(long settingsId) {
        PracticeSettings practiceSettings = null;
        String sql =
                "SELECT " +
                        "settings_id, " +
                        "is_definition_first, " +
                        "practice_deck_percentage, " +
                        "term_language_code, " +
                        "definition_language_code, " +
                        "term_language_name, " +
                        "definition_language_name, " +
                        "should_read_out_on_flip, " +
                        "should_read_out_on_next_card " +
                "FROM practice_settings " +
                "WHERE settings_id = ?;";
        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, settingsId);
            if (sqlRowSet.next()) {
                practiceSettings = mapRowToPracticeSettings(sqlRowSet);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
        return practiceSettings;
    }

    @Override
    public PracticeSettings getPracticeSettingsByUserIdAndDeckId(int userId, long deckId) {
        PracticeSettings practiceSettings = null;
        String sql =
                "SELECT " +
                        "practice_settings.settings_id, " +
                        "is_definition_first, " +
                        "practice_deck_percentage, " +
                        "term_language_code, " +
                        "definition_language_code, " +
                        "term_language_name, " +
                        "definition_language_name, " +
                        "should_read_out_on_flip, " +
                        "should_read_out_on_next_card " +
                "FROM practice_settings " +
                "JOIN deck_users ON practice_settings.settings_id = deck_users.settings_id " +
                "WHERE user_id = ? AND deck_id = ? AND is_deck_deleted = false;";
        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, userId, deckId);
            if (sqlRowSet.next()) {
                practiceSettings = mapRowToPracticeSettings(sqlRowSet);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
        return practiceSettings;
    }

    @Override
    public PracticeSettings updatePracticeSettings(PracticeSettings practiceSettings) {
        PracticeSettings updatedPracticeSettings;
        String sql =
                "UPDATE practice_settings (" +
                        "is_definition_first, " +
                        "practice_deck_percentage, " +
                        "term_language_code, " +
                        "definition_language_code, " +
                        "term_language_name, " +
                        "definition_language_name, " +
                        "should_read_out_on_flip, " +
                        "should_read_out_on_next_card" +
                ") SET " +
                        "is_definition_first = ?, " +
                        "practice_deck_percentage = ?, " +
                        "term_language_code = ?, " +
                        "definition_language_code = ?, " +
                        "term_language_name = ?, " +
                        "definition_language_name = ?, " +
                        "should_read_out_on_flip = ?, " +
                        "should_read_out_on_next_card = ? " +
                "WHERE settings_id = ?;";
        try {
            int rowsAffected = jdbcTemplate.update(
                    sql,
                    practiceSettings.isDefinitionFirst(),
                    practiceSettings.getPracticeDeckPercentage(),
                    practiceSettings.getTermLanguageCode(),
                    practiceSettings.getDefinitionLanguageCode(),
                    practiceSettings.getTermLanguageName(),
                    practiceSettings.getDefinitionLanguageName(),
                    practiceSettings.isShouldReadOutOnFlip(),
                    practiceSettings.isShouldReadOutOnNextCard(),
                    practiceSettings.getSettingsId()
            );
            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected one.");
            }
            updatedPracticeSettings = getPracticeSettingsById(practiceSettings.getSettingsId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return updatedPracticeSettings;
    }

    private PracticeSettings mapRowToPracticeSettings(SqlRowSet sqlRowSet) {
        PracticeSettings practiceSettings = new PracticeSettings();
        practiceSettings.setSettingsId(sqlRowSet.getLong("settings_id"));
        practiceSettings.setDefinitionFirst(sqlRowSet.getBoolean("is_definition_first"));
        practiceSettings.setPracticeDeckPercentage(sqlRowSet.getByte("practice_deck_percentage"));
        practiceSettings.setTermLanguageCode(sqlRowSet.getString("term_language_code"));
        practiceSettings.setDefinitionLanguageCode(sqlRowSet.getString("definition_language_code"));
        practiceSettings.setTermLanguageName(sqlRowSet.getString("term_language_name"));
        practiceSettings.setDefinitionLanguageName(sqlRowSet.getString("definition_language_name"));
        practiceSettings.setShouldReadOutOnFlip(sqlRowSet.getBoolean("should_read_out_on_flip"));
        practiceSettings.setShouldReadOutOnNextCard(sqlRowSet.getBoolean("should_read_out_on_next_card"));
        return practiceSettings;
    }
}
