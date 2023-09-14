package flashcardsapi.model.dao;

import flashcardsapi.model.models.Card;

import java.util.List;

public interface CardDao {
    List<Card> getCards(long deckId);

    Card getCardById(long cardId);

    Card createCard(Card card);

    Card updateCard(Card card);
    int updateCardScore(Card card);

    int deleteCardById(long cardId);
}
