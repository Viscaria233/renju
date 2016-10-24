package com.haochen.renju.control.wintree;

import com.haochen.renju.storage.PieceColor;
import com.haochen.renju.storage.Point;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Haochen on 2016/10/24.
 */
public class WinTree implements Iterable<WinTree> {
    private WinTree parent;
    private List<WinTree> children = new ArrayList<>();
    private Point point;
    private PieceColor color;

    public void add(WinTree tree) {
        children.addAll(tree.children);
    }

    public void add(List<Point> points, PieceColor color) {
        WinTree winTree;
        for (Point p : points) {
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

    public void remove(Point point) {
        for (WinTree n : children) {
            if (n.getPoint().equals(point)) {
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

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public PieceColor getColor() {
        return color;
    }

    public void setColor(PieceColor color) {
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
}
