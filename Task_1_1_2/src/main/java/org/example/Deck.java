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
        for (Suit s : Suit.values()) {
            for (Rank r : Rank.values()) {
                cards.add(new Card(s.label(), r.label(), r.value()));
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