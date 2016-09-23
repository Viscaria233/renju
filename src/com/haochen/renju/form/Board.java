package com.haochen.renju.form;

import java.awt.Color;

import com.haochen.renju.common.Controller;
import com.haochen.renju.common.Piece;
import com.haochen.renju.common.RealPiece;
import com.haochen.renju.util.PieceMap;
import com.haochen.renju.util.PieceTree;

public class Board {
    
    private PieceMap map;
    private PieceTree tree;
    private int number;
    
    private Controller controller;
    
    public Board() {
        map = new PieceMap();
        tree = new PieceTree();
        number = 0;
    }
    
    public void setController(Controller controller) {
        this.controller = controller;
    }
    
    public int getNumber() {
        return number;
    }
    
    public boolean avaliable(Point boardLocation) {
        return map.available(boardLocation);
    }
    
    public void addPiece(int index, Point boardLocation, Color color) {
        addPiece(new RealPiece(index, boardLocation, color));
    }
    
    public void addPiece(Piece piece) {
        ++number;
        map.addCell(piece);
        tree.addPiece(piece);
    }
    
    public void addHandCut(Point location) {
        map.addHandCut(location);
    }
    
    public void removePiece(Point boardLocation) {
        --number;
        map.removeCell(boardLocation);
    }
    
    public void removeLastPiece() {
        Piece piece = tree.getCurrent().getPiece();
        if (piece == null) {
            return;
        }
        --number;
        tree.back();
        map.removeCell(piece.getLocation());
    }
    
    public void removeHandCut(Point location) {
        map.removeCell(location);
    }
    
    public void clearHandCut() {
        for (int i = 1; i <= 15; ++i) {
            for (int j = 1; j <= 15; ++j) {
                if (map.getCell(i, j).isHandCut()) {
                    map.removeCell(i, j);
                }
            }
        }
    }
    
    public Piece getLastPiece() {
        return tree.getCurrent().getPiece();
    }
    
    public PieceMap createPieceMap() throws CloneNotSupportedException {
        return map.clone();
    }
    
    public void display() {
        map.display();
        tree.display();
    }
}
