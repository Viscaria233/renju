package com.haochen.renju.storage;

import java.io.Serializable;

public class Point implements Serializable {
    
    public int x;
    public int y;
    
    public Point() {
        x = 0;
        y = 0;
    }
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public boolean isEmpty() {
        return (x == 0 || y == 0);
    }
    
    public boolean isValid() {
        if (x > 15 || x < 1) {
            return false;
        }
        if (y > 15 || y < 1) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Point [x=" + x + ", y=" + y + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point other = (Point) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }
}
