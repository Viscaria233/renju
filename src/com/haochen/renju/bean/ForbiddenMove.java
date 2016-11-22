package com.haochen.renju.bean;

import com.haochen.renju.storage.Point;

public class ForbiddenMove extends Cell {

    private Point point;
    
    public ForbiddenMove(Point point) {
        this.point = point;
    }
    
    public ForbiddenMove(int x, int y) {
        this.point = new Point(x, y);
    }
    
    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public Point getPoint() {
        return point;
    }

    @Override
    public int getType() {
        return Cell.FORBIDDEN;
    }
    
    @Override
    public boolean isPiece() {
        return false;
    }
}
