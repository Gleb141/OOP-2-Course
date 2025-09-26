package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

class Card{
    String suit;
    String rank;
    int value;

    Card(String suit, String rank, int value) {
        this.suit = suit;
        this.rank = rank;
        this.value = value;
    }

    public String toString() {
        return rank + " " + suit + " (" + value + ")";
    }
}

class Deck {
    List<Card> cards = new ArrayList<Card>();

    Deck() {
        build();
        shuffle();
    }
    void build(){
        cards.clear();
        String[] suits = {"Пики","Трефы","Черви","Бубны"};
        String[] ranks = {"Двойка","Тройка","Четвёрка","Пятёрка","Шестёрка","Семёрка",
                          "Восьмёрка","Девятка","Десятка","Валет","Дама","Король","Туз"};
        int[] values = {2,3,4,5,6,7,8,9,10,10,10,10,11};
        for(int s = 0; s < suits.length; s++){
            for(int r = 0; r < ranks.length; r++){
                cards.add(new Card(suits[s], ranks[r], values[r]));
            }
        }
    }

    void shuffle(){
        Collections.shuffle(cards);
    }

    Card deal(){
        if(cards.isEmpty()){
            build();
            shuffle();
        }
        return cards.remove(cards.size()-1);
    }
}

class Hand{
    List<Card> list = new ArrayList<>();

    void add(Card card){
        list.add(card);
    }
    int value(){
        int sum = 0;
        int tQuan = 0;
        for(int i = 0; i < list.size(); i++){
            sum += list.get(i).value;
            if(list.get(i).rank.equals("Туз")) {
                tQuan++;
            }
        }
        while(tQuan > 0 && sum > 21){
            sum -= 10;
            tQuan--;
        }
        return sum;
    }
    boolean isBust(){
        return value() > 21;
    }
    boolean isBlackJack(){
        return list.size() == 2 && value() == 21;
    }
    String show(boolean hideSecond){
        String s = "[";
        for(int i = 0; i < list.size(); i++){
            if(hideSecond && i == 1){
                s += "<закрытая карта>";
            }
            else{
                s += list.get(i).toString();
            }
            if(i != list.size() - 1){
                s += ", ";
            }
        }
        s += "]";
        return s;
    }
}

class Game{
    Deck deck = new Deck();
    Scanner in = new Scanner(System.in);
    int round = 0;
    int player_wins = 0;
    int dealer_wins = 0;
    int draws = 0;


    void run(){
        System.out.println("Добро пожаловать в Блэкджек!");
        boolean again = true;
        while(again){
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

    void playRound(){
        Hand player = new Hand();
        Hand dealer = new Hand();
        player.add(deck.deal());
        dealer.add(deck.deal());
        player.add(deck.deal());
        dealer.add(deck.deal());
        System.out.println("Дилер раздал карты");
        showHands(player,dealer,false);
        boolean pBJ = player.isBlackJack();
        boolean dBJ = dealer.isBlackJack();
        if(pBJ || dBJ){
            System.out.println();
            System.out.println("Открываем карты...");
            showHands(player,dealer,true);
            if(pBJ && dBJ){
                System.out.println("У обоих игроков Блэкджек. Ничья!");
                draws++;
                printScore("draw");
            }
            else if(pBJ){
                System.out.println("У Игрока Блэкджек. Победа!");
                player_wins++;
                printScore("player");
            }
            else{
                System.out.println("У Дилера Блэкджек. Поражение!");
                dealer_wins++;
                printScore("dealer");
            }
            return;
        }
        System.out.println();
        System.out.println("Ваш ход");
        System.out.println("-------");
        boolean playerBust = false;
        while(true) {
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
        if(playerBust){
            System.out.println("\n Перебор. Вы проиграли раунд!");
            dealer_wins++;
            printScore("dealer");
            return;
        }
        System.out.println("\nХод Дилера");
        System.out.println("-------");
        System.out.println("Дилер открывает закрытую карту: " + dealer.list.get(1));
        showHands(player,dealer,true);
        while(dealer.value() < 17){
            Card c = deck.deal();
            dealer.add(c);
            System.out.println("Дилер открывает карту " + c);
            showHands(player,dealer,true);
        }
        int p = player.value();
        int d = dealer.value();
        if(dealer.isBust()){
            System.out.println("Дилер перебрал. Вы выиграли раунд!");
            player_wins++;
            printScore("player");
        }
        else if(p>d){
            System.out.println("Вы выиграли раунд!");
            player_wins++;
            printScore("player");
        }
        else if(p<d){
            System.out.println("Дилер выиграл раунд!");
            dealer_wins++;
            printScore("dealer");
        }
        else{
            System.out.println("Ничья!");
            draws++;
            printScore("draw");
        }
    }
    void showHands(Hand player, Hand dealer, boolean showDealerAll) {
        System.out.println("  Ваши карты: " + player.show(false) + " => " + player.value());
        if (showDealerAll) {
            System.out.println("  Карты дилера: " + dealer.show(false) + " => " + dealer.value());
        } else {
            System.out.println("  Карты дилера: " + dealer.show(true));
        }
    }
    boolean askYesNo() {
        while (true) {
            String s = in.nextLine().trim().toLowerCase();
            if (s.startsWith("y") || s.startsWith("д")) return true;
            if (s.startsWith("n") || s.startsWith("н")) return false;
            System.out.print("Введите y/n: ");
        }
    }
    void printScore(String who) {
        String score = "Счёт " + player_wins + ":" + dealer_wins;
        if (who.equals("player")) System.out.println(score + " в вашу пользу.");
        else if (who.equals("dealer")) System.out.println(score + " в пользу дилера.");
        else System.out.println(score + ". Ничья раунда.");
    }

}

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.run();
    }
}


