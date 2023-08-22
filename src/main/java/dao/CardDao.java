package dao;

import model.Card;

import java.util.List;

public interface CardDao {
    List<Card> getCards(long deckId);

    Card getCardById(long cardId);

    Card createCard(Card card);

    Card updateCard(Card card);

    int deleteCardById(long cardId);
}
