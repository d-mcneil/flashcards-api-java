package dao;

import model.Deck;

import java.util.List;

public interface DeckDao {
    List<Deck> getDecks(int userId);

    Deck getDeckById(long deckId);

    Deck createDeck(Deck deck);

    Deck updateDeck(Deck deck);

    int deleteDeckById(long deckId);

    void linkDeckUser(long deckId, int userId);

    void unlinkDeckUser(long deckId, int userId);
}
