package com.haochen.renju.bean;

import java.awt.Color;

import com.haochen.renju.storage.Point;

public class EmptyCell extends Cell {

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public Point getLocation() {
        return null;
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
        return false;
    }

}
