package com.haochen.renju.storage;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
	private TreeNode parent;
	private List<TreeNode> children;
	private Cell cell;
	
	TreeNode(TreeNode parent, Cell cell) {
		this.parent = parent;
		children = new ArrayList<>();
		this.cell = cell;
	}

	public TreeNode getParent() {
		return parent;
	}

	public List<TreeNode> getChildren() {
		return children;
	}

    public Cell getCell() {
        return cell;
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
		if (cell == null) {
			if (other.cell != null)
				return false;
		} else if (!cell.equals(other.cell))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = parent != null ? parent.hashCode() : 0;
		result = 31 * result + (children != null ? children.hashCode() : 0);
		result = 31 * result + (cell != null ? cell.hashCode() : 0);
		return result;
	}
}
