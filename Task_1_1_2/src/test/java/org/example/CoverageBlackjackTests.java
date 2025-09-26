package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class CoverageBlackjackTests {

    private Card make(String rank, String suit) {
        int v = switch (rank) {
            case "Двойка" -> 2;
            case "Тройка" -> 3;
            case "Четвёрка" -> 4;
            case "Пятёрка" -> 5;
            case "Шестёрка" -> 6;
            case "Семёрка" -> 7;
            case "Восьмёрка" -> 8;
            case "Девятка" -> 9;
            case "Десятка", "Валет", "Дама", "Король" -> 10;
            case "Туз" -> 11;
            default -> throw new IllegalArgumentException("Unknown rank: " + rank);
        };
        return new Card(suit, rank, v);
    }

    static class StubDeck extends Deck {
        private final Deque<Card> q = new ArrayDeque<>();
        StubDeck(Card... cards) { for (Card c : cards) q.addLast(c); }
        @Override void build() {}
        @Override void shuffle() {}
        @Override Card deal() {
            if (q.isEmpty()) throw new IllegalStateException("StubDeck exhausted");
            return q.removeFirst();
        }
    }

    private void injectDeck(Game g, Deck deck) throws Exception {
        for (Field f : g.getClass().getDeclaredFields()) {
            if (Deck.class.isAssignableFrom(f.getType())) {
                f.setAccessible(true);
                f.set(g, deck);
                return;
            }
        }
        fail("Не найдено поле Deck внутри Game для инъекции");
    }

    private void injectScanner(Game g, String input) throws Exception {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        for (Field f : g.getClass().getDeclaredFields()) {
            if (Scanner.class.isAssignableFrom(f.getType())) {
                f.setAccessible(true);
                f.set(g, new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8.name()));
                return;
            }
        }
    }

    private String capturePlayRound(Game g) throws Exception {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bout, true, StandardCharsets.UTF_8.name()));
        try {
            g.playRound();
            return bout.toString(StandardCharsets.UTF_8.name());
        } finally {
            System.setOut(originalOut);
        }
    }

    private boolean containsAny(String haystack, String... needles) {
        for (String s : needles) if (haystack.contains(s)) return true;
        return false;
    }

    @Test @DisplayName("Hand: понижение туза и флаги isBlackJack/isBust")
    void handAceAndFlags() {
        Hand h = new Hand();
        h.add(make("Туз", "Пики"));
        h.add(make("Девятка", "Черви"));
        h.add(make("Король", "Трефы"));
        assertEquals(20, h.value());
        assertFalse(h.isBust());

        Hand bj = new Hand();
        bj.add(make("Туз", "Бубны"));
        bj.add(make("Десятка", "Пики"));
        assertEquals(21, bj.value());
        assertTrue(bj.isBlackJack());

        Hand bust = new Hand();
        bust.add(make("Десятка", "Бубны"));
        bust.add(make("Девятка", "Трефы"));
        bust.add(make("Пятёрка", "Черви"));
        assertTrue(bust.isBust());
    }

    @Test @DisplayName("Deck: выдаёт 53-ю карту (автопересбор/перетасовка)")
    void deckRebuilds() {
        Deck d = new Deck();
        for (int i = 0; i < 52; i++) {
            assertNotNull(d.deal(), "Карта #" + (i+1) + " должна быть не null");
        }
        Card c53 = d.deal();
        assertNotNull(c53);
    }

    @Test @DisplayName("Игрок получает блэкджек сразу")
    void immediatePlayerBlackjack() throws Exception {
        Game g = new Game();
        StubDeck deck = new StubDeck(
                make("Туз", "Пики"),
                make("Девятка", "Черви"),
                make("Король", "Трефы"),
                make("Семёрка", "Бубны")
        );
        injectDeck(g, deck);
        injectScanner(g, "");
        String out = capturePlayRound(g);
        assertTrue(containsAny(out, "У Игрока Блэкджек", "Блэкджек", "Blackjack"));
    }

    @Test @DisplayName("Игрок берёт одну карту и сгорает")
    void playerBustsOnHit() throws Exception {
        Game g = new Game();
        StubDeck deck = new StubDeck(
                make("Девятка", "Пики"),
                make("Девятка", "Черви"),
                make("Шестёрка", "Бубны"),
                make("Семёрка", "Трефы"),
                make("Король", "Пики")
        );
        injectDeck(g, deck);
        injectScanner(g, "1\n");
        String out = capturePlayRound(g);
        assertTrue(containsAny(out, "Перебор", "проиграли", "проигрыш", "bust"));
    }

    @Test @DisplayName("Игрок стоит; дилер сгорает при доборе")
    void dealerBustsAfterStand() throws Exception {
        Game g = new Game();
        StubDeck deck = new StubDeck(
                make("Девятка", "Пики"),
                make("Шестёрка", "Черви"),
                make("Пятёрка", "Бубны"),
                make("Девятка", "Трефы"),
                make("Король", "Пики")
        );
        injectDeck(g, deck);
        injectScanner(g, "0\n");
        String out = capturePlayRound(g);
        assertTrue(containsAny(out, "Дилер перебрал", "Вы выиграли", "дилер сгорел", "dealer bust"));
    }

    @Test @DisplayName("Оба получают блэкджек — ничья")
    void bothBlackjackDraw() throws Exception {
        Game g = new Game();
        StubDeck deck = new StubDeck(
                make("Туз", "Пики"),
                make("Туз", "Черви"),
                make("Король", "Трефы"),
                make("Дама", "Бубны")
        );
        injectDeck(g, deck);
        injectScanner(g, "");
        String out = capturePlayRound(g);
        assertTrue(containsAny(out, "Нич", "ничья", "draw"));
    }

    @Test @DisplayName("Дилер получает блэкджек — игрок проигрывает сразу")
    void dealerBlackjackImmediateLoss() throws Exception {
        Game g = new Game();
        StubDeck deck = new StubDeck(
                make("Девятка", "Пики"),
                make("Туз", "Черви"),
                make("Десятка", "Бубны"),
                make("Король", "Трефы")
        );
        injectDeck(g, deck);
        injectScanner(g, "");
        String out = capturePlayRound(g);
        assertTrue(containsAny(out,
                "Блэкджек у дилера",
                "Дилер получил блэкджек",
                "У Дилера Блэкджек",
                "Поражение",
                "проиграли",
                "dealer blackjack"));
    }
}