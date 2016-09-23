package com.haochen.renju.common;

import com.haochen.renju.form.Point;

public interface GetChessObject {
	public abstract Piece getChess(int index);
	public abstract Piece getChess(Point location);
	public abstract Piece getLastChess();
	public abstract int getChessNumber();
}
