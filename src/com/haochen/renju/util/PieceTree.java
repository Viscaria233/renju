package com.haochen.renju.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Stack;

import com.haochen.renju.common.Piece;
import com.haochen.renju.common.RealPiece;
import com.haochen.renju.form.Point;

public class PieceTree {
    private TreeNode root;
    private TreeNode current;
    
    public PieceTree() {
        root = new TreeNode(null, null);
        current = root;
    }
    
    public TreeNode getRoot() {
        return root;
    }
    
    public TreeNode getCurrent() {
        return current;
    }
    
    public TreeNode getParent() {
        return current.getParent();
    }
    
    public ArrayList<TreeNode> getChildren() {
        return current.getChildren();
    }
    
    public int getChildrenNumber() {
        return current.getChildren().size();
    }
    
//    public ChessMap createChessMap() {
//        TreeNode treeNode = current;
//        ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();
//        while (treeNode.getParent() != null) {
//            nodes.add(treeNode);
//            treeNode = treeNode.getParent();
//        }
//        
//        ChessMap chessMap = new ChessMap();
//        while (nodes.size() > 0) {
//            Point chessLocation = nodes.get(nodes.size() - 1).getPiece().getLocation();
//            chessMap.addChess(chessLocation);
//            nodes.remove(nodes.size() - 1);
//        }
//        return chessMap;
//    }
    
    public void clearChildren(TreeNode treeNode) {
        treeNode.getChildren().clear();
    }
    
    public void back() {
        current = current.getParent();
    }
    
//    private TreeNode findNodeInChild(TreeNode treeNode) {
//        ArrayList<TreeNode> list = getChildren();
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).equals(treeNode)) {
//                return list.get(i);
//            }
//        }
//        return null;
//    }

    public void addPiece(int index, Point boardLocation, Color color) {
        addPiece(new RealPiece(index, boardLocation, color));
    }
    
    public void addPiece(Piece piece) {
        TreeNode treeNode = new TreeNode(current, piece);
        if (current.getChildren().contains(treeNode)) {
            current = current.getChildren().get(current.getChildren().indexOf(treeNode));
        } else {
            current.getChildren().add(treeNode);
            current = treeNode;
        }
    }

    public boolean removeLastPiece() {
        TreeNode temp = current;
        if (temp.getParent() == null) {
            return false;
        }
        temp.getParent().getChildren().remove(temp);
        return true;
    }
    
    public void display() {
        Stack<Piece> stack = new Stack<Piece>();
        TreeNode temp = current;
        while (temp.getParent() != null) {
            stack.push(temp.getPiece());
            temp = temp.getParent();
        }
        while (!stack.isEmpty()) {
            System.out.println(stack.peek().getIndex() + "  " + stack.pop().getLocation());
        }
    }
}
