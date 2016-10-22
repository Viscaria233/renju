package com.haochen.renju.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.haochen.renju.control.Mediator;
import com.haochen.renju.bean.Piece;
import com.haochen.renju.bean.RealPiece;

public class Board {
    
    private PieceMap map;
    private PieceTree tree;
    private int number;
    
    private Mediator mediator;
    
    public Board() {
        map = new PieceMap();
        tree = new PieceTree();
        number = 0;
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
    
    public void addPiece(int index, Point boardLocation, PieceColor color) {
        addPiece(new RealPiece(index, boardLocation, color));
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
        map.removeCell(piece.getLocation());
    }
    
    public void removeForbiddenMark(Point location) {
        map.removeCell(location);
    }
    
    public void clearForbiddenMark() {
        for (int i = 1; i <= 15; ++i) {
            for (int j = 1; j <= 15; ++j) {
                if (map.getCell(i, j).isForbiddenMove()) {
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

    public PieceMap createPieceMap() throws CloneNotSupportedException {
        return map.clone();
    }
    
    public void display() {
        map.display();
        tree.display();
    }
}
