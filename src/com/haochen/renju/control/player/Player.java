package com.haochen.renju.control.player;

import com.haochen.renju.bean.Cell;
import com.haochen.renju.control.Mediator;

public abstract class Player {
    
    protected String name;
    protected int color;
    protected Mediator mediator;
    
    public Player(String name, int color) {
        this.name = name;
        this.color = color;
    }
    
    public String getName() {
        return name;
    }
    
    public int getColor() {
        return color;
    }
    
    public String getColorString() {
        if (color == Cell.BLACK) {
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
