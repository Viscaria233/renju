package com.haochen.renju.control.player;

public class AIPlayer extends Player {

    public AIPlayer(String name, int color) {
        super(name, color);
    }

    @Override
    public void move() {
        mediator.getOperator().aiMove();
    }

}
