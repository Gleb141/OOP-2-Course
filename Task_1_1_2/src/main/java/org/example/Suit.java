package org.example;

enum Suit {
    SPADES("Пики"),
    CLUBS("Трефы"),
    HEARTS("Черви"),
    DIAMONDS("Бубны");

    private final String label;

    Suit(String label) {
        this.label = label;
    }

    String label() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}