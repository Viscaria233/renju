package com.haochen.renju.player;

import java.awt.Color;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public void move() {
        controller.response("human moving", null);
    }

}
