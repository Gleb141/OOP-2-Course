package org.example;

class Card {
    String suit;
    String rank;
    int value;

    Card(String suit, String rank, int value) {
        this.suit = suit;
        this.rank = rank;
        this.value = value;
    }

    @Override
    public String toString() {
        return rank + " " + suit + " (" + value + ")";
    }
}