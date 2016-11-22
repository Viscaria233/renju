package com.haochen.renju.util;

import com.haochen.renju.storage.Direction;

/**
 * Created by Haochen on 2016/11/8.
 */
public class DirectionUtils {
    public static final int L_R = 1 << 3;
    public static final int T_B = 1 << 2;
    public static final int LT_RB = 1 << 1;
    public static final int LB_RT = 1;
    public static final int EMPTY = 0;
    public static final int ALL = (1 << 4) - 1;

    public int remove(int d1, int d2) {
        return ~(d1 & d2) & d1;
    }

    public boolean contains(int d1, int d2) {
        return (d1 & d2) != 0;
    }

    public boolean isSingle(int d) {
        return d == L_R || d == T_B || d == LT_RB || d == LB_RT;
    }

    public static int[] directionArray() {
        int[] result = new int[4];
        result[0] = L_R;
        result[1] = T_B;
        result[2] = LT_RB;
        result[3] = LB_RT;
        return result;
    }

    public String toString(int d) {
        String str = "";
        if (contains(d, L_R)) {
            str += "[©¤]";
        }
        if (contains(d, T_B)) {
            str += "[©¦]";
        }
        if (contains(d, LT_RB)) {
            str += "[¨v]";
        }
        if (contains(d, LB_RT)) {
            str += "[¨u]";
        }
        return "Direction  " + str + "    [value=" + d + "]";
    }
}
