package com.haochen.renju.util;

import java.awt.Color;

import com.haochen.renju.common.Cell;
import com.haochen.renju.common.HandCut;
import com.haochen.renju.common.RealPiece;
import com.haochen.renju.form.Point;

public class PieceMap implements Cloneable {
    
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
    
    public void addPiece(int index, Point boardLocation, Color color) {
        map[boardLocation.x - 1][boardLocation.y - 1] = new RealPiece(index, boardLocation, color);
    }
    
    public void addPiece(int index, int x, int y, Color color) {
        map[x - 1][y - 1] = new RealPiece(index, x, y, color);
    }
    
    public void addHandCut(Point boardLocation) {
        map[boardLocation.x - 1][boardLocation.y - 1] = new HandCut(boardLocation);
    }
    
    public void addHandCut(int x, int y) {
        map[x - 1][y - 1] = new HandCut(x, y);
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
    
}
