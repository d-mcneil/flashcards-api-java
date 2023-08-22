package flashcardsapi;

import model.PracticeSettings;
import org.junit.Assert;

public class JdbcPracticeSettingsDaoTests extends BaseDaoTests {

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
