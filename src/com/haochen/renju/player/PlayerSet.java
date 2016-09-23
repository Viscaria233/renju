package com.haochen.renju.player;

import java.awt.Color;

public class PlayerSet {
    
    private final static int BLACK = 0;
    private final static int WHITE = 1;
    private Player[] players = new Player[2];
    private int moving = BLACK;

    public void addPlayer(Player player) {
        Color color = player.getColor();
        if (color.equals(Color.black)) {
            players[BLACK] = player;
        } else if (color.equals(Color.white)) {
            players[WHITE] = player;
        }
    }
    
    public Player getMovingPlayer() {
        return players[moving];
    }
    
    public void move() {
        players[moving].move();
    }
    
    public void exchangePlayer() {
        moving = 1 - moving;
    }
}
