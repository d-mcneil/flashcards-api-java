package model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class UserDeckPracticeSettings {
    @NotNull
    private int userId;
    @NotNull
    private long deckId;
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

    public int getUserId() {
        return userId;
    }

    public long getDeckId() {
        return deckId;
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
}
