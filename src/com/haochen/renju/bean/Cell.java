package com.haochen.renju.bean;

import com.haochen.renju.storage.Point;

import java.io.Serializable;

public abstract class Cell implements Serializable {

    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final int FORBIDDEN = 3;

    public abstract int getIndex();
    public abstract Point getPoint();
    public abstract int getType();
    public abstract boolean isPiece();
}
