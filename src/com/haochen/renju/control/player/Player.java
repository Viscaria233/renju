package com.haochen.renju.control.player;

import com.haochen.renju.control.Mediator;
import com.haochen.renju.storage.PieceColor;

public abstract class Player {
    
    protected String name;
    protected PieceColor color;
    protected Mediator mediator;
    
    public Player(String name, PieceColor color) {
        this.name = name;
        this.color = color;
    }
    
    public String getName() {
        return name;
    }
    
    public PieceColor getColor() {
        return color;
    }
    
    public String getColorString() {
        if (color.equals(PieceColor.BLACK)) {
            return "black";
        } else {
            return "white";
        }
    }
    
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
    
    public abstract void move();
}
