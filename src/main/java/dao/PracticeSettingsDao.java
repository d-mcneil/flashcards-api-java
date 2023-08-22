package dao;

import model.PracticeSettings;

public interface PracticeSettingsDao {
    PracticeSettings getPracticeSettingsById(long settingsId);

    PracticeSettings getPracticeSettingsByUserIdAndDeckId(int userId, long deckId);

    PracticeSettings updatePracticeSettings(PracticeSettings practiceSettings);
}
