package com.haochen.renju.calculate;

import com.haochen.renju.storage.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Haochen on 2016/11/8.
 */
public class Resources {
    private Map<Integer, List<Res>> black = new HashMap<>();
    private Map<Integer, List<Res>> white = new HashMap<>();

    public Map<Integer, List<Res>> of(int color) {
        switch (color) {
            case Cell.BLACK:
                return black;
            case Cell.WHITE:
                return white;
        }
        return null;
    }

    public void add(int point, int color, Res res) {
        of(color).get(point).add(res);
    }

    public void remove(int point, int color, Res res) {
        of(color).get(point).remove(res);
    }

    public void remove(int point, int color, int index) {
        of(color).get(point).remove(index);
    }

    public int size(int color) {
        int result = 0;
        for (Map.Entry<Integer, List<Res>> entry : of(color).entrySet()) {
            result += entry.getValue().size();
        }
        return result;
    }
}
