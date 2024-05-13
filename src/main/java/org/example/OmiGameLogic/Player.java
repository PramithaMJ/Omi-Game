package org.example.OmiGameLogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;
    private BufferedReader reader;
    private ClientHandler myClientHandler;

    public Player(String name, ClientHandler clientHandler) {
        this.name = name;
        hand = new ArrayList<>();
        reader = new BufferedReader(new InputStreamReader(System.in));
        myClientHandler = clientHandler;
    }

    public ClientHandler getMyClientHandler() {
        return myClientHandler;
    }

    public void addToDeck(Card card){
        hand.add(card);
    }

    public int findCardIndex(Card card) {
        Rank targetRank = card.getRank();
        Suit targetSuit = card.getSuit();

        for (int i = 0; i < hand.size(); i++) {
            Card currentCard = hand.get(i);
            if (currentCard.getRank() == targetRank && currentCard.getSuit() == targetSuit) {
                return i; // Return the index if the card with matching rank and suit is found
            }
        }
        return -1; // Return -1 if the card is not found in the hand
    }

    public void removeFromDeck(Card card){
        int index = findCardIndex(card);
        if (index != -1) {
            hand.remove(index);
        } else {
            //System.out.println("Card not found in the hand.");
        }
    }

    public boolean hasCard(Card card) {
        Rank targetRank = card.getRank();
        Suit targetSuit = card.getSuit();

        for (Card c : hand) {
            Rank currentRank = c.getRank();
            Suit currentSuit = c.getSuit();
            if (currentRank == targetRank && currentSuit == targetSuit) {
                return true; // Card found in the hand
            }
        }
        return false; // Card not found in the hand
    }

    public String getName(){
        return name;
    }

    public Card playCard() {
//        System.out.println(name + ", Rank:");
        myClientHandler.broadcastMessage2("===============================");
        myClientHandler.broadcastMessage2(name + ", Rank:");
        Rank rank = null;
        Suit suit = null;
        try {
//            String rankInput = reader.readLine().toUpperCase();
            String rankInput = myClientHandler.bufferedReader.readLine().toUpperCase();
            System.out.println(rankInput);
            rank = Rank.valueOf(rankInput);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Invalid input. Please enter a valid rank.");
            myClientHandler.broadcastMessage2("Invalid input. Please enter a valid rank.");
            return playCard();
        }

        System.out.println("Suit");
        myClientHandler.broadcastMessage2("Suit");
        try {
//            String suitInput = reader.readLine().toUpperCase();
            String suitInput = myClientHandler.bufferedReader.readLine().toUpperCase();
            suit = Suit.valueOf(suitInput);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Invalid input. Please enter a valid suit.");
            myClientHandler.broadcastMessage2("Invalid input. Please enter a valid suit.");
            return playCard();
        }

        Card selectedCard = new Card(rank, suit);
        System.out.println(selectedCard);

//        myClientHandler.broadcastMessage2(selectedCard.toString());
        if (hand.contains(selectedCard)) {
            printHand();
            System.out.println("You don't have that card. Please select a card from your hand.");
            myClientHandler.broadcastMessage2("You don't have that card. Please select a card from your hand.");
            return playCard();
        }
        myClientHandler.broadcastMessage2(selectedCard.toString());
        hand.remove(selectedCard);
        return selectedCard;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void printHand() {
//        myClientHandler.broadcastMessage2(name+" Your hand:");
        myClientHandler.broadcastMessage2(name+" Your hand:");
        for (Card card : hand) {
            myClientHandler.broadcastMessage2(card.toString());
        }
//        System.out.println(name+" Your hand:");
//        for (Card card : hand) {
//            System.out.println(card);
//        }
    }
}
