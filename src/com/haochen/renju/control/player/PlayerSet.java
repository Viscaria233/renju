package com.haochen.renju.control.player;

import com.haochen.renju.bean.Cell;

public class PlayerSet {
    
    private final static int BLACK = 0;
    private final static int WHITE = 1;
    private Player[] players = new Player[2];
    private int moving = BLACK;

    public void addPlayer(Player player) {
        int color = player.getColor();
        if (color == Cell.BLACK) {
            players[BLACK] = player;
        } else if (color == Cell.WHITE) {
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
