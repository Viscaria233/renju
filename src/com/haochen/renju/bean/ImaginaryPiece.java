package com.haochen.renju.bean;

import java.awt.Color;

import com.haochen.renju.storage.Point;

public class ImaginaryPiece extends Piece {

    public ImaginaryPiece(Point boardLocation, Color color) {
        super(-1, boardLocation, color);
    }

}
