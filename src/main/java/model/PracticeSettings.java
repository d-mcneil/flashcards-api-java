package model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class PracticeSettings {

    @NotNull
    private long settingsId;

    @NotNull
    private boolean isDefinitionFirst;

    @NotNull
    private byte practiceDeckPercentage;

    @NotNull
    @Length(max = 5, min = 5)
    private String termLanguageCode;

    @NotNull
    @Length(max = 5, min = 5)
    private String definitionLanguageCode;

    @NotNull
    @Length(max = 255)
    private String termLanguageName;

    @NotNull
    @Length(max = 255)
    private String definitionLanguageName;

    @NotNull
    private boolean shouldReadOutOnFlip;

    @NotNull
    private boolean shouldReadOutOnNextCard;

    public long getSettingsId() {
        return settingsId;
    }

    public boolean isDefinitionFirst() {
        return isDefinitionFirst;
    }

    public byte getPracticeDeckPercentage() {
        return practiceDeckPercentage;
    }

    public String getTermLanguageCode() {
        return termLanguageCode;
    }

    public String getDefinitionLanguageCode() {
        return definitionLanguageCode;
    }

    public String getTermLanguageName() {
        return termLanguageName;
    }

    public String getDefinitionLanguageName() {
        return definitionLanguageName;
    }

    public boolean isShouldReadOutOnFlip() {
        return shouldReadOutOnFlip;
    }

    public boolean isShouldReadOutOnNextCard() {
        return shouldReadOutOnNextCard;
    }

    public void setSettingsId(long settingsId) {
        this.settingsId = settingsId;
    }

    public void setDefinitionFirst(boolean definitionFirst) {
        isDefinitionFirst = definitionFirst;
    }

    public void setPracticeDeckPercentage(byte practiceDeckPercentage) {
        this.practiceDeckPercentage = practiceDeckPercentage;
    }

    public void setTermLanguageCode(String termLanguageCode) {
        this.termLanguageCode = termLanguageCode;
    }

    public void setDefinitionLanguageCode(String definitionLanguageCode) {
        this.definitionLanguageCode = definitionLanguageCode;
    }

    public void setTermLanguageName(String termLanguageName) {
        this.termLanguageName = termLanguageName;
    }

    public void setDefinitionLanguageName(String definitionLanguageName) {
        this.definitionLanguageName = definitionLanguageName;
    }

    public void setShouldReadOutOnFlip(boolean shouldReadOutOnFlip) {
        this.shouldReadOutOnFlip = shouldReadOutOnFlip;
    }

    public void setShouldReadOutOnNextCard(boolean shouldReadOutOnNextCard) {
        this.shouldReadOutOnNextCard = shouldReadOutOnNextCard;
    }
}
