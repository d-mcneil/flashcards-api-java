package flashcardsapi.model.dao;

import flashcardsapi.model.models.PracticeSettings;

public interface PracticeSettingsDao {
    PracticeSettings getPracticeSettingsById(long settingsId);

    PracticeSettings getPracticeSettingsByUserIdAndDeckId(int userId, long deckId);

    PracticeSettings updatePracticeSettings(PracticeSettings practiceSettings);
}
