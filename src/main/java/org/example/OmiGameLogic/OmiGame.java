package org.example.OmiGameLogic;

import org.example.client.Client1;
import org.example.client.Client2;
import org.example.client.Client3;
import org.example.client.Client4;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class OmiGame {
    private Team team1;
    private Team team2;
    private Deck deck;
    private int roundNumber;
    private int dealerIndex;
    private List<Card> currentTrick;
    private Suit trumps;
    private Player winner;
    private boolean namedTrump = false;
    private ArrayList<ClientHandler> list;
    private Client1 client1;
    private Client2 client2;
    private Client3 client3;
    private Client4 client4;

/*    private ClientHandler myClientHandler;

    public ClientHandler getMyClientHandler() {
        return myClientHandler;
    }*/

    public OmiGame(ArrayList<ClientHandler> lst) {
        this.list = lst;

        initializeGame();

    }

    private void initializeGame() {
        deck = new Deck();
        deck.shuffle();

        Player player1 = new Player("Player 1",list.get(1));
        Player player2 = new Player("Player 2",list.get(2));
        Player player3 = new Player("Player 3",list.get(3));
        Player player4 = new Player("Player 4",list.get(4));

        team1 = new Team("Team A", player1, player2);
        team2 = new Team("Team B", player3, player4);

        dealerIndex = 0; // Assume player1 is the dealer initially
        roundNumber = 1;
        currentTrick = new ArrayList<>();

        // Initialize client objects
        try {
            client1 = new Client1(new Socket("localhost", 1235), "Player 1");
            client2 = new Client2(new Socket("localhost", 1235), "Player 2");
            client3 = new Client3(new Socket("localhost", 1235), "Player 3");
            client4 = new Client4(new Socket("localhost", 1235), "Player 4");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dealCards() {
        List<Card> cards = deck.getCards();
        int startIndex = dealerIndex == 0 ? 0 : 1; // Skip the dealer for cutting
        int index = startIndex;

        for (int i = 0; i < 4; i++) {
            team1.getPlayer1().addToDeck(cards.get(index++));
            team1.getPlayer2().addToDeck(cards.get(index++));
            team2.getPlayer1().addToDeck(cards.get(index++));
            team2.getPlayer2().addToDeck(cards.get(index++));
        }

        dealerIndex = (dealerIndex + 1) % 4;
        System.out.println("Dealer: " + getCurrentDealer().getName());
        System.out.println("Deck cut by: " + getCurrentLeftPlayer().getName());
        System.out.println("Cards dealt.");
    }

    private void nameTrumps() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Player to the right of the dealer, please name trumps (CLUBS, DIAMONDS, HEARTS, SPADES):");
        getCurrentRightPlayer().getMyClientHandler().broadcastMessage2("=============================================================");
       // myClientHandler.broadcastMessage2("Player to the right of the dealer, please name trumps (CLUBS, DIAMONDS, HEARTS, SPADES):");
        getCurrentRightPlayer().printHand();
        getCurrentRightPlayer().getMyClientHandler().broadcastMessage2("Player to the right of the dealer, please name trumps (CLUBS, DIAMONDS, HEARTS, SPADES):");

        boolean trumpFound = false;

        while (!trumpFound) {
            try {
                sleep(1000);
                String trumpInput = Message.getInstance().getMsg().trim().toUpperCase();
                // Split the input string by ": " and get the last part
//                String[] parts = trumpInput.split(": ");
//                String suitInput = parts[parts.length - 1].trim().toUpperCase();
                trumps = Suit.valueOf(trumpInput);
                trumpFound = true;
                this.namedTrump = true;
                // broadcast
                getCurrentRightPlayer().getMyClientHandler().broadcastMessage("========Selected Trumps: " + trumps+"========");

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input. Please enter a valid suit.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        try {
//
//            String trumpInput = scanner.nextLine().toUpperCase();
//            String trumpInput = Message.getInstance().getMsg().toUpperCase();
//            trumps = Suit.valueOf(trumpInput);
//
//            System.out.println("Trumps named: " + trumps);
//        } catch (IllegalArgumentException e) {
//            System.out.println("Invalid input. Please enter a valid suit.");
//            nameTrumps();
//        }
    }

    private Player getCurrentDealer() {
        switch (dealerIndex) {
            case 0:
                return team1.getPlayer1();
            case 1:
                return team1.getPlayer2();
            case 2:
                return team2.getPlayer1();
            default:
                return team2.getPlayer2();
        }
    }

    private Player getCurrentLeftPlayer() {
        int leftIndex = (dealerIndex + 1) % 4;
        switch (leftIndex) {
            case 0:
                return team1.getPlayer1();
            case 1:
                return team1.getPlayer2();
            case 2:
                return team2.getPlayer1();
            default:
                return team2.getPlayer2();
        }
    }

    private Player getCurrentRightPlayer() {
        int rightIndex = (dealerIndex - 1) % 4;
        switch (rightIndex) {
            case 0:
                return team1.getPlayer1();
            case 1:
                return team1.getPlayer2();
            case 2:
                return team2.getPlayer1();
            default:
                return team2.getPlayer2();
        }
    }

    private boolean isValidPlay(Player player, Card card) {
        if (currentTrick.isEmpty()) {
            return true; // First card in the trick, any card is valid
        }

        Suit leadSuit = currentTrick.get(0).getSuit();
        if (card.getSuit() == leadSuit) {
            return true; // Following suit
        }

        // Not following suit, check if the player has the lead suit
        for (Card c : player.getHand()) {
            if (c.getSuit() == leadSuit) {
                return false; // Player has a card of the lead suit, must play that card
            }
        }

        return true; // Player doesn't have the lead suit, any card is valid
    }

    private void playTrick() {
        currentTrick.clear(); // Clear the current trick
        Player currentPlayer;
        if (roundNumber % 4 == 1) {
            currentPlayer = team1.getPlayer1(); // Player 1's turn
            // Send message to Client 1
            sendMessageToClient("Your turn, Player 1", team1.getPlayer1());
        } else if (roundNumber % 4 == 2) {
            currentPlayer = team1.getPlayer2(); // Player 2's turn
            // Send message to Client 2
            sendMessageToClient("Your turn, Player 2", team1.getPlayer2());
        } else if (roundNumber % 4 == 3) {
            currentPlayer = team2.getPlayer1(); // Player 3's turn
            // Send message to Client 3
            sendMessageToClient("Your turn, Player 3", team2.getPlayer1());
        } else if(roundNumber % 4 == 0){
            currentPlayer = team2.getPlayer2(); // Player 4's turn
            // Send message to Client 4
            sendMessageToClient("Your turn, Player 4", team2.getPlayer2());
        }else {
            currentPlayer = winner;
        }

        while (currentTrick.size() < 4) {
            currentPlayer.getMyClientHandler().broadcastMessage2("Your turn, " + currentPlayer.getName() + "!");
//            System.out.println(currentPlayer.getName() + "'s turn.");
//            System.out.println("Your hand:");
            currentPlayer.getMyClientHandler().broadcastMessage2("Your hand:");
//            currentPlayer.getHand().forEach(System.out::println);
            Player finalCurrentPlayer = currentPlayer;
            currentPlayer.getHand().forEach(card -> finalCurrentPlayer.getMyClientHandler().broadcastMessage2(card.toString()));
            Card cardPlayed = currentPlayer.playCard();
            currentPlayer.getMyClientHandler().broadcastMessage(currentPlayer.getName() + " ========== Played Cards " + cardPlayed+" ==========");
            if (!isValidPlay(currentPlayer, cardPlayed)) {
                System.out.println("Invalid play. Please follow suit if possible.");
                continue;
            }
            currentTrick.add(cardPlayed);
            System.out.println(currentPlayer.getName() + " played: " + cardPlayed);
//broadcast

            currentPlayer = getNextPlayer(currentPlayer);
        }

        determineTrickWinner();
        removePlayedCards();
    }



    private void sendMessageToClient(String message, Player player) {
        if (player == team1.getPlayer1()) {

//            client1.sendMessageFromServer(message);
            player.getMyClientHandler().broadcastMessage2(message);
        } else if (player == team1.getPlayer2()) {
            player.getMyClientHandler().broadcastMessage2(message);
//            client2.sendMessageFromServer(message);
        } else if (player == team2.getPlayer1()) {
            player.getMyClientHandler().broadcastMessage2(message);
//            client3.sendMessageFromServer(message);
        } else if (player == team2.getPlayer2()) {
            player.getMyClientHandler().broadcastMessage2(message);
//            client4.sendMessageFromServer(message);
        }
    }

    private Player getNextPlayer(Player currentPlayer) {
        if (currentPlayer == team1.getPlayer1()) {
            return team1.getPlayer2();
        } else if (currentPlayer == team1.getPlayer2()) {
            return team2.getPlayer1();
        } else if (currentPlayer == team2.getPlayer1()) {
            return team2.getPlayer2();
        } else {
            return team1.getPlayer1();
        }
    }

    private void determineTrickWinner() {
        Suit leadSuit = currentTrick.get(0).getSuit();
        Card highestTrump = null;
        Card highestOfLeadSuit = null;

        for (Card card : currentTrick) {
            if (card.getSuit() == trumps) {
                if (highestTrump == null || card.getRank().ordinal() > highestTrump.getRank().ordinal()) {
                    highestTrump = card;
                }
            } else if (card.getSuit() == leadSuit) {
                if (highestOfLeadSuit == null || card.getRank().ordinal() > highestOfLeadSuit.getRank().ordinal()) {
                    highestOfLeadSuit = card;
                }
            }
        }

        Player trickWinner = null;
        if (highestTrump != null) {
            trickWinner = getPlayerByCard(highestTrump);
        } else if (highestOfLeadSuit != null) {
            trickWinner = getPlayerByCard(highestOfLeadSuit);
        }

        if (trickWinner != null) {
            System.out.println("Trick won by " + trickWinner.getName());
            //broadcast
            getCurrentRightPlayer().getMyClientHandler().broadcastMessage("Trick won by " + trickWinner.getName());
            winner = trickWinner;
        } else {
            System.out.println("Trick tied. No winner.");
        }

    }

    private void removePlayedCards(){
        for(Card card : currentTrick){
            team1.getPlayer1().removeFromDeck(card);
            team1.getPlayer2().removeFromDeck(card);
            team2.getPlayer1().removeFromDeck(card);
            team2.getPlayer2().removeFromDeck(card);
        }
        // Clear the current trick for the next round
        currentTrick.clear();
    }

    private Player getPlayerByCard(Card card) {
        if (team1.getPlayer1().hasCard(card)) {
            return team1.getPlayer1();
        } else if (team1.getPlayer2().hasCard(card)) {
            return team1.getPlayer2();
        } else if (team2.getPlayer1().hasCard(card)) {
            return team2.getPlayer1();
        } else if (team2.getPlayer2().hasCard(card)) {
            return team2.getPlayer2();
        }

        return null; // Card not found in any player's hand
    }

    public void playGame() {
        System.out.println(" =========== Welcome to OMI Game! ================");
        play10Rounds();

    }

    public void play10Rounds(){
        Scanner scanner = new Scanner(System.in);
        while (roundNumber <= 10) {  // Play 10 rounds
            System.out.println("\nRound " + roundNumber);

            if (roundNumber == 1) {
                deck.shuffle();
                dealCards();
                nameTrumps();
            }

//            System.out.println("Press enter to play round " + roundNumber);
//            scanner.nextLine();
            if (namedTrump) {
                playTrick();

                roundNumber++;
            }
        }
    }

//    public static void main(String[] args) {
//        OmiGame omiGame = new OmiGame();
//        omiGame.playGame();
//    }
}