package com.haochen.renju.bean;

import java.awt.Color;

import com.haochen.renju.storage.Point;

public class RealPiece extends Piece {

    public RealPiece() {
        super();
    }
    
    public RealPiece(int index, Point boardLocation, Color color) {
        super(index, boardLocation, color);
    }

    public RealPiece(int index, int x, int y, Color color) {
        super(index, x, y, color);
    }

}
