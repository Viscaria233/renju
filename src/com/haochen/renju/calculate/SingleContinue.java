package com.haochen.renju.calculate;

import com.haochen.renju.storage.Direction;
import com.haochen.renju.util.PointUtils;

public class SingleContinue {
	private int length;
	private int[] continueEnd;
	private int[] breakPoint;
	private Direction direction;

	public SingleContinue() {
		continueEnd = new int[2];
		breakPoint = new int[2];
	}

	public void generateLength() {
		if (continueEnd[0] == 0 || continueEnd[1] == 0) {
			return;
		}
		if (direction == Direction.horizontal || direction == Direction.mainDiagonal) {
			length = Math.abs(PointUtils.getX(continueEnd[1]) - PointUtils.getX(continueEnd[0])) + 1;
		} else if (direction == Direction.vertical || direction == Direction.counterDiagonal) {
			length = Math.abs(PointUtils.getY(continueEnd[1]) - PointUtils.getY(continueEnd[0])) + 1;
		}
	}

	public void setEnd(int end0, int end1) {
		continueEnd[0] = end0;
		continueEnd[1] = end1;
	}

	public void setSide(int side0, int side1) {
		breakPoint[0] = side0;
		breakPoint[1] = side1;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public int[] getContinueEnd() {
		return continueEnd;
	}

	public void setContinueEnd(int[] end) {
		this.continueEnd = end;
	}

	public int[] getBreakPoint() {
		return breakPoint;
	}

	public void setBreakPoint(int[] side) {
		this.breakPoint = side;
	}

	public int getLength() {
		return length;
	}

	public Direction getDirection() {
		return direction;
	}
}
