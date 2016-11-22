package com.haochen.renju.calculate;

import com.haochen.renju.bean.Res;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haochen on 2016/11/8.
 */
public class Resources {
    private List<Res> resources = new ArrayList<>();

    public Resources() {}

    public void add(Res res) {
        resources.add(res);
    }

    public void remove(Res res) {
        resources.remove(res);
    }

    public void remove(int index) {
        resources.remove(index);
    }

    public int size() {
        return resources.size();
    }

    public Resources search(int direction) {
        Resources result = new Resources();
        for (Res res : resources) {
            if (res.getDirection() == direction) {
                result.add(res);
            }
        }
        return result;
    }
}
