package org.example;

import java.util.ArrayList;
import java.util.List;

class Hand {
    List<Card> list = new ArrayList<>();

    void add(Card card) {
        list.add(card);
    }

    int value() {
        int sum = 0;
        int aceCount = 0;
        for (int i = 0; i < list.size(); i++) {
            Card c = list.get(i);
            sum += c.value;
            if ("Туз".equals(c.rank)) {
                aceCount++;
            }
        }
        while (aceCount > 0 && sum > 21) {
            sum -= 10;
            aceCount--;
        }
        return sum;
    }

    boolean isBust() {
        return value() > 21;
    }

    boolean isBlackJack() {
        return list.size() == 2 && value() == 21;
    }

    String show(boolean hideSecond) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            if (hideSecond && i == 1) {
                sb.append("<закрытая карта>");
            } else {
                sb.append(list.get(i).toString());
            }
            if (i != list.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}