package com.haochen.renju.util;

import com.haochen.renju.bean.Piece;
import com.haochen.renju.bean.Piece;

/**
 * Created by Haochen on 2016/11/2.
 */
public class PieceUtils {
    public static Piece build(int index, int point, int color) {
        return new Piece(index, PointUtils.build(point), color);
    }
}
