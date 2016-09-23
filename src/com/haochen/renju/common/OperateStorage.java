package com.haochen.renju.common;

import com.haochen.renju.form.Point;

public interface OperateStorage {
	public abstract boolean addChess(Point location);
	public abstract boolean removeLastChess();
}
