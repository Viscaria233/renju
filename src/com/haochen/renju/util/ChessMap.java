//package com.haochen.renju.util;
//
//import com.haochen.renju.form.Point;
//
//import java.awt.Color;
//
//import com.haochen.renju.common.GetChessObject;
//import com.haochen.renju.common.ImaginaryPiece;
//import com.haochen.renju.common.OperateStorage;
//import com.haochen.renju.common.Piece;
//import com.haochen.renju.common.RealPiece;
//
//public class ChessMap implements GetChessObject, OperateStorage {
//    private Piece[][] chessMap;
//    private int chessNumber;
//    
//    public ChessMap() {
//        chessMap = new Piece[16][16];
//        chessNumber = 0;
//    }
//    
//    public boolean isAvailable(Point boardLocation) {
//    	return chessMap[boardLocation.x][boardLocation.y] == null;
//    }
//    
//    @Override
//    public Piece getChess(Point boardLocation) {
//    	if (boardLocation.isValid() == false) {
//    		return null;
//    	}
//    	if (chessMap[boardLocation.x][boardLocation.y] == null) {
//    		return null;
//    	}
//        return chessMap[boardLocation.x][boardLocation.y];
//    }
//    
//    @Override
//    public Piece getChess(int index) {
//    	if (index < 1 || index > 225) {
//    		return null;
//    	}
//    	Point boardLocation = new Point();
//    	for (int i = 1; i <= 15; i++) {
//    		boolean isFound = false;
//    		if (isFound) {
//    			break;
//    		}
//    		for (int j = 1; j <= 15; j++) {
//    			if (chessMap[i][j] == null) {
//    				continue;
//    			}
//    			if (chessMap[i][j].getIndex() == index) {
//    				boardLocation = chessMap[i][j].getLocation();
//    				isFound = true;
//    				break;
//    			}
//    		}
//    	}
//    	return getChess(boardLocation);
//    }
//    
//	@Override
//	public Piece getLastChess() {
//		return getChess(chessNumber);
//	}
//	
//    @Override
//    public int getChessNumber() {
//        return chessNumber;
//    }
//    
//    @Override
//    public boolean addChess(Point boardLocation) {
//    	if (boardLocation.isValid() == false) {
//    		return false;
//    	}
//    	if (chessMap[boardLocation.x][boardLocation.y] != null) {
//    		return false;
//    	}
//    	
//    	chessNumber++;
//    	chessMap[boardLocation.x][boardLocation.y] = new RealPiece(chessNumber, boardLocation, Color.red);
//        return true;
//    }
//    
//    @Override
//    public boolean removeLastChess() throws NullPointerException {
//    	if (chessNumber == 0) {
//    		return false;
//    	}
//    	Piece lastChess = getChess(chessNumber);
//    	Point boardLocation = lastChess.getLocation();
//        chessMap[boardLocation.x][boardLocation.y] = null;
//        chessNumber--;
//        return true;
//    }
//    
//    public void imaginaryChess(int color,  Point boardLocation) {
//    	if (color == Piece.BLACK) {
//    		chessMap[boardLocation.x][boardLocation.y] = new ImaginaryPiece(1, boardLocation, Color.black);
//    	} else {
//    		chessMap[boardLocation.x][boardLocation.y] = new ImaginaryPiece(2, boardLocation, Color.white);
//    	}
//    }
//    
//    public void removeImaginaryChess(Point boardLocation) {
//    	chessMap[boardLocation.x][boardLocation.y] = null;
//    }
//    
//    public void clear() {
//    	for (int i = 1; i <= 15; i++) {
//    		for (int j = 1; j <= 15; j++) {
//    			chessMap[i][j] = null;
//    		}
//    	}
//    	chessNumber = 0;
//    }
//
//}
