package com.haochen.renju.form;

public class SingleContinue {
	private int length;
	private Point[] continueEnd;
	private Point[] breakPoint;
	private Direction direction;
	
	public SingleContinue() {
		continueEnd = new Point[2];
		breakPoint = new Point[2];
	}
	
	public void generateLength() {
		if (continueEnd[0] == null || continueEnd[1] == null) {
			return;
		}
		if (direction == Direction.horizontal || direction == Direction.mainDiagonal) {
			length = Math.abs(continueEnd[1].x - continueEnd[0].x) + 1;
		} else if (direction == Direction.vertical || direction == Direction.counterDiagonal) {
			length = Math.abs(continueEnd[1].y - continueEnd[0].y) + 1;
		}
	}
	
	public void setEnd(Point end0, Point end1) {
		continueEnd[0] = end0;
		continueEnd[1] = end1;
	}
	
	public void setSide(Point side0, Point side1) {
		breakPoint[0] = side0;
		breakPoint[1] = side1;
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public Point[] getContinueEnd() {
		return continueEnd;
	}

	public void setContinueEnd(Point[] end) {
		this.continueEnd = end;
	}

	public Point[] getBreakPoint() {
		return breakPoint;
	}

	public void setBreakPoint(Point[] side) {
		this.breakPoint = side;
	}

	public int getLength() {
		return length;
	}

	public Direction getDirection() {
		return direction;
	}
}
