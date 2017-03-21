package com.haochen.renju.storage;

import java.util.List;
import java.util.Stack;

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
    
    public List<TreeNode> getChildren() {
        return current.getChildren();
    }
    
    public int getChildrenNumber() {
        return current.getChildren().size();
    }
    
    public void clearChildren(TreeNode treeNode) {
        treeNode.getChildren().clear();
    }

    public void back() {
        current = current.getParent();
    }
    
//    private TreeNode findNodeInChild(TreeNode treeNode) {
//        ArrayList<TreeNode> list = getChild();
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).equals(treeNode)) {
//                return list.get(i);
//            }
//        }
//        return null;
//    }

    public void addCell(int index, Point point, int color) {
        addCell(new Cell(index, point, color));
    }
    
    public void addCell(Cell cell) {
        TreeNode treeNode = new TreeNode(current, cell);
        if (current.getChildren().contains(treeNode)) {
            current = current.getChildren().get(current.getChildren().indexOf(treeNode));
        } else {
            current.getChildren().add(treeNode);
            current = treeNode;
        }
    }

    public boolean removeCurrentPiece() {
        TreeNode temp = current;
        if (temp.getParent() == null) {
            return false;
        }
        temp.getParent().getChildren().remove(temp);
        return true;
    }
    
    public void display() {
        Stack<Cell> stack = new Stack<>();
        TreeNode temp = current;
        while (temp.getParent() != null) {
            stack.push(temp.getCell());
            temp = temp.getParent();
        }
        while (!stack.isEmpty()) {
            System.out.println(stack.peek().getIndex() + "  " + stack.pop().getPoint());
        }
    }

    public void clear() {
        root = new TreeNode(null, null);
        current = root;
    }
}
