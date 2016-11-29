package com.haochen.renju.storage;

import com.haochen.renju.bean.Cell;

import java.io.Serializable;

public class PieceMap implements Cloneable, Serializable {
    
    private Cell[][] map;
    
    PieceMap() {
        map = new Cell[15][15];
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                map[i][j] = Cell.EMPTY_CELL;
            }
        }
    }
    
    public boolean available(Point boardLocation) {
        return boardLocation.isValid() && !map[boardLocation.x - 1][boardLocation.y - 1].isPiece();
    }
    
    void addCell(Cell cell) {
        if (cell.getType() == Cell.EMPTY) {
            return;
        }
        Point point = cell.getPoint();
        map[point.x - 1][point.y - 1] = cell;
    }
    
    void removeCell(Point point) {
        removeCell(point.x, point.y);
    }
    
    private void removeCell(int x, int y) {
        map[x - 1][y - 1] = Cell.EMPTY_CELL;
    }
    
    void clear() {
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                map[i][j] = Cell.EMPTY_CELL;
            }
        }
    }
    
    void display() {
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
    
    private Cell getCell(int x, int y) {
        return map[x - 1][y - 1];
    }
}
