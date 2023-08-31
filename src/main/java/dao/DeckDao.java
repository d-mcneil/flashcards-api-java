package dao;

import model.Deck;

import java.util.List;

public interface DeckDao {
    List<Deck> getDecks(int userId);

    Deck getDeckById(long deckId);

    Deck createDeck(Deck deck);

    Deck updateDeck(Deck deck, int ownerUserId);

    Deck updateIsDeckPublic(Deck deck, int ownerUserId);
    int deleteDeckById(long deckId, int ownerUserId);

    void linkDeckUser(long deckId, int userId);

    void unlinkDeckUser(long deckId, int userId);
}
