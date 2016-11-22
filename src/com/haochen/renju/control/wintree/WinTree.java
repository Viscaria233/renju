package com.haochen.renju.control.wintree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Haochen on 2016/10/24.
 */
public class WinTree implements Iterable<WinTree>, Serializable {
    private WinTree parent;
    private List<WinTree> children = new ArrayList<>();
    private int point;
    private int color;

    public void add(WinTree tree) {
        tree.setParent(this);
        children.add(tree);
    }

    public void addAllChildren(WinTree tree) {
        for (WinTree t : tree.children) {
            t.setParent(this);
            children.add(t);
        }
    }

    public void add(List<Integer> points, int color) {
        WinTree winTree;
        for (int p : points) {
            winTree = new WinTree();
            winTree.setParent(this);
            winTree.setPoint(p);
            winTree.setColor(color);
            children.add(winTree);
        }
    }

    public void remove(WinTree tree) {
        children.remove(tree);
    }

    public void remove(int point) {
        for (WinTree n : children) {
            if (n.getPoint() != point) {
                children.remove(n);
            }
        }
    }

    public int size() {
        return children.size();
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    public void clearChildren() {
        children.clear();
    }

    public WinTree getParent() {
        return parent;
    }

    public void setParent(WinTree parent) {
        this.parent = parent;
    }

    public WinTree getChild(int index) {
        return children.get(index);
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public Iterator<WinTree> iterator() {
        return new Iterator<WinTree>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < children.size();
            }

            @Override
            public WinTree next() {
                return children.get(index++);
            }

            @Override
            public void remove() {
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WinTree winTree = (WinTree) o;

        if (point != winTree.point) return false;
        if (color != winTree.color) return false;
        return children != null ? children.equals(winTree.children) : winTree.children == null;

    }

    @Override
    public int hashCode() {
        int result = children != null ? children.hashCode() : 0;
        result = 31 * result + point;
        result = 31 * result + color;
        return result;
    }
}
