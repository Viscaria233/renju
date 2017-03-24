package com.haochen.renju.main;

/**
 * Created by Haochen on 2016/9/23.
 */
public class Config {
    public static boolean usingForbiddenMove = false;
    public static boolean shouldStopFinding = false;
    public static boolean GAME_OVER = false;

    public static class AILevel {
        public static int level;
        public static final int NONE = -1;
        public static final int LOW = 1;
        public static final int NORMAL = 2;
        public static final int HIGH = 3;
    }
}
