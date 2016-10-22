package com.haochen.renju.bean;

import com.haochen.renju.storage.PieceColor;
import com.haochen.renju.storage.Point;

public abstract class Piece extends Cell {
    
	protected int index;
	protected Point location;
	protected PieceColor color;
	
	public Piece() {
	}

	public Piece(int index, Point location, PieceColor color) {
		this.index = index;
		this.location = location;
		this.color = color;
	}
	
	public Piece(int index, int x, int y, PieceColor color) {
        this.index = index;
        this.location = new Point(x, y);
        this.color = color;
    }
	
	@Override
	public int getIndex() {
	    return index;
	}
	
	@Override
	public Point getLocation() {
	    return location;
	}
	
	@Override
	public PieceColor getColor() {
	    return color;
	}
	
	@Override
	public boolean isPiece() {
	    return true;
	}
	
	@Override
	public boolean isForbiddenMove() {
	    return false;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + index;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Piece other = (Piece) obj;
        if (color == null) {
            if (other.color != null)
                return false;
        } else if (!color.equals(other.color))
            return false;
        if (index != other.index)
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        return true;
    }
	
}
