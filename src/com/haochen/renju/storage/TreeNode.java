package com.haochen.renju.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.haochen.renju.bean.Piece;

public class TreeNode {
	private TreeNode parent;
	private List<TreeNode> children;
	private Piece piece;
	
	TreeNode(TreeNode parent, Piece piece) {
		this.parent = parent;
		children = new ArrayList<TreeNode>();
		this.piece = piece;
	}

	public TreeNode getParent() {
		return parent;
	}

	public List<TreeNode> getChildren() {
		return children;
	}

    public Piece getPiece() {
        return piece;
    }
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeNode other = (TreeNode) obj;
		if (piece == null) {
			if (other.piece != null)
				return false;
		} else if (!piece.equals(other.piece))
			return false;
		return true;
	}
}
