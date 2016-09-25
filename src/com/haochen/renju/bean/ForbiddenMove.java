package com.haochen.renju.bean;

import java.awt.Color;

import com.haochen.renju.storage.Point;

public class ForbiddenMove extends Cell {

    private Point location;
    
    public ForbiddenMove(Point location) {
        this.location = location;
    }
    
    public ForbiddenMove(int x, int y) {
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
    public boolean isForbiddenMove() {
        return true;
    }

}
