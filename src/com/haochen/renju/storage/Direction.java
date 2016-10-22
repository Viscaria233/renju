package com.haochen.renju.storage;

public class Direction {
    public static final Direction horizontal = new Direction(1 << 3) ;
    public static final Direction vertical = new Direction(1 << 2);
    public static final Direction mainDiagonal = new Direction(1 << 1);
    public static final Direction counterDiagonal = new Direction(1);
    public static final Direction empty = new Direction(0);
    public static final Direction all = new Direction((1 << 4) - 1);
    
    private int value;
    private int quantity;
    
    public Direction() {
        value = 0;
        quantity = 0;
    }
    
    private Direction(Direction direction) {
    	this.value = direction.value;
    	this.quantity = direction.quantity;
    }
    
    private Direction(int directionValue) {
        value = directionValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Direction other = (Direction) obj;
        if (value != other.value)
            return false;
        return true;
    }
    
    public void add(Direction direction) {
        if (direction.value != 0 && direction.value != value) {
            quantity++;
        }
        value |= direction.value;
    }

    public void doubleAdd(Direction direction) {
        quantity += 2;
        value |= direction.value;
    }
    
    public Direction remove(Direction direction) {
    	Direction backup = new Direction(this);
    	if (backup.contains(direction)) {
    		backup.value -= direction.value;
    		if (backup.quantity > 0) {
    			backup.quantity--;
    		}
    	}
    	return backup;
    }
    
    public void append(Direction direction) {
    	quantity += direction.quantity;
    	value |= direction.value;
    }
    
    public boolean isValid() {
        if (value == 0) {
            return false;
        }
        return true;
    }
    
    public int getValue() {
        return value;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean contains(Direction direction) {
    	return (value & direction.value) != 0;
    }
    
    public static Direction[] createDirectionArray() {
    	Direction[] direction = new Direction[4];
    	direction[0] = Direction.horizontal;
    	direction[1] = Direction.vertical;
    	direction[2] = Direction.mainDiagonal;
    	direction[3] = Direction.counterDiagonal;
    	return direction;
    }
    
    public boolean isSingle() {
    	if (this.equals(horizontal)) {
    		return true;
    	}
    	if (this.equals(vertical)) {
    		return true;
    	}
    	if (this.equals(mainDiagonal)) {
    		return true;
    	}
    	if (this.equals(counterDiagonal)) {
    		return true;
    	}
    	return false;
    }
    
    @Override
    public String toString() {
        String str = "";
        if (this.contains(horizontal)) {
            str += "[horizontal]";
        }
        if (this.contains(vertical)) {
            str += "[vertical]";
        }
        if (this.contains(mainDiagonal)) {
            str += "[mainDiagonal]";
        }
        if (this.contains(counterDiagonal)) {
            str += "[counterDiagonal]";
        }
        return "Direction  " + str + "    [value=" + value + ", quantity=" + quantity + "]";
    }
}
