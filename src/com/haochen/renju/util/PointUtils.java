package com.haochen.renju.util;

import com.haochen.renju.storage.Point;

/**
 * Created by Haochen on 2016/11/2.
 */
public class PointUtils {
    public static String toString(int point) {
        return "[" + (char)('A' + (point >> 4) - 1) + (point & 0xf) + "]";
    }

    public static String toString(Point point) {
        return toString(parse(point));
    }

    public static int parse(int x, int y) {
        return (x << 4) + y;
    }

    public static int parse(Point point) {
        return parse(point.x, point.y);
    }

    public static Point build(int p) {
        return build(getX(p), getY(p));
    }

    public static Point build(int x, int y) {
        return new Point(x, y);
    }

    public static int getX(int p) {
        return p >> 4;
    }

    public static int getY(int p) {
        return p & 0xf;
    }

    public static int move(int p, int dx, int dy) {
        int x = getX(p) + dx;
        int y = getY(p) + dy;
        if (x >= 1 && y >= 1 && x <= 15 && y <= 15) {
            return parse(x, y);
        } else {
            return 0;
        }
    }

    public static boolean isValid(int p) {
        int x = getX(p);
        int y = getY(p);
        return x >= 1 && y >= 1 && x <= 15 && y <= 15;
    }
}
