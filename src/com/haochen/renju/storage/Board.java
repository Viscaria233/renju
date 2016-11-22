package com.haochen.renju.storage;

import java.util.ArrayList;
import java.util.List;

import com.haochen.renju.bean.Cell;
import com.haochen.renju.control.Mediator;
import com.haochen.renju.bean.Piece;
import com.haochen.renju.util.PointUtils;

public class Board {
    
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
    
    public void addPiece(int index, Point boardLocation, int color) {
        addPiece(new Piece(index, boardLocation, color));
    }
    
    public void addPiece(Piece piece) {
        ++number;
        map.addCell(piece);
        tree.addPiece(piece);
    }
    
    public void addForbiddenMark(Point location) {
        map.addForbiddenMark(location);
    }
    
    public void removePiece(Point boardLocation) {
        --number;
        map.removeCell(boardLocation);
    }
    
    public void removeCurrentPiece() {
        Piece piece = tree.getCurrent().getPiece();
        if (piece == null) {
            return;
        }
        --number;
        tree.back();
        map.removeCell(piece.getPoint());
    }
    
    public void removeForbiddenMark(Point location) {
        map.removeCell(location);
    }
    
    public void clearForbiddenMark() {
        for (int i = 1; i <= 15; ++i) {
            for (int j = 1; j <= 15; ++j) {
                if (map.getCell(i, j).getType() == Cell.FORBIDDEN) {
                    map.removeCell(i, j);
                }
            }
        }
    }

    public Piece getCurrentPiece() {
        return tree.getCurrent().getPiece();
    }

    /**
     * @return  当前局面下，对方曾经出现过的落子记录
     */
    public List<Piece> getRecords() {
        List<Piece> list = new ArrayList<>();
        List<TreeNode> nodes = tree.getCurrent().getChildren();
        for (TreeNode node : nodes) {
            list.add(node.getPiece());
        }
        return list;
    }

    public BitPieceMap bitPieceMap() {
        BitPieceMap result = new BitPieceMap();
        for (Point p : map) {
            result.addPiece(PointUtils.parse(p), map.getCell(p).getType());
        }
        return result;
    }

    public PieceMap pieceMap() {
        PieceMap result = null;
        try {
            result = map.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public void display() {
        map.display();
        tree.display();
    }
}
