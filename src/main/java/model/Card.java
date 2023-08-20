package model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class Card {
    private long cardId;
    @NotNull
    private long deckId;
    @NotNull
    @Length(max = 511)
    private String cardTerm;
    @NotNull
    private String cardDefinition;
    @NotNull
    private int cardScore;
    @NotNull
    private int cardCreatedDate;
    @NotNull
    private boolean isCardDeleted;

    public long getCardId() {
        return cardId;
    }

    public long getDeckId() {
        return deckId;
    }

    public String getCardTerm() {
        return cardTerm;
    }

    public String getCardDefinition() {
        return cardDefinition;
    }

    public int getCardScore() {
        return cardScore;
    }

    public int getCardCreatedDate() {
        return cardCreatedDate;
    }

    public boolean isCardDeleted() {
        return isCardDeleted;
    }
}
