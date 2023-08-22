package dao;

import model.Deck;

import java.util.List;

public interface DeckDao {
    public List<Deck> getDecks(int userId);

    public Deck getDeckById(long deckId);

    public Deck updateDeck(Deck deck);

    public int deleteDeckById(long deckId);

    public void linkDeckUser(long deckId, int userId);
}
