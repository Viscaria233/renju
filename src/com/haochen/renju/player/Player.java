package com.haochen.renju.player;

import java.awt.Color;

import com.haochen.renju.common.Controller;

public abstract class Player {
    
    protected String name;
    protected Color color;
    protected Controller controller;
    
    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }
    
    public String getName() {
        return name;
    }
    
    public Color getColor() {
        return color;
    }
    
    public String getColorString() {
        if (color.equals(Color.black)) {
            return "black";
        } else {
            return "white";
        }
    }
    
    public void setController(Controller controller) {
        this.controller = controller;
    }
    
    public abstract void move();
}
