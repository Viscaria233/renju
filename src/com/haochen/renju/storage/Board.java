package com.haochen.renju.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.haochen.renju.control.Mediator;

public class Board implements Mediator.Storage {
    
    private PieceMap map;
    private PieceTree tree;
    private int number;
    
    private Mediator mediator;
    
    public Board() {
        map = new PieceMap();
        tree = new PieceTree();
    }

    public Board(PieceMap map) {
        this.map = map;
        this.tree = new PieceTree();
    }
    
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
    
    public int getNumber() {
        return number;
    }
    
    public boolean available(Point boardLocation) {
        return map.available(boardLocation);
    }

    @Override
    public Cell getCell(Point point) {
        return map.getCell(point);
    }

    @Override
    public void addCell(Cell cell) {
        map.addCell(cell);
        if (cell.isPiece()) {
            ++number;
            tree.addCell(cell);
        }
    }

    @Override
    public void removeCell(Point point) {
        if (map.getCell(point).isPiece()) {
            --number;
        }
        map.removeCell(point);
    }

    @Override
    public void removeCurrentCell() {
        Cell cell = tree.getCurrent().getCell();
        if (cell == null) {
            return;
        }
        --number;
        tree.back();
        map.removeCell(cell.getPoint());
    }

    public Cell getCurrentCell() {
        return tree.getCurrent().getCell();
    }

    /**
     * @return  当前局面下，对方曾经出现过的落子记录
     */
    public List<Cell> getRecords() {
        List<Cell> list = new ArrayList<>();
        List<TreeNode> nodes = tree.getCurrent().getChildren();
        for (TreeNode node : nodes) {
            list.add(node.getCell());
        }
        return list;
    }

    @Override
    public void display() {
        map.display();
        tree.display();
    }

    public void clear() {
        map.clear();
        tree.clear();
        number = 0;
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<Point>() {
            int x = 1;
            int y = 1;

            @Override
            public boolean hasNext() {
                return x <= 15 && y <= 15;
            }

            @Override
            public Point next() {
                Point result = new Point(x, y);
                if (y < 15) {
                    ++y;
                } else {
                    y = 1;
                    ++x;
                }
                return result;
            }

            @Override
            public void remove() {
            }
        };
    }
}
