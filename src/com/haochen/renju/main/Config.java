package com.haochen.renju.main;

import com.haochen.renju.storage.PieceColor;

/**
 * Created by Haochen on 2016/9/23.
 */
public class Config {

    public static class AILevel {
        public static int level;
        public static final int NONE = -1;
        public static final int LOW = 1;
        public static final int NORMAL = 2;
        public static final int HIGH = 3;
    }

    public static class Test {
        public static PieceColor color = PieceColor.BLACK;
    }
}
