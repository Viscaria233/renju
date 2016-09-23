package com.haochen.renju.common;

import java.awt.Color;

import com.haochen.renju.form.Point;

public class HandCut extends Cell {

    private Point location;
    
    public HandCut(Point location) {
        this.location = location;
    }
    
    public HandCut(int x, int y) {
        this.location = new Point(x, y);
    }
    
    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public Point getLocation() {
        return location;
    }

    @Override
    public Color getColor() {
        return null;
    }
    
    @Override
    public boolean isPiece() {
        return false;
    }
    
    @Override
    public boolean isHandCut() {
        return true;
    }

}
