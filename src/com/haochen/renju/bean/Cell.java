package com.haochen.renju.bean;

import com.haochen.renju.storage.PieceColor;
import com.haochen.renju.storage.Point;

public abstract class Cell {
    public static final Cell empty = new EmptyCell();
    public abstract int getIndex();
    public abstract Point getLocation();
    public abstract PieceColor getColor();
    public abstract boolean isPiece();
    public abstract boolean isForbiddenMove();
}
