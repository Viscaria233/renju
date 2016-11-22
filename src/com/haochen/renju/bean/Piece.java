package com.haochen.renju.bean;

import com.haochen.renju.storage.Point;

public class Piece extends Cell {
    
	protected int index;
	protected Point point;
	protected int type;
	
	public Piece() {
	}

	public Piece(int index, Point point, int type) {
		this.index = index;
		this.point = point;
		this.type = type;
	}
	
	public Piece(int index, int x, int y, int type) {
        this.index = index;
        this.point = new Point(x, y);
        this.type = type;
    }
	
	@Override
	public int getIndex() {
	    return index;
	}
	
	@Override
	public Point getPoint() {
	    return point;
	}
	
	@Override
	public int getType() {
	    return type;
	}
	
	@Override
	public boolean isPiece() {
	    return true;
	}
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Piece piece = (Piece) o;

        if (index != piece.index) return false;
        if (type != piece.type) return false;
        return point != null ? point.equals(piece.point) : piece.point == null;

    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + (point != null ? point.hashCode() : 0);
        result = 31 * result + type;
        return result;
    }
}
