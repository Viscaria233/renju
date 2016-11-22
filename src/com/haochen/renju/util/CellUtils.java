package com.haochen.renju.util;

import com.haochen.renju.bean.Cell;

import java.awt.*;

/**
 * Created by Haochen on 2016/11/2.
 */
public class CellUtils {

    public static String toString(int cell) {
        switch (cell) {
            case Cell.EMPTY:
                return "[N]";
            case Cell.BLACK:
                return "[B]";
            case Cell.WHITE:
                return "[W]";
            case Cell.FORBIDDEN:
                return "[F]";
        }
        return "";
    }

    public static int foeColor(int c) {
        if (c == Cell.BLACK) {
            return Cell.WHITE;
        } else if (c == Cell.WHITE) {
            return Cell.BLACK;
        }
        return 0;
    }

    public static Color getAwtColor(int c) {
        if (c == Cell.BLACK) {
            return Color.black;
        } else if (c == Cell.WHITE) {
            return Color.white;
        }
        return null;
    }
}
