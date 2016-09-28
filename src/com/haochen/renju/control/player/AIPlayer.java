package com.haochen.renju.control.player;

import java.awt.Color;

public class AIPlayer extends Player {

    public AIPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public void move() {
        try {
            Thread.sleep(1000);
            mediator.getOperator().aiMove();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
