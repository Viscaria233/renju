package com.haochen.renju.bean;

import com.haochen.renju.storage.Point;

import java.io.Serializable;

public class Cell implements Serializable {

    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;

    public static final Cell EMPTY_CELL = new Cell(0, null, EMPTY);

    protected int index;
    protected Point point;
    protected int type;

    public Cell(int index, Point point, int type) {
        this.index = index;
        this.point = point;
        this.type = type;
    }

    public Cell(int index, int x, int y, int type) {
        this.index = index;
        this.point = new Point(x, y);
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public Point getPoint() {
        return point;
    }

    public int getType() {
        return type;
    }

    public boolean isPiece() {
        return type == BLACK | type == WHITE;
    }
}
