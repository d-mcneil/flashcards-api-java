package dao;

import model.Card;

import java.util.List;

public interface CardDao {
    public List<Card> getCards(long deckId);

    public Card getCardById(long cardId);

    public Card updateCard(Card card);

    public int deleteCardById(long cardId);
}
