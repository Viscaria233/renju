//package com.haochen.renju.util;
//
//import java.com.haochen.renju.ui.draw.ArrayList;
//
//import com.haochen.renju.bean.GetChessAttribute;
//import com.haochen.renju.bean.OperateStorage;
//import com.haochen.renju.storage.Point;
//
//
//public class ChessTree implements OperateStorage, GetChessAttribute {
//	private TreeNode root;
//	private TreeNode current;
//	
//	public ChessTree() {
//		root = new TreeNode(null, null);
//		current = root;
//	}
//	
//	public TreeNode getRoot() {
//		return root;
//	}
//	
//	public TreeNode getCurrent() {
//		return current;
//	}
//	
//	public TreeNode getFather() {
//		return current.getParent();
//	}
//	
//	public ArrayList<TreeNode> getChild() {
//		return current.getChild();
//	}
//	
//	@Override
//	public Point getLocation() {
//		return current.getLocation();
//	}
//	
//	public int getChildNumber() {
//		return current.getChild().size();
//	}
//	
//	@Override
//	public int getIndex() {
////		TreeNode temp = current;
////		int index;
////		for (index = 0; temp.getFather() != null; index++) {
////			temp = temp.getFather();
////		}
////		return index;
//		return current.getIndex();
//	}
//	
//	@Override
//	public int getColor() {
//		return current.getColor();
//	}
//	
//	public ChessMap createChessMap() {
//		TreeNode treeNode = current;
//		ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();
//		while (treeNode.getParent() != null) {
//			nodes.add(treeNode);
//			treeNode = treeNode.getParent();
//		}
//		
//		ChessMap chessMap = new ChessMap();
//		while (nodes.size() > 0) {
//			Point chessLocation = nodes.get(nodes.size() - 1).getLocation();
//			chessMap.addChess(chessLocation);
//			nodes.remove(nodes.size() - 1);
//		}
//		return chessMap;
//	}
//	
//	
//	
//	public void clearChild(TreeNode treeNode) {
//		for (int i = 0; i < treeNode.getChild().size() - 1; i++) {
//			//子节点中有父节点的引用，必须从叶子节点开始递归删除，否则内存泄漏
//			clearChild(treeNode.getChild().get(i));
//		}
//		treeNode.getChild().clear();
//	}
//	
//	public void back() {
//		current = current.getParent();
//	}
//	
//	private TreeNode findNodeInChild(TreeNode treeNode) {
//		ArrayList<TreeNode> list = getChild();
//		for (int i = 0; i < list.size(); i++) {
//			if (list.get(i).equals(treeNode)) {
//				return list.get(i);
//			}
//		}
//		return null;
//	}
//
//	@Override
//	public boolean addChess(Point location) {
//		TreeNode treeNode = new TreeNode(current, location);
//		if (current.getChild().contains(treeNode)) {
//			current = findNodeInChild(treeNode);
//			return false;
//		}
//		current.getChild().add(treeNode);
//		current = treeNode;
//		return true;
//	}
//
//	@Override
//	public boolean removeLastChess() {
//		TreeNode temp = current;
//		if (temp.getParent() == null) {
//			return false;
//		}
//		current = current.getParent();
//		current.getChild().remove(temp);
//		clearChild(temp);
//		return true;
//	}
//}
