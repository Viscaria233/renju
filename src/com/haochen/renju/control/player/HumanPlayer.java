package com.haochen.renju.control.player;


public class HumanPlayer extends Player {

    public HumanPlayer(String name, int color) {
        super(name, color);
    }

    @Override
    public void move() {
        mediator.getOperator().humanMove();
    }

}
