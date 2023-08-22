package dao;

import model.PracticeSettings;

public interface PracticeSettingsDao {
    public PracticeSettings getPracticeSettingsById(long settingsId);

    public PracticeSettings getPracticeSettingsByUserIdAndDeckId(int userId, long deckId);

    public PracticeSettings updatePracticeSettings(PracticeSettings practiceSettings);
}
