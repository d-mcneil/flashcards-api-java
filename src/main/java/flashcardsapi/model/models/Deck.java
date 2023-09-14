package flashcardsapi.model.models;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

public class Deck {

    private long deckId;

    @NotNull
    private int ownerUserId;

    @NotNull
    @Length(max = 511)
    private String deckName;

    @NotNull
    private String deckDescription;

    private Timestamp deckCreatedDate;

    @NotNull
    private boolean isDeckPublic;

    @NotNull
    private boolean isDeckDeleted;

    public long getDeckId() {
        return deckId;
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }

    public String getDeckName() {
        return deckName;
    }

    public String getDeckDescription() {
        return deckDescription;
    }

    public Timestamp getDeckCreatedDate() {
        return deckCreatedDate;
    }

    public boolean isDeckPublic() {
        return isDeckPublic;
    }

    public boolean isDeckDeleted() {
        return isDeckDeleted;
    }

    public void setDeckId(long deckId) {
        this.deckId = deckId;
    }

    public void setOwnerUserId(int ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public void setDeckDescription(String deckDescription) {
        this.deckDescription = deckDescription;
    }

    public void setDeckCreatedDate(Timestamp deckCreatedDate) {
        this.deckCreatedDate = deckCreatedDate;
    }

    public void setDeckPublic(boolean deckPublic) {
        isDeckPublic = deckPublic;
    }

    public void setDeckDeleted(boolean deckDeleted) {
        isDeckDeleted = deckDeleted;
    }
}
