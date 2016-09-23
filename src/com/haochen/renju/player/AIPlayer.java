package com.haochen.renju.player;

import java.awt.Color;

public class AIPlayer extends Player {

    public AIPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public void move() {
        controller.response("ai moving", null);
    }

}
