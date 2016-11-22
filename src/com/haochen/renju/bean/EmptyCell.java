package com.haochen.renju.bean;

import com.haochen.renju.storage.Point;
import com.haochen.renju.util.CellUtils;

public class EmptyCell extends Cell {

    public static final EmptyCell INSTANCE = new EmptyCell();

    private EmptyCell() {}

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public Point getPoint() {
        return null;
    }

    @Override
    public int getType() {
        return Cell.EMPTY;
    }

    @Override
    public boolean isPiece() {
        return false;
    }
}
