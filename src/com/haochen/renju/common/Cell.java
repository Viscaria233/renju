package com.haochen.renju.common;

import java.awt.Color;

import com.haochen.renju.form.Point;

public abstract class Cell {
    public static final Cell empty = new EmptyCell();
    public abstract int getIndex();
    public abstract Point getLocation();
    public abstract Color getColor();
    public abstract boolean isPiece();
    public abstract boolean isHandCut();
}
