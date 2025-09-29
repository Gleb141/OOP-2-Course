package org.example;

import java.util.Scanner;

class Game {
    Deck deck = new Deck();
    Scanner in = new Scanner(System.in);

    int round = 0;
    int playerWins = 0;
    int dealerWins = 0;
    int draws = 0;

    void run() {
        System.out.println("Добро пожаловать в Блэкджек!");
        boolean again = true;
        while (again) {
            round++;
            System.out.println();
            System.out.println("Раунд " + round);
            playRound();
            System.out.println();
            System.out.print("Сыграть ещё раунд? (y/n): ");
            again = askYesNo();
        }
        System.out.println("Спасибо за игру!");
    }

    void playRound() {
        Hand player = new Hand();
        Hand dealer = new Hand();

        initialDeal(player, dealer);
        if (handleImmediateBlackjacks(player, dealer)) {
            return;
        }

        System.out.println();
        System.out.println("Ваш ход");
        System.out.println("-------");

        boolean playerBust = playerTurn(player, dealer);
        if (playerBust) {
            System.out.println("\nПеребор. Вы проиграли раунд!");
            dealerWins++;
            printScore("dealer");
            return;
        }

        dealerTurn(dealer, player);
        settleRound(player, dealer);
    }

    // --- helpers ---

    void initialDeal(Hand player, Hand dealer) {
        player.add(deck.deal());
        dealer.add(deck.deal());
        player.add(deck.deal());
        dealer.add(deck.deal());

        System.out.println("Дилер раздал карты");
        showHands(player, dealer, false);
    }

    boolean handleImmediateBlackjacks(Hand player, Hand dealer) {
        boolean playerBlackjack = player.isBlackJack();
        boolean dealerBlackjack = dealer.isBlackJack();
        if (!playerBlackjack && !dealerBlackjack) {
            return false;
        }

        System.out.println();
        System.out.println("Открываем карты...");
        showHands(player, dealer, true);

        if (playerBlackjack && dealerBlackjack) {
            System.out.println("У обоих игроков Блэкджек. Ничья!");
            draws++;
            printScore("draw");
        } else if (playerBlackjack) {
            System.out.println("У Игрока Блэкджек. Победа!");
            playerWins++;
            printScore("player");
        } else {
            System.out.println("У Дилера Блэкджек. Вы проиграли раунд!");
            dealerWins++;
            printScore("dealer");
        }
        return true;
    }

    boolean playerTurn(Hand player, Hand dealer) {
        boolean playerBust = false;
        while (true) {
            System.out.println("Введите 1 - взять, 0 - стоп: ");
            String s = in.nextLine().trim();

            if (s.equals("1")) {
                Card c = deck.deal();
                player.add(c);
                System.out.println("Вы открыли карту: " + c);
                showHands(player, dealer, false);
                if (player.isBust()) {
                    playerBust = true;
                    break;
                }
                if (player.value() == 21) {
                    System.out.println("У вас 21. Стоп.");
                    break;
                }
            } else if (s.equals("0")) {
                break;
            } else {
                System.out.println("Неверный ввод.");
            }
        }
        return playerBust;
    }

    void dealerTurn(Hand dealer, Hand player) {
        System.out.println("\nХод Дилера");
        System.out.println("-------");
        System.out.println("Дилер открывает закрытую карту: " + dealer.list.get(1));
        showHands(player, dealer, true);

        while (dealer.value() < 17) {
            Card c = deck.deal();
            dealer.add(c);
            System.out.println("Дилер открывает карту " + c);
            showHands(player, dealer, true);
        }
    }

    void settleRound(Hand player, Hand dealer) {
        int p = player.value();
        int d = dealer.value();

        if (dealer.isBust()) {
            System.out.println("Дилер перебрал. Вы выиграли раунд!");
            playerWins++;
            printScore("player");
        } else if (p > d) {
            System.out.println("Вы выиграли раунд!");
            playerWins++;
            printScore("player");
        } else if (p < d) {
            System.out.println("Дилер выиграл раунд!");
            dealerWins++;
            printScore("dealer");
        } else {
            System.out.println("Ничья!");
            draws++;
            printScore("draw");
        }
    }

    void showHands(Hand player, Hand dealer, boolean showDealerAll) {
        System.out.println("  Ваши карты: " + player.show(false) + " => " + player.value());
        if (showDealerAll) {
            System.out.println(
                    "  Карты дилера: " + dealer.show(false) + " => " + dealer.value()
            );
        } else {
            System.out.println("  Карты дилера: " + dealer.show(true));
        }
    }

    boolean askYesNo() {
        while (true) {
            String s = in.nextLine().trim().toLowerCase();
            if (s.startsWith("y") || s.startsWith("д")) {
                return true;
            }
            if (s.startsWith("n") || s.startsWith("н")) {
                return false;
            }
            System.out.print("Введите y/n: ");
        }
    }

    void printScore(String who) {
        String score = "Счёт " + playerWins + ":" + dealerWins;
        if ("player".equals(who)) {
            System.out.println(score + " в вашу пользу.");
        } else if ("dealer".equals(who)) {
            System.out.println(score + " в пользу дилера.");
        } else {
            System.out.println(score + ". Ничья раунда.");
        }
    }
}