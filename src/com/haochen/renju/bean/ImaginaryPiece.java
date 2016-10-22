package com.haochen.renju.bean;

import com.haochen.renju.storage.PieceColor;
import com.haochen.renju.storage.Point;

public class ImaginaryPiece extends Piece {

    public ImaginaryPiece(Point boardLocation, PieceColor color) {
        super(-1, boardLocation, color);
    }

}
