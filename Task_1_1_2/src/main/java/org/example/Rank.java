package org.example;

enum Rank {
    TWO("Двойка", 2),
    THREE("Тройка", 3),
    FOUR("Четвёрка", 4),
    FIVE("Пятёрка", 5),
    SIX("Шестёрка", 6),
    SEVEN("Семёрка", 7),
    EIGHT("Восьмёрка", 8),
    NINE("Девятка", 9),
    TEN("Десятка", 10),
    JACK("Валет", 10),
    QUEEN("Дама", 10),
    KING("Король", 10),
    ACE("Туз", 11);

    private final String label;
    private final int value;

    Rank(String label, int value) {
        this.label = label;
        this.value = value;
    }

    String label() {
        return label;
    }

    int value() {
        return value;
    }

    @Override
    public String toString() {
        return label;
    }
}