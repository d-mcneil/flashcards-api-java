package flashcardsapi.model.models;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

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
    private Timestamp cardCreatedDate;

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

    public Timestamp getCardCreatedDate() {
        return cardCreatedDate;
    }

    public boolean isCardDeleted() {
        return isCardDeleted;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public void setDeckId(long deckId) {
        this.deckId = deckId;
    }

    public void setCardTerm(String cardTerm) {
        this.cardTerm = cardTerm;
    }

    public void setCardDefinition(String cardDefinition) {
        this.cardDefinition = cardDefinition;
    }

    public void setCardScore(int cardScore) {
        this.cardScore = cardScore;
    }

    public void setCardCreatedDate(Timestamp cardCreatedDate) {
        this.cardCreatedDate = cardCreatedDate;
    }

    public void setCardDeleted(boolean cardDeleted) {
        isCardDeleted = cardDeleted;
    }
}
