package com.haochen.renju.storage;

import java.awt.Color;
import java.io.Serializable;

/**
 * Created by Haochen on 2016/10/1.
 */
public class PieceColor implements Serializable {
    public static final PieceColor BLACK = new PieceColor(1);
    public static final PieceColor WHITE = new PieceColor(2);

    private int value;
    private PieceColor(int value) {
        this.value = value;
    }

    public PieceColor foeColor() {
        if (value == 1) {
            return WHITE;
        } else {
            return BLACK;
        }
    }

    public Color getAwtColor() {
        if (value == 1) {
            return Color.black;
        } else {
            return Color.white;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PieceColor color = (PieceColor) o;
        return value == color.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return value == 1 ? "[B]" : "[W]";
    }
}
