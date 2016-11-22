package com.haochen.renju.bean;

import java.util.List;

/**
 * Created by Haochen on 2016/11/7.
 *
 * ½ø¹¥×ÊÔ´
 */
public class Res {

    public static final int NONE = -1;
    public static final int FIVE = 1;
    public static final int ALIVE_FOUR = 2;
    public static final int ASLEEP_FOUR = 3;
    public static final int ALIVE_THREE = 4;
    public static final int ASLEEP_THREE = 5;
    public static final int ALIVE_TWO = 6;
    public static final int ASLEEP_TWO = 7;
    
    private int color;
    private int type;
    private int direction;
//    private List<Integer> points;
//    private List<Integer> attacks;

    public Res() {}

//    public Res(int type, int type, int direction, List<Integer> points, List<Integer> attacks) {
//        this.type = type;
//        this.type = type;
//        this.direction = direction;
//        this.points = points;
//        this.attacks = attacks;
//    }

    public Res(int color, int type, int direction) {
        this.color = color;
        this.type = type;
        this.direction = direction;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

//    public List<Integer> getPoints() {
//        return points;
//    }
//
//    public void setPoints(List<Integer> points) {
//        this.points = points;
//    }
//
//    public List<Integer> getAttacks() {
//        return attacks;
//    }
//
//    public void setAttacks(List<Integer> attacks) {
//        this.attacks = attacks;
//    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Res res = (Res) o;
//
//        if (type != res.type) return false;
//        if (type != res.type) return false;
//        if (direction != res.direction) return false;
//        if (points != null ? !points.equals(res.points) : res.points != null) return false;
//        return attacks != null ? attacks.equals(res.attacks) : res.attacks == null;
//
//    }
//
//    @Override
//    public int hashCode() {
//        int result = type;
//        result = 31 * result + type;
//        result = 31 * result + direction;
//        result = 31 * result + (points != null ? points.hashCode() : 0);
//        result = 31 * result + (attacks != null ? attacks.hashCode() : 0);
//        return result;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Res res = (Res) o;

        if (color != res.color) return false;
        if (type != res.type) return false;
        return direction == res.direction;

    }

    @Override
    public int hashCode() {
        int result = color;
        result = 31 * result + type;
        result = 31 * result + direction;
        return result;
    }
}
