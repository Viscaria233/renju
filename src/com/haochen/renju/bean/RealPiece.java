package com.haochen.renju.bean;

import com.haochen.renju.storage.PieceColor;
import com.haochen.renju.storage.Point;

public class RealPiece extends Piece {

    public RealPiece() {
        super();
    }

    public RealPiece(int index, Point boardLocation, PieceColor color) {
        super(index, boardLocation, color);
    }

    public RealPiece(int index, int x, int y, PieceColor color) {
        super(index, x, y, color);
    }

}
