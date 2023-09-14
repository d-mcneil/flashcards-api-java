package flashcardsapi.daotests;

import flashcardsapi.model.models.PracticeSettings;
import org.junit.Assert;
import org.junit.Before;

public class JdbcPracticeSettingsDaoTests extends BaseDaoTests {

    private PracticeSettings TEST_PRACTICE_SETTINGS_1;
    private PracticeSettings TEST_PRACTICE_SETTINGS_2;

    @Before
    public void createPracticeSettings() {
        TEST_PRACTICE_SETTINGS_1 = new PracticeSettings();
        TEST_PRACTICE_SETTINGS_1.setSettingsId(4001);
        TEST_PRACTICE_SETTINGS_1.setDefinitionFirst(false);
        TEST_PRACTICE_SETTINGS_1.setPracticeDeckPercentage((byte) 100);
        TEST_PRACTICE_SETTINGS_1.setTermLanguageCode("en-US");
        TEST_PRACTICE_SETTINGS_1.setTermLanguageName("Google US English");
        TEST_PRACTICE_SETTINGS_1.setDefinitionLanguageCode("en-US");
        TEST_PRACTICE_SETTINGS_1.setDefinitionLanguageName("Google US English");
        TEST_PRACTICE_SETTINGS_1.setShouldReadOutOnNextCard(false);
        TEST_PRACTICE_SETTINGS_1.setShouldReadOutOnFlip(false);

        TEST_PRACTICE_SETTINGS_2.setSettingsId(4002);
        TEST_PRACTICE_SETTINGS_2.setDefinitionFirst(true);
        TEST_PRACTICE_SETTINGS_2.setPracticeDeckPercentage((byte) 100);
        TEST_PRACTICE_SETTINGS_2.setTermLanguageCode("YY-YY");
        TEST_PRACTICE_SETTINGS_2.setTermLanguageName("YYYYYYYYYY");
        TEST_PRACTICE_SETTINGS_2.setDefinitionLanguageCode("XX-XX");
        TEST_PRACTICE_SETTINGS_2.setDefinitionLanguageName("XXXXXXXXXX");
        TEST_PRACTICE_SETTINGS_2.setShouldReadOutOnNextCard(true);
        TEST_PRACTICE_SETTINGS_2.setShouldReadOutOnFlip(true);
    }

    private void assertPracticeSettingsMatch(PracticeSettings expected, PracticeSettings actual) {
        Assert.assertEquals(expected.getSettingsId(), actual.getSettingsId());
        Assert.assertEquals(expected.getTermLanguageCode(), actual.getTermLanguageCode());
        Assert.assertEquals(expected.getDefinitionLanguageCode(), actual.getDefinitionLanguageCode());
        Assert.assertEquals(expected.getTermLanguageName(), actual.getTermLanguageName());
        Assert.assertEquals(expected.getDefinitionLanguageName(), actual.getDefinitionLanguageName());
        Assert.assertEquals(expected.getPracticeDeckPercentage(), actual.getPracticeDeckPercentage());
        Assert.assertEquals(expected.isDefinitionFirst(), actual.isDefinitionFirst());
        Assert.assertEquals(expected.isShouldReadOutOnFlip(), actual.isShouldReadOutOnFlip());
        Assert.assertEquals(expected.isShouldReadOutOnNextCard(), actual.isShouldReadOutOnNextCard());
    }
}
