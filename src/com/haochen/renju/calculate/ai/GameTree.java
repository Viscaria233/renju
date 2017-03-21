package com.haochen.renju.calculate.ai;

import com.haochen.renju.util.CellUtils;
import com.haochen.renju.util.PointUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Haochen on 2016/10/24.
 */
public class GameTree implements Iterable<GameTree>, Serializable {
    private GameTree parent;
    private List<GameTree> children = new ArrayList<>();
    private int point;
    private int color;

    public void add(GameTree tree) {
        tree.setParent(this);
        children.add(tree);
    }

    public void addAllChildren(GameTree tree) {
        for (GameTree t : tree.children) {
            t.setParent(this);
            children.add(t);
        }
    }

    public void add(List<Integer> points, int color) {
        GameTree gameTree;
        for (int p : points) {
            gameTree = new GameTree();
            gameTree.setParent(this);
            gameTree.setPoint(p);
            gameTree.setColor(color);
            children.add(gameTree);
        }
    }

    public void remove(GameTree tree) {
        children.remove(tree);
    }

    public void remove(int point) {
        for (GameTree n : children) {
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

    public GameTree getParent() {
        return parent;
    }

    public void setParent(GameTree parent) {
        this.parent = parent;
    }

    public GameTree getChild(int index) {
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
    public Iterator<GameTree> iterator() {
        return new Iterator<GameTree>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < children.size();
            }

            @Override
            public GameTree next() {
                return children.get(index++);
            }

            @Override
            public void remove() {
                children.remove(--index);
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameTree gameTree = (GameTree) o;

        return point == gameTree.point && color == gameTree.color;
    }

    @Override
    public int hashCode() {
        int result = point;
        result = 31 * result + color;
        return result;
    }

    @Override
    public String toString() {
        return "GameTree{" +
                PointUtils.toString(point) +
                CellUtils.toString(color) +
                " children: " + children.size() + '}';
    }
}
