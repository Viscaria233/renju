package com.haochen.renju.bean;

import com.haochen.renju.storage.PieceColor;
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
    public PieceColor getColor() {
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
