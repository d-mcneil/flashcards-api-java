package model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class Deck {
    private long deckId;
    @NotNull
    private int ownerUserId;
    @NotNull
    @Length(max = 511)
    private String deckName;
    @NotNull
    private String deckDescription;
    @NotNull
    private LocalDateTime deckCreatedDate;
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

    public LocalDateTime getDeckCreatedDate() {
        return deckCreatedDate;
    }

    public boolean isDeckPublic() {
        return isDeckPublic;
    }

    public boolean isDeckDeleted() {
        return isDeckDeleted;
    }
}
