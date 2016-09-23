package com.haochen.renju.common;

import java.awt.Color;

import com.haochen.renju.form.Point;

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
    public boolean isHandCut() {
        return false;
    }

}
