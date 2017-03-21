package com.haochen.renju.storage;

import com.haochen.renju.util.CellUtils;
import com.haochen.renju.util.PointUtils;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;

        if (index != cell.index) return false;
        if (type != cell.type) return false;
        return point != null ? point.equals(cell.point) : cell.point == null;
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + (point != null ? point.hashCode() : 0);
        result = 31 * result + type;
        return result;
    }

    @Override
    public String toString() {
        if (point == null) {
            return super.toString();
        }
        return "Cell{" +
                "index=" + index +
                " " + PointUtils.toString(point) +
                " " + CellUtils.toString(type) +
                '}';
    }
}
