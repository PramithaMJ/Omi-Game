package org.example.OmiGameLogic;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private Player player1;
    private Player player2;
    private String teamName;
    private List<Card> teamHand;

    public Team(String teamName, Player player1, Player player2) {
        this.teamName = teamName;
        this.player1 = player1;
        this.player2 = player2;
        teamHand = new ArrayList<>();
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<Card> getTeamHand() {
        return teamHand;
    }

    public void setTeamHand(List<Card> teamHand) {
        this.teamHand = teamHand;
    }

    private boolean hasCard(Card card){
        return teamHand.contains(card);
    }
}
