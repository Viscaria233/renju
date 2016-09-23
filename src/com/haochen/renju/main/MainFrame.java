//package com.haochen.renju.main;
//
//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//import java.com.haochen.renju.util.ArrayList;
//
//import javax.swing.JFrame;
//import javax.swing.JOptionPane;
//
//import com.haochen.renju.ai.Ai;
//import com.haochen.renju.common.OperateStorage;
//import com.haochen.renju.common.Piece;
//import com.haochen.renju.common.RealPiece;
//import com.haochen.renju.form.ChessForm;
//import com.haochen.renju.form.Direction;
//import com.haochen.renju.form.Point;
//import com.haochen.renju.util.ChessTree;
//import com.haochen.renju.util.TreeNode;
//
//public class MainFrame extends JFrame implements OperateStorage {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	public final int boardSize = 420;
//	public final int boardLocationX = 70;
//	public final int boardLocationY = 110;
//	public final Color boardColor = new Color(235, 191, 58);
//	
//	private Ai com.haochen.renju.ai = new Ai();
//	
//	private boolean gameOvered = false;
//
//	public ChessForm chessForm;
//	private boolean usingForbidden;
//
//	public MainFrame(String title) {
//	    super(title);
//		chessForm = new ChessForm();
//		ToolBar bar = new ToolBar(this);
//		
//		this.setSize(600, 600);
//		this.setLocation(550, 50);
//		this.setVisible(true);
//		chessForm.clear();
//		this.setJMenuBar(bar);
//		eventPerform();
//	}
//
//	public boolean isUsingForbidden() {
//		return usingForbidden;
//	}
//
//	public void setUsingForbidden(boolean isUsingForbidden) {
//		this.usingForbidden = isUsingForbidden;
//	}
//
//	@Override
//	public void paint(Graphics g) {
//		drawChessBoard(boardLocationX, boardLocationY);
//		if (chessForm.getChessNumber() > 0) {
//			TreeNode node = chessForm.getChessTree().getCurrent();
//			for (int i = node.getIndex(); i > 0; i--) {
//				if (i % 2 == 1) {
//					drawChess(Piece.BLACK, node.getLocation(), i);
//				} else {
//					drawChess(Piece.WHITE, node.getLocation(), i);
//				}
//				node = node.getParent();
//			}
//		}
//		if (gameOvered == false) {
//			drawAllForbiddenMarks();
//			drawAllRecords();
//		}
//	}
//
//	private int[] chessLocToFrame(Point location) {
//		int[] t = new int[2];
//		t[0] = boardLocationX + (location.x - 1) * 30;
//		t[1] = boardLocationY + boardSize - (location.y - 1) * 30;
//		return t;
//	}
//
//	private void drawChessBoard(int x, int y) {
//		Graphics2D g2d = (Graphics2D) getGraphics();
//
//		g2d.setColor(boardColor);
//		g2d.fillRect(x - 30, y - 30, boardSize + 60, boardSize + 60);
//
//		g2d.setColor(Color.black);
//		g2d.setStroke(new BasicStroke(2.0f));
//		g2d.drawRect(x, y, boardSize, boardSize);
//
//		// 绘制横纵线
//		g2d.setStroke(new BasicStroke(1.0f));
//		for (int i = 1; i <= 14; i++) {
//			g2d.drawLine(x + i * (boardSize / 14), y, x + i * (boardSize / 14), y + boardSize);
//			g2d.drawLine(x, y + i * (boardSize / 14), x + boardSize, y + i * (boardSize / 14));
//		}
//
//		drawStarPoint(new Point(4, 4));
//		drawStarPoint(new Point(4, 12));
//		drawStarPoint(new Point(8, 8));
//		drawStarPoint(new Point(12, 4));
//		drawStarPoint(new Point(12, 12));
//
//		// 绘制坐标
//		String letter[] = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O" };
//
//		for (int i = 0; i < 15; i++) {
//			g2d.drawString(letter[i], x + i * 30 - 4, y + boardSize + 28);
//			g2d.drawString(i + 1 + "", x - 28, y + boardSize - i * 30 + 4);
//		}
//	}
//
//	private void drawStarPoint(Point location) {
//		Graphics g = getGraphics();
//		g.setColor(Color.black);
//		int[] p = chessLocToFrame(location);
//		g.fillOval(p[0] - 3, p[1] - 3, 7, 7);
//	}
//
//	private void drawForbiddenMark(Point location) {
//		Graphics2D g2d = (Graphics2D) getGraphics();
//		g2d.setColor(Color.red);
//		g2d.setStroke(new BasicStroke(3.0f));
//
//		int[] p = chessLocToFrame(location);
//		g2d.drawLine(p[0] - 6, p[1] - 6, p[0] + 6, p[1] + 6);
//		g2d.drawLine(p[0] - 6, p[1] + 6, p[0] + 6, p[1] - 6);
//	}
//
//	private void drawAllForbiddenMarks() {
//		ArrayList<Point> bannedPoint = chessForm.getForbiddenPoint();
//		// System.out.println("Banned Point : " + bannedPoint.size());
//		for (int i = 0; i < bannedPoint.size(); i++) {
//			drawForbiddenMark(bannedPoint.get(i));
//		}
//	}
//
//	private void deleteAllForbiddenMarks() {
//		ArrayList<Point> bannedPoint = chessForm.getForbiddenPoint();
//		for (int i = 0; i < bannedPoint.size(); i++) {
//			fixChessBoard(bannedPoint.get(i));
//		}
//	}
//
//	public void updateForbiddenMarks() {
//		deleteAllForbiddenMarks();
//		chessForm.updateForbiddenPoints();
//		drawAllForbiddenMarks();
//		drawAllRecords();
//	}
//	
//	private void eventPerform() {
//		this.addWindowListener(new WindowAdapter() {
//			public void windowClosing(WindowEvent e) {
//				System.exit(0);
//			}
//		});
//
//		this.addMouseListener(new MouseListener() {
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				int modify = e.getModifiers();
//
//				int leftTopX = e.getX() - boardLocationX + 15;
//				int leftTopY = e.getY() - boardLocationY + 15;
//				Point location = new Point();
//
//				if ((leftTopX >= 0 && leftTopX <= 449) && (leftTopY >= 0 && leftTopY <= 449)) {
//					location.setLocation(leftTopX / 30 + 1, 16 - (leftTopY / 30 + 1));
//				}
//
//				if (location.x > 0 && location.y > 0) {
//					if (modify == MouseEvent.BUTTON1_MASK) {
//						if (addChess(location) == false && gameOvered) {
//							JOptionPane.showMessageDialog(null, "Game is overed.");
//						}
//					} else if (modify == MouseEvent.BUTTON2_MASK) {
//						System.out.println(chessForm.isForbiddenPoint(location, Direction.all));
////						if (chessForm.getChess(location) == null) {
////							System.out.println("null");
////						}
//					} else if (modify == MouseEvent.BUTTON3_MASK) {
//						removeLastChess();
//					}
//				}
//			}
//
//			@Override
//			public void mousePressed(MouseEvent e) {
//			}
//
//			@Override
//			public void mouseExited(MouseEvent e) {
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent e) {
//			}
//
//			@Override
//			public void mouseClicked(MouseEvent e) {
//			}
//		});
//	}
//
//	@Override
//	public boolean addChess(Point location) {
//		if (gameOvered) {
//			return false;
//		}
//		if (chessForm.isFull()) {
//			return false;
//		}
//		if (chessForm.getChess(location) != null) {
//			return false;
//		}
//		deleteAllForbiddenMarks();
//		deleteAllRecords();
//		Piece chess = chessForm.getLastChess();
//		if (chess != null) {
//			drawChess(chess.getColor(), chess.getLocation(), chess.getIndex());
//		}
//		int winner = chessForm.findWinner(
//				new RealPiece(chessForm.getChessNumber() + 1, location));
//		
//		chessForm.addChess(location);
//		chess = chessForm.getLastChess();
//		drawChess(chess.getColor(), location, chess.getIndex());
//		drawHighlight(chess);
//		if (winner == 0) {
//			drawAllForbiddenMarks();
//			drawAllRecords();
//		} else if (winner == Piece.BLACK) {
//			gameOvered = true;
//			JOptionPane.showMessageDialog(null, "Black Win");
//		} else {
//			gameOvered = true;
//			JOptionPane.showMessageDialog(null, "White Win");
//		}
//		
//		return true;
//	}
//
//	private void drawChess(int color, Point location, int index) {
//		Graphics g = getGraphics();
//		int[] p = chessLocToFrame(location);
//
//		if (color == Piece.BLACK) {
//			g.setColor(Color.black);
//		} else {
//			g.setColor(Color.white);
//		}
//		g.fillOval(p[0] - 14, p[1] - 14, 28, 28);
//		drawIndex(location, index);
//	}
//
//	private void drawHighlight(Piece chess) {
//		Graphics g = getGraphics();
//		int[] p = chessLocToFrame(chess.getLocation());
//		int index = chess.getIndex();
//		g.setColor(new Color(1f, 1f, 0f, 0.7f));
//		if (index < 10) {
//			g.fillRect(p[0] - 5, p[1] - 8, 10, 16);
//		} else if (index < 100) {
//			g.fillRect(p[0] - 8, p[1] - 8, 16, 16);
//		} else {
//			g.fillRect(p[0] - 11, p[1] - 8, 22, 16);
//		}
//		drawIndex(chess.getLocation(), index);
//	}
//
//	private void drawIndex(Point location, int index) {
//		Graphics g = getGraphics();
//		int[] p = chessLocToFrame(location);
//
//		if (index % 2 == 1) {
//			g.setColor(Color.white);
//		} else {
//			g.setColor(Color.black);
//		}
//
//		if (index < 10) {
//			p[0] -= 4;
//		} else if (index < 100) {
//			p[0] -= 7;
//		} else {
//			p[0] -= 10;
//		}
//
//		g.drawString(index + "", p[0], p[1] + 4);
//	}
//
//	private void drawAllRecords() {
//		ChessTree tree = chessForm.getChessTree();
//		TreeNode node = tree.getCurrent();
//		ArrayList<TreeNode> list = node.getChildren();
//		for (int i = 0; i < list.size(); i++) {
//			drawRecord(list.get(i).getLocation());
//		}
//	}
//
//	private void deleteAllRecords() {
//		ChessTree tree = chessForm.getChessTree();
//		TreeNode node = tree.getCurrent();
//		ArrayList<TreeNode> list = node.getChildren();
//		for (int i = 0; i < list.size(); i++) {
//			fixChessBoard(list.get(i).getLocation());
//		}
//	}
//
//	private void fixChessBoard(Point location) {
//		Graphics2D g2d = (Graphics2D) getGraphics();
//		int indexX = location.x;
//		int indexY = location.y;
//		int leftTopX = (indexX - 1) * 30 + boardLocationX - 14;
//		int leftTopY = (15 - indexY) * 30 + boardLocationY - 14;
//
//		g2d.setColor(boardColor);
//		g2d.fillRect(leftTopX, leftTopY, 28, 28);
//
//		g2d.setColor(Color.black);
//		g2d.setStroke(new BasicStroke(2.0f));
//
//		if (indexX == 1 && indexY == 1) {
//			g2d.drawLine(leftTopX + 14, leftTopY + 14, leftTopX + 28, leftTopY + 14);
//			g2d.drawLine(leftTopX + 14, leftTopY + 0, leftTopX + 14, leftTopY + 14);
//		} else if (indexX == 15 && indexY == 15) {
//			g2d.drawLine(leftTopX, leftTopY + 14, leftTopX + 14, leftTopY + 14);
//			g2d.drawLine(leftTopX + 14, leftTopY + 14, leftTopX + 14, leftTopY + 28);
//		} else if (indexX == 1 && indexY == 15) {
//			g2d.drawLine(leftTopX + 14, leftTopY + 14, leftTopX + 28, leftTopY + 14);
//			g2d.drawLine(leftTopX + 14, leftTopY + 14, leftTopX + 14, leftTopY + 28);
//		} else if (indexX == 15 && indexY == 1) {
//			g2d.drawLine(leftTopX, leftTopY + 14, leftTopX + 14, leftTopY + 14);
//			g2d.drawLine(leftTopX + 14, leftTopY, leftTopX + 14, leftTopY + 14);
//		} else if (indexX == 1) {
//			g2d.drawLine(leftTopX + 14, leftTopY, leftTopX + 14, leftTopY + 28);
//			g2d.setStroke(new BasicStroke(1.0f));
//			g2d.drawLine(leftTopX + 14, leftTopY + 14, leftTopX + 28, leftTopY + 14);
//		} else if (indexX == 15) {
//			g2d.drawLine(leftTopX + 14, leftTopY, leftTopX + 14, leftTopY + 28);
//			g2d.setStroke(new BasicStroke(1.0f));
//			g2d.drawLine(leftTopX, leftTopY + 14, leftTopX + 14, leftTopY + 14);
//		} else if (indexY == 1) {
//			g2d.drawLine(leftTopX, leftTopY + 14, leftTopX + 28, leftTopY + 14);
//			g2d.setStroke(new BasicStroke(1.0f));
//			g2d.drawLine(leftTopX + 14, leftTopY, leftTopX + 14, leftTopY + 14);
//		} else if (indexY == 15) {
//			g2d.drawLine(leftTopX, leftTopY + 14, leftTopX + 28, leftTopY + 14);
//			g2d.setStroke(new BasicStroke(1.0f));
//			g2d.drawLine(leftTopX + 14, leftTopY + 14, leftTopX + 14, leftTopY + 28);
//		} else {
//			g2d.setStroke(new BasicStroke(1.0f));
//			g2d.drawLine(leftTopX, leftTopY + 14, leftTopX + 28, leftTopY + 14);
//			g2d.drawLine(leftTopX + 14, leftTopY, leftTopX + 14, leftTopY + 28);
//		}
//
//		if ((indexX == 8 && indexY == 8) || (indexX == 4 && indexY == 4) || (indexX == 12 && indexY == 12)
//				|| (indexX == 4 && indexY == 12) || (indexX == 12 && indexY == 4)) {
//			drawStarPoint(location);
//		}
//	}
//
//	@Override
//	public boolean removeLastChess() {
//		if (chessForm.getChessNumber() == 0) {
//			return false;
//		}
//		gameOvered = false;
//		deleteAllForbiddenMarks();
//		deleteAllRecords();
//		deleteLastChess();
//		chessForm.removeLastChess();
//		Piece chess = chessForm.getLastChess();
//		if (chess != null) {
//			drawHighlight(chess);
//		}
//		drawAllForbiddenMarks();
//		drawAllRecords();
//		return true;
//	}
//
//	private void deleteLastChess() {
//		Point location = chessForm.getLastChess().getLocation();
//		fixChessBoard(location);
//	}
//
//	private void drawRecord(Point location) {
//		int r = 5;
//		Graphics2D g2d = (Graphics2D) getGraphics();
//		Piece chess = chessForm.getLastChess();
//		if (chess == null) {
//			g2d.setColor(Color.black);
//		} else if (chess.getColor() == Piece.BLACK) {
//			g2d.setColor(Color.white);
//		} else {
//			g2d.setColor(Color.black);
//		}
//		int[] p = chessLocToFrame(location);
//		g2d.fillOval(p[0] - r, p[1] - r, 2 * r, 2 * r);
//	}
//}
