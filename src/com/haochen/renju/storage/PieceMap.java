package com.haochen.renju.storage;

import com.haochen.renju.bean.Cell;
import com.haochen.renju.bean.ForbiddenMove;
import com.haochen.renju.bean.RealPiece;

import java.io.Serializable;
import java.util.Iterator;

public class PieceMap implements Cloneable, Iterable<Point>, Serializable {
    
    private Cell[][] map;
    
    public PieceMap() {
        map = new Cell[15][15];
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                map[i][j] = Cell.empty;
            }
        }
    }
    
    public boolean available(Point boardLocation) {
        if (!boardLocation.isValid()) {
            return false;
        }
        return !map[boardLocation.x - 1][boardLocation.y - 1].isPiece();
    }
    
    public void addCell(Cell cell) {
        if (cell == Cell.empty) {
            return;
        }
        Point point = cell.getLocation();
        map[point.x - 1][point.y - 1] = cell;
    }
    
    public void addPiece(int index, Point boardLocation, PieceColor color) {
        map[boardLocation.x - 1][boardLocation.y - 1] = new RealPiece(index, boardLocation, color);
    }
    
    public void addPiece(int index, int x, int y, PieceColor color) {
        map[x - 1][y - 1] = new RealPiece(index, x, y, color);
    }
    
    public void addForbiddenMark(Point boardLocation) {
        map[boardLocation.x - 1][boardLocation.y - 1] = new ForbiddenMove(boardLocation);
    }
    
    public void addForbiddenMark(int x, int y) {
        map[x - 1][y - 1] = new ForbiddenMove(x, y);
    }
    
    public void removeCell(Point boardLocation) {
        map[boardLocation.x - 1][boardLocation.y - 1] = Cell.empty;
    }
    
    public void removeCell(int x, int y) {
        map[x - 1][y - 1] = Cell.empty;
    }
    
    public void clear() {
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                map[i][j] = null;
            }
        }
    }
    
    public void display() {
        for (int i = 14; i >= 0; --i) {
            for (int j = 0; j < 15; ++j) {
                if (!map[j][i].isPiece()) {
                    System.out.print("0  ");
                } else {
                    System.out.print("" + map[j][i].getIndex() + "  ");
                }
            }
            System.out.println();
        }
    }

    public PieceMap clone() throws CloneNotSupportedException {
        PieceMap pieceMap = (PieceMap) super.clone();
        pieceMap.map = this.map.clone();
        for (int i = 0; i < 15; ++i) {
            pieceMap.map[i] = this.map[i].clone();
        }
        return pieceMap;
    }
    
    public Cell getCell(Point location) {
        return getCell(location.x, location.y);
    }
    
    public Cell getCell(int x, int y) {
        return map[x - 1][y - 1];
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<Point>() {
            int x = 1;
            int y = 1;

            @Override
            public boolean hasNext() {
                return x <= 15 && y <= 15;
            }

            @Override
            public Point next() {
                Point result = new Point(x, y);
                if (y < 15) {
                    ++y;
                } else {
                    y = 1;
                    ++x;
                }
                return result;
            }

            @Override
            public void remove() {
            }
        };
    }
}
