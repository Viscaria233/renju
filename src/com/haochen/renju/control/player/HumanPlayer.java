package com.haochen.renju.control.player;

import java.awt.Color;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public void move() {
        mediator.response("human moving", null);
    }

}
