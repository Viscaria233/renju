package com.haochen.renju.control.player;

import com.haochen.renju.bean.Piece;
import com.haochen.renju.storage.PieceColor;


public class HumanPlayer extends Player {

    public HumanPlayer(String name, PieceColor color) {
        super(name, color);
    }

    @Override
    public void move() {
        mediator.getOperator().humanMove();
    }

}
