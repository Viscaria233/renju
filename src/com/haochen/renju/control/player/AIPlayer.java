package com.haochen.renju.control.player;

import com.haochen.renju.storage.PieceColor;

public class AIPlayer extends Player {

    public AIPlayer(String name, PieceColor color) {
        super(name, color);
    }

    @Override
    public void move() {
        mediator.getOperator().aiMove();
    }

}
