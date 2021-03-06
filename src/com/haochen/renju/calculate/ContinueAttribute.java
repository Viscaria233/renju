package com.haochen.renju.calculate;

import com.haochen.renju.storage.Direction;

public class ContinueAttribute {
	private int color;
	private int location;
	
	private SingleContinue horizontal;
	private SingleContinue vertical;
	private SingleContinue mainDiagonal;
	private SingleContinue counterDiagonal;
	
	public ContinueAttribute() {
	}
	
	public ContinueAttribute(int color, int location, Direction direction) {
		this.color = color;
		this.location = location;
		
		if (direction.contains(Direction.horizontal)) {
			horizontal = new SingleContinue();
			horizontal.setDirection(Direction.horizontal);
		}
		if (direction.contains(Direction.vertical)) {
			vertical = new SingleContinue();
			vertical.setDirection(Direction.vertical);
		}
		if (direction.contains(Direction.mainDiagonal)) {
			mainDiagonal = new SingleContinue();
			mainDiagonal.setDirection(Direction.mainDiagonal);
		}
		if (direction.contains(Direction.counterDiagonal)) {
			counterDiagonal = new SingleContinue();
			counterDiagonal.setDirection(Direction.counterDiagonal);
		}
	}
	
	public void initialize(int color, int location, Direction direction) {
		this.color = color;
		this.location = location;
		
		if (horizontal != null && direction.contains(Direction.horizontal)) {
			horizontal = new SingleContinue();
			horizontal.setDirection(Direction.horizontal);
		}
		if (vertical != null && direction.contains(Direction.vertical)) {
			vertical = new SingleContinue();
			vertical.setDirection(Direction.vertical);
		}
		if (mainDiagonal != null && direction.contains(Direction.mainDiagonal)) {
			mainDiagonal = new SingleContinue();
			mainDiagonal.setDirection(Direction.mainDiagonal);
		}
		if (counterDiagonal != null && direction.contains(Direction.counterDiagonal)) {
			counterDiagonal = new SingleContinue();
			counterDiagonal.setDirection(Direction.counterDiagonal);
		}
	}

	public void setContinue(SingleContinue single) {
		if (single.getDirection() == Direction.horizontal) {
			horizontal = single;
		} else if (single.getDirection() == Direction.vertical) {
			vertical = single;
		} else if (single.getDirection() == Direction.mainDiagonal) {
			mainDiagonal = single;
		} else if (single.getDirection() == Direction.counterDiagonal) {
			counterDiagonal = single;
		}
	}
	
	public SingleContinue getContinue(Direction direction) {
		if (direction == Direction.horizontal) {
			return horizontal;
		} else if (direction == Direction.vertical) {
			return vertical;
		} else if (direction == Direction.mainDiagonal) {
			return mainDiagonal;
		} else if (direction == Direction.counterDiagonal) {
			return counterDiagonal;
		}
		return null;
	}

	public int getColor() {
		return color;
	}

	public int getLocation() {
		return location;
	}
	
	public void append(ContinueAttribute attribute) {
		if (location != attribute.location) {
			return;
		}
		
		if (horizontal == null && attribute.horizontal != null) {
			horizontal = attribute.horizontal;
		}
		if (vertical == null && attribute.vertical != null) {
			vertical = attribute.vertical;
		}
		if (mainDiagonal == null && attribute.mainDiagonal != null) {
			mainDiagonal = attribute.mainDiagonal;
		}
		if (counterDiagonal == null && attribute.counterDiagonal != null) {
			counterDiagonal = attribute.counterDiagonal;
		}
	}
}
