package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Deck {
    List<Card> cards = new ArrayList<>();

    Deck() {
        build();
        shuffle();
    }

    void build() {
        cards.clear();
        String[] suits = {"Пики", "Трефы", "Черви", "Бубны"};
        String[] ranks = {
                "Двойка", "Тройка", "Четвёрка", "Пятёрка", "Шестёрка", "Семёрка",
                "Восьмёрка", "Девятка", "Десятка", "Валет", "Дама", "Король", "Туз"
        };
        int[] values = {2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11};
        for (int s = 0; s < suits.length; s++) {
            for (int r = 0; r < ranks.length; r++) {
                cards.add(new Card(suits[s], ranks[r], values[r]));
            }
        }
    }

    void shuffle() {
        Collections.shuffle(cards);
    }

    Card deal() {
        if (cards.isEmpty()) {
            build();
            shuffle();
        }
        return cards.remove(cards.size() - 1);
    }
}