package com.haochen.renju.control.ai;

import java.awt.Color;

import com.haochen.renju.bean.Cell;
import com.haochen.renju.control.Mediator;
import com.haochen.renju.bean.Piece;
import com.haochen.renju.calculate.ContinueAttribute;
import com.haochen.renju.storage.Direction;
import com.haochen.renju.storage.Point;
import com.haochen.renju.calculate.SingleContinue;
import com.haochen.renju.storage.PieceMap;

public class AI {

    private boolean forbiddenMove = true;
    private Mediator mediator;
    
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
    
    public Color findWinner(PieceMap map, Piece piece) {
        Point location = piece.getLocation();
        Color color = piece.getColor();
        if (color.equals(Color.black)) {
            if (forbiddenMove && map.getCell(location).isForbiddenMove()) {
                return Color.white;
            }
        }
        
        Color winner = null;
        boolean isImaginary = false;
        //插入假想棋子
        if (map.available(location)) {
            map.addPiece(-1, location, color);
            isImaginary = true;
        }
        
        if (findFive(map, color, location, Direction.all).getQuantity() > 0) {
            winner = color;
        }
        
        //删除假想棋子
        if (isImaginary) {
            map.removeCell(location);
        }
        return winner;
    }
    
    public ContinueAttribute getContinueAttribute(PieceMap map, Color color, Point location, Direction direction) {
        //坐标不合法，或坐标处没有棋子
        if (!location.isValid() || map.available(location)) {
            return null;
        }
        
        ContinueAttribute attribute = new ContinueAttribute(color, location, Direction.empty);
        
        Direction[] directionArray = Direction.createDirectionArray();
        if (direction.isSingle() == false) {
            for (int i = 0; i < directionArray.length; i++) {
                if (direction.contains(directionArray[i])) {
                    attribute.append(getContinueAttribute(map, color, location, directionArray[i]));
                }
            }
            return attribute;
        }
        
        boolean[] isValid = new boolean[2];
        for (int i = 0; i < isValid.length; i++) {
            isValid[i] = true;
        }
        Point[] backup = new Point[2];
        for (int i = 0; i < backup.length; i++) {
            backup[i] = new Point();
        }
        Point[] temp = new Point[2];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = new Point();
        }
        Point[] continueEnd = new Point[2];
        Point[] breakPoint = new Point[2];
        
        for (int i = 1; i < 15; i++) {
            if (isValid[0] == false && isValid[1] == false) {
                break;
            }
            if (isValid[0]) {
                if (direction == Direction.horizontal) {
                    backup[0].setLocation(location.x - (i - 1), location.y);
                    temp[0].setLocation(location.x - i, location.y);
                } else if (direction == Direction.vertical) {
                    backup[0].setLocation(location.x, location.y - (i - 1));
                    temp[0].setLocation(location.x, location.y - i);
                } else if (direction == Direction.mainDiagonal) {
                    backup[0].setLocation(location.x - (i - 1), location.y + (i - 1));
                    temp[0].setLocation(location.x - i, location.y + i);
                } else if (direction == Direction.counterDiagonal) {
                    backup[0].setLocation(location.x - (i - 1), location.y - (i - 1));
                    temp[0].setLocation(location.x - i, location.y - i);
                }
                isValid[0] = temp[0].isValid();
            }
            if (isValid[1]) {
                if (direction == Direction.horizontal) {
                    backup[1].setLocation(location.x + (i - 1), location.y);
                    temp[1].setLocation(location.x + i, location.y);
                } else if (direction == Direction.vertical) {
                    backup[1].setLocation(location.x, location.y + (i - 1));
                    temp[1].setLocation(location.x, location.y + i);
                } else if (direction == Direction.mainDiagonal) {
                    backup[1].setLocation(location.x + (i - 1), location.y - (i - 1));
                    temp[1].setLocation(location.x + i, location.y - i);
                } else if (direction == Direction.counterDiagonal) {
                    backup[1].setLocation(location.x + (i - 1), location.y + (i - 1));
                    temp[1].setLocation(location.x + i, location.y + i);
                }
                isValid[1] = temp[1].isValid();
            }
            for (int j = 0; j < isValid.length; j++) {
                if (isValid[j]) {
                    Cell cell = map.getCell(temp[j]);
                    if (!cell.isPiece() || !cell.getColor().equals(color)) {
                        continueEnd[j] = backup[j];
                        breakPoint[j] = temp[j];
                        isValid[j] = false;
                    }
                } else {
                    continueEnd[j] = backup[j];
                    breakPoint[j] = temp[j];
                }
                if (!temp[j].isValid()) {
                    breakPoint[j] = null;
                }
            }
            SingleContinue single = new SingleContinue();
            single.setDirection(direction);
            single.setContinueEnd(continueEnd);
            single.setBreakPoint(breakPoint);
            single.generateLength();
            attribute.setContinue(single);
        }
        return attribute;
    }

    public Direction findFive(PieceMap map, Color color, Point location, Direction direction) {
        Direction fiveDirection = new Direction();
        if (!location.isValid() || map.available(location) || !map.getCell(location).getColor().equals(color)) {
            return fiveDirection;
        }
        
        Direction[] directionArray = Direction.createDirectionArray();
        if (direction.isSingle() == false) {
            for (int i = 0; i < directionArray.length; i++) {
                if (direction.contains(directionArray[i])) {
                    fiveDirection.append(findFive(map, color, location, directionArray[i]));
                }
            }
            return fiveDirection;
        }
        
        SingleContinue single = getContinueAttribute(map, color, location, direction).getContinue(direction);
        if (forbiddenMove) {
            if (color.equals(Color.black)) {
                if (single.getLength() == 5) {
                    fiveDirection.add(direction);
                }
            } else {
                if (single.getLength() >= 5) {
                    fiveDirection.add(direction);
                }
            }
        } else {
            if (single.getLength() >= 5) {
                fiveDirection.add(direction);
            } 
        }
        return fiveDirection;
    }

    public Direction findAliveFour(PieceMap map, Color color, Point location, Direction direction) {
        Direction fourDirection = new Direction();
        if (!location.isValid() || map.available(location) || !map.getCell(location).getColor().equals(color)) {
            return fourDirection;
        }
        
        Direction[] directionArray = Direction.createDirectionArray();
        if (direction.isSingle() == false) {
            for (int i = 0; i < directionArray.length; i++) {
                if (direction.contains(directionArray[i])) {
                    fourDirection.append(findAliveFour(map, color, location, directionArray[i]));
                }
            }
            return fourDirection;
        }
        
        SingleContinue single = getContinueAttribute(map, color, location, direction).getContinue(direction);
        if (single.getLength() != 4) {
            return fourDirection;
        }
        Point[] breakPoint = single.getBreakPoint();
        int fivePoint = 0;
        for (int i = 0; i < breakPoint.length; i++) {
            if (breakPoint[i] != null) {
                boolean isImaginary = false;
                if (map.available(breakPoint[i])) {
                    //插入假想棋子
                    map.addPiece(-1, breakPoint[i], color);
                    isImaginary = true;
                }

                if (findFive(map, color, breakPoint[i], direction).getQuantity() > 0) {
                    fivePoint++;
                }

                //删除假想棋子
                if (isImaginary) {
                    map.removeCell(breakPoint[i]);
                }
            }
        }
        if (fivePoint == 2) {
            fourDirection.add(direction);
        }
        return fourDirection;
    }

    public Direction findAsleepFour(PieceMap map, Color color, Point location, Direction direction) {
        Direction fourDirection = new Direction();
        if (!location.isValid() || map.available(location) || !map.getCell(location).getColor().equals(color)) {
            return fourDirection;
        }
        
        Direction[] directionArray = Direction.createDirectionArray();
        if (direction.isSingle() == false) {
            for (int i = 0; i < directionArray.length; i++) {
                if (direction.contains(directionArray[i])) {
                    fourDirection.append(findAsleepFour(map, color, location, directionArray[i]));
                }
            }
            return fourDirection;
        }
        
        SingleContinue single = getContinueAttribute(map, color, location, direction).getContinue(direction);
        if (single.getLength() > 4) {
            return fourDirection;
        }
        Point[] breakPoint = single.getBreakPoint();
        int fivePoint = 0;
        for (int i = 0; i < breakPoint.length; i++) {
            if (breakPoint[i] != null) {
                boolean isImaginary = false;
                if (map.available(breakPoint[i])) {
                    //插入假想棋子
                    map.addPiece(-1, breakPoint[i], color);
                    isImaginary = true;
                }

                if (findFive(map, color, breakPoint[i], direction).getQuantity() > 0) {
                    fivePoint++;
                }

                //删除假想棋子
                if (isImaginary) {
                    map.removeCell(breakPoint[i]);
                }
            }
        }
        if (fivePoint == 1) {
            fourDirection.add(direction);
        } else if (fivePoint == 2 && single.getLength() != 4) {
            fourDirection.doubleAdd(direction);
        }
        return fourDirection;
    }
    
    public Direction findAliveThree(PieceMap map, Color color, Point location, Direction direction) {
        Direction threeDirection = new Direction();
        if (!location.isValid() || map.available(location) || !map.getCell(location).getColor().equals(color)) {
            return threeDirection;
        }
        
        Direction[] directionArray = Direction.createDirectionArray();
        if (direction.isSingle() == false) {
            for (int i = 0; i < directionArray.length; i++) {
                if (direction.contains(directionArray[i])) {
                    threeDirection.append(findAliveThree(map, color, location, directionArray[i]));
                }
            }
            return threeDirection;
        }
        
        SingleContinue single = getContinueAttribute(map, color, location, direction).getContinue(direction);
        if (single.getLength() > 3) {
            return threeDirection;
        }
        Point[] breakPoint = single.getBreakPoint();
        int aliveFourPoint = 0;
        for (int i = 0; i < breakPoint.length; i++) {
            if (breakPoint[i] != null) {
                //先判断此处是不是禁手
                if (map.getCell(breakPoint[i]).isForbiddenMove()) {
                    continue;
                }
                boolean isImaginary = false;
                if (map.available(breakPoint[i])) {
                    //插入假想棋子
                    map.addPiece(-1, breakPoint[i], color);
                    isImaginary = true;
                }

                if (findAliveFour(map, color, breakPoint[i], direction).getQuantity() > 0) {
                    aliveFourPoint++;
                }

                //删除假想棋子
                if (isImaginary) {
                    map.removeCell(breakPoint[i]);
                }
            }
        }
        if (aliveFourPoint > 0) {
            threeDirection.add(direction);
        }
        return threeDirection;
    }
    
    public Direction findAsleepThree(PieceMap map, Color color, Point location, Direction direction) {
        Direction threeDirection = new Direction();
        if (!location.isValid() || map.available(location) || !map.getCell(location).getColor().equals(color)) {
            return threeDirection;
        }
        
        Direction[] directionArray = Direction.createDirectionArray();
        if (direction.isSingle() == false) {
            for (int i = 0; i < directionArray.length; i++) {
                if (direction.contains(directionArray[i])) {
                    threeDirection.append(findAsleepThree(map, color, location, directionArray[i]));
                }
            }
            return threeDirection;
        }
        
        SingleContinue single = getContinueAttribute(map, color, location, direction).getContinue(direction);
        if (single.getLength() > 3) {
            return threeDirection;
        }
        Point[] breakPoint = single.getBreakPoint();
        int asleepFourPoint = 0;
        for (int i = 0; i < breakPoint.length; i++) {
            if (breakPoint[i] != null) {
                //先判断此处是不是禁手
                if (map.getCell(breakPoint[i]).isForbiddenMove()) {
                    continue;
                }
                boolean isImaginary = false;
                if (map.available(breakPoint[i])) {
                    //插入假想棋子
                    map.addPiece(-1, breakPoint[i], color);
                    isImaginary = true;
                }

                if (findAliveFour(map, color, breakPoint[i], direction).getQuantity() > 0) {
                    asleepFourPoint++;
                }

                //删除假想棋子
                if (isImaginary) {
                    map.removeCell(breakPoint[i]);
                }
            }
        }
        if (asleepFourPoint > 0) {
            threeDirection.add(direction);
        }
        return threeDirection;
    }

    public Direction findLongContinue(PieceMap map, Point location, Direction direction) {
        Direction longContinueDirection = new Direction();
        if (!location.isValid() || map.available(location)) {
            return longContinueDirection;
        }
        
        Direction[] directionArray = Direction.createDirectionArray();
        if (direction.isSingle() == false) {
            for (int i = 0; i < directionArray.length; i++) {
                if (direction.contains(directionArray[i])) {
                    longContinueDirection.append(findLongContinue(map, location, directionArray[i]));
                }
            }
            return longContinueDirection;
        }
        
        SingleContinue single = getContinueAttribute(map, Color.black, location, direction).getContinue(direction);
        if (single.getLength() > 5) {
            longContinueDirection.add(direction);
        }
        return longContinueDirection;
    }

    public boolean isDoubleFour(PieceMap map, Point location) {
        boolean isImaginary = false;
        //插入假想棋子
        if (map.available(location)) {
            map.addPiece(-1, location, Color.black);
            isImaginary = true;
        }
        
        int four = findAliveFour(map, Color.black, location, Direction.all).getQuantity()
                + findAsleepFour(map, Color.black, location, Direction.all).getQuantity();

        //删除假想棋子
        if(isImaginary) {
            map.removeCell(location);
        }
        return four >= 2;
    }
    
    public boolean isDoubleThree(PieceMap map, Point location) {
        boolean isImaginary = false;
        //插入假想棋子
        if (map.available(location)) {
            map.addPiece(-1, location, Color.black);
            isImaginary = true;
        }

        int three = findAliveThree(map, Color.black, location, Direction.all).getQuantity();
        
        //删除假想棋子
        if(isImaginary) {
            map.removeCell(location);
        }
        return three >= 2;
    }
    
    public boolean isHandCut(PieceMap map, Point location, Direction direction) {
        boolean handCut = false;
        boolean isImaginary = false;
        Color color = Color.black;
        //插入假想棋子
        if (map.available(location)) {
            map.addPiece(-1, location, color);
            isImaginary = true;
        }
        
        boolean isChanged = false;
        if (findFive(map, color, location, direction).getQuantity() > 0) {
            handCut = false;
            isChanged = true;
        }
        if (isChanged == false) {
            if (findAliveFour(map, color, location, direction).getQuantity()
                    + findAsleepFour(map, color, location, direction).getQuantity() >= 2
                    || findLongContinue(map, location, direction).getQuantity() > 0) {
                handCut = true;
                isChanged = true;
            }
        }
        if (isChanged == false) {
            Direction aliveThree = findAliveThree(map, color, location, direction);
            if (aliveThree.getQuantity() >= 2) {
                ContinueAttribute attribute = getContinueAttribute(map, color, location, aliveThree);
                Direction[] directionArray = Direction.createDirectionArray();
                int valid = 0;
                for (int i = 0; i < directionArray.length; i++) {
                    if (aliveThree.contains(directionArray[i])) {
                        SingleContinue single = attribute.getContinue(directionArray[i]);
                        Point[] breakPoint = single.getBreakPoint();
                        Point[] aliveFourPoint = new Point[2];
                        for (int j = 0; j < breakPoint.length; j++) {
                            if (breakPoint[j] != null) {
                                boolean isImaginary1 = false;
                                //插入假想棋子
                                if (map.available(breakPoint[j])) {
                                    map.addPiece(-1, breakPoint[j], color);
                                    isImaginary1 = true;
                                }
                                if (findAliveFour(map, color, breakPoint[j], directionArray[i]).getQuantity() > 0) {
                                    aliveFourPoint[j] = breakPoint[j];
                                }
                                //删除假想棋子
                                if (isImaginary1) {
                                    map.removeCell(breakPoint[j]);
                                }
                            }
                        }
                        for (int j = 0; j < aliveFourPoint.length; j++) {
                            if (aliveFourPoint[j] != null) {
                                boolean isImaginary1 = false;
                                //插入假想棋子
                                if (map.available(aliveFourPoint[j])) {
                                    map.addPiece(-1, aliveFourPoint[j], color);
                                    isImaginary1 = true;
                                }
                                if (findFive(map, color, aliveFourPoint[j], direction.remove(directionArray[i])).getQuantity() > 0) {
                                    //删除假想棋子
                                    if (isImaginary1) {
                                        map.removeCell(aliveFourPoint[j]);
                                    }
                                    continue;
                                }
                                if (isHandCut(map, aliveFourPoint[j], Direction.all)) {
                                    //删除假想棋子
                                    if (isImaginary1) {
                                        map.removeCell(aliveFourPoint[j]);
                                    }
                                    continue;
                                }
                                //删除假想棋子
                                if (isImaginary1) {
                                    map.removeCell(aliveFourPoint[j]);
                                }
                                valid++;
                                break;
                            }
                        }
                    }
                }
                if (valid >= 2) {
                    handCut = true;
                    isChanged = true;
                }
            }
        }
        //删除假想棋子
        if (isImaginary) {
            map.removeCell(location);
        }
        return handCut;
    }
    
    public Point getRandomMove(PieceMap map, Color color) {
        Point point = new Point((int) (Math.random() * 16), (int) (Math.random() * 16));
        while (!map.available(point)) {
            point = new Point((int) (Math.random() * 16), (int) (Math.random() * 16));
        }
        return point;
    }
    
    public Point getCloseMove(PieceMap map, Color color, Piece lastPiece) {
        if (lastPiece == null) {
            return new Point(8, 8);
        }
        int x = lastPiece.getLocation().x;
        int y = lastPiece.getLocation().y;
        Point point;
        do {
            point = new Point((int) (Math.random() * 3) - 1 + x, (int) (Math.random() * 3) - 1 + y);
        } while (!map.available(point));
        return point;
    }
    
    public Point getMove(PieceMap map, Color color) {
        Point point = null;
        return point;
    }
    
}




//class ChessForm implements GetChessObject, OperateStorage{
//    private ChessTree chessTree;
//    private ChessMap chessMap;
//    private ArrayList<Point> forbiddenPoints;
//    private boolean usingForbidden;
//
//    public ChessForm() {
//        chessTree = new ChessTree();
//        chessMap = new ChessMap();
//        forbiddenPoints = new ArrayList<Point>();
//    }
//
//    public boolean isUsingForbidden() {
//        return usingForbidden;
//    }
//
//    public void setUsingForbidden(boolean isUsingForbidden) {
//        this.usingForbidden = isUsingForbidden;
//    }
//
//    public boolean isFull() {
//        if (getChessNumber() == 225) {
//            return true;
//        }
//        return false;
//    }
//
//    public boolean isEmpty() {
//        if (getChessNumber() == 0) {
//            return true;
//        }
//        return false;
//    }
//
//    public int findWinner(Piece piece) {
//        Point location = piece.getLocation();
//        int winner = 0;
//        boolean isImaginary = false;
//        if (map.getPiece(location) == null) {
//            chessMap.imaginaryChess(piece.getColor(), location);
//            isImaginary = true;
//        }
//            
//        if (piece.getColor() == Piece.BLACK) {
//            if (usingForbidden && forbiddenPoints.contains(location)) {
//                winner = Piece.WHITE;
//            } else if (findFive(Piece.BLACK, location, Direction.all).getQuantity() > 0) {
//                winner = Piece.BLACK;
//            }
//        } else {
//            if (findFive(Piece.WHITE, location, Direction.all).getQuantity() > 0) {
//                winner = Piece.WHITE;
//            }
//        }
//        if (isImaginary) {
//            chessMap.removeImaginaryChess(location);
//        }
//        return winner;
//    }
//
//    public Direction findLongContinue(Point location, Direction direction) {
//        Direction longContinueDirection = new Direction();
//        if (getChess(location) == null) {
//            return longContinueDirection;
//        }
//        
//        Direction[] directionArray = Direction.createDirectionArray();
//        if (direction.isSingle() == false) {
//            for (int i = 0; i < directionArray.length; i++) {
//                if (direction.contain(directionArray[i])) {
//                    longContinueDirection.append(findLongContinue(location, directionArray[i]));
//                }
//            }
//            return longContinueDirection;
//        }
//        
//        SingleContinue single = getContinueAttribute(Piece.BLACK, location, direction).getContinue(direction);
//        if (single.getLength() > 5) {
//            longContinueDirection.add(direction);
//        }
//        return longContinueDirection;
//    }
//
//    public boolean isDoubleFour(Point location) {
//        boolean isImaginary = false;
//        if (map.getPiece(location) == null) {
//            chessMap.imaginaryChess(Piece.BLACK, location);
//            isImaginary = true;
//        }
//        
//        int four = findAliveFour(Piece.BLACK, location, Direction.all).getQuantity()
//                + findAsleepFour(Piece.BLACK, location, Direction.all).getQuantity();
//
//        if(isImaginary) {
//            chessMap.removeImaginaryChess(location);
//        }
//        return four >= 2;
//    }
//    
//    public boolean isDoubleThree(Point location) {
//        boolean isImaginary = false;
//        
//        if (getChess(location) == null) {
//            chessMap.imaginaryChess(Piece.BLACK, location);
//            isImaginary = true;
//        }
//
//        int three = findAliveThree(Piece.BLACK, location, Direction.all).getQuantity();
//        if (isImaginary) {
//            chessMap.removeImaginaryChess(location);
//        }
//        return three >= 2;
//    }
//    
//    public boolean isBannedPoint(Location location) {
//        if (getChess(location) != null) {
//            return false;
//        }
//        boolean isBannedPoint = false;
//        boolean isImaginary = false;
//        if (map.getPiece(location) == null) {
//            chessMap.imaginaryChess(Piece.BLACK, location);
//            isImaginary = true;
//        }
//
//        if (findFive(Piece.BLACK, location, Direction.all).getQuantity() > 0) {
//            isBannedPoint = false;
//        } else if (findLongContinue(location, Direction.all).getQuantity() > 0) {
//            isBannedPoint = true;
//        } else if (isDoubleFour(location)) {
//            isBannedPoint = true;
//        } else if (isDoubleThree(location)) {
//            isBannedPoint = true;
//        }
//        
//        if (isImaginary) {
//            chessMap.removeImaginaryChess(location);
//        }
//        return isBannedPoint;
//    }
//    
//    public boolean isForbiddenPoint(Point location, Direction direction) {
////      if (getChess(location) != null) {
////          return false;
////      }
//        
//        boolean isBannedPoint = false;
//        boolean isImaginary = false;
//        if (map.getPiece(location) == null) {
//            chessMap.imaginaryChess(Piece.BLACK, location);
//            isImaginary = true;
//        }
//        
////      ContinueAttribute attribute = getContinueAttribute(Piece.BLACK, location, Direction.all);
////      Direction[] directionArray = Direction.createDirectionArray();
//        
//        
//        boolean isChanged = false;
//        if (findFive(Piece.BLACK, location, direction).getQuantity() > 0) {
//            isBannedPoint = false;
//            isChanged = true;
//        }
//        if (isChanged == false) {
//            if (findAliveFour(Piece.BLACK, location, direction).getQuantity()
//                    + findAsleepFour(Piece.BLACK, location, direction).getQuantity() >= 2
//                    || findLongContinue(location, direction).getQuantity() > 0) {
//                isBannedPoint = true;
//                isChanged = true;
//            }
//        }
//        if (isChanged == false) {
//            Direction aliveThree = findAliveThree(Piece.BLACK, location, direction);
//            if (aliveThree.getQuantity() >= 2) {
//                ContinueAttribute attribute = getContinueAttribute(Piece.BLACK, location, aliveThree);
//                Direction[] directionArray = Direction.createDirectionArray();
//                int valid = 0;
//                for (int i = 0; i < directionArray.length; i++) {
//                    if (aliveThree.contain(directionArray[i])) {
//                        SingleContinue single = attribute.getContinue(directionArray[i]);
//                        Point[] breakPoint = single.getBreakPoint();
//                        Point[] aliveFourPoint = new Point[2];
//                        for (int j = 0; j < breakPoint.length; j++) {
//                            if (breakPoint[j] != null) {
//                                boolean isImaginary1 = false;
//                                if (map.getPiece(breakPoint[j]) == null) {
//                                    chessMap.imaginaryChess(Piece.BLACK, breakPoint[j]);
//                                    isImaginary1 = true;
//                                }
//                                if (findAliveFour(Piece.BLACK, breakPoint[j], directionArray[i]).getQuantity() > 0) {
//                                    aliveFourPoint[j] = breakPoint[j];
//                                }
//                                if (isImaginary1) {
//                                    chessMap.removeImaginaryChess(breakPoint[j]);
//                                }
//                            }
//                        }
//                        for (int j = 0; j < aliveFourPoint.length; j++) {
//                            if (aliveFourPoint[j] != null) {
//                                boolean isImaginary1 = false;
//                                if (map.getPiece(aliveFourPoint[j]) == null) {
//                                    chessMap.imaginaryChess(Piece.BLACK, aliveFourPoint[j]);
//                                    isImaginary1 = true;
//                                }
//                                if (findFive(Piece.BLACK, aliveFourPoint[j], direction.remove(directionArray[i])).getQuantity() > 0) {
//                                    if (isImaginary1) {
//                                        chessMap.removeImaginaryChess(aliveFourPoint[j]);
//                                    }
//                                    continue;
//                                }
//                                if (isForbiddenPoint(aliveFourPoint[j], Direction.all)) {
//                                    if (isImaginary1) {
//                                        chessMap.removeImaginaryChess(aliveFourPoint[j]);
//                                    }
//                                    continue;
//                                }
//                                if (isImaginary1) {
//                                    chessMap.removeImaginaryChess(aliveFourPoint[j]);
//                                }
//                                valid++;
//                                break;
//                            }
//                        }
//                    }
//                }
//                if (valid >= 2) {
//                    isBannedPoint = true;
//                    isChanged = true;
//                }
//            }
//        }
//        
//        if (isImaginary) {
//            chessMap.removeImaginaryChess(location);
//        }
//        return isBannedPoint;
//    }
//
//    public Direction findFive(int color, Point location, Direction direction) {
//        Direction fiveDirection = new Direction();
//        if (color == 0) {
//            return fiveDirection;
//        }
//        if (getChess(location) == null) {
//            return fiveDirection;
//        }
//        if (getChess(location).getColor() != color) {
//            return fiveDirection;
//        }
//        
//        Direction[] directionArray = Direction.createDirectionArray();
//        if (direction.isSingle() == false) {
//            for (int i = 0; i < directionArray.length; i++) {
//                if (direction.contain(directionArray[i])) {
//                    fiveDirection.append(findFive(color, location, directionArray[i]));
//                }
//            }
//            return fiveDirection;
//        }
//        
//        SingleContinue single = getContinueAttribute(color, location, direction).getContinue(direction);
//        if (usingForbidden) {
//            if (color == Piece.BLACK) {
//                if (single.getLength() == 5) {
//                    fiveDirection.add(direction);
//                }
//            } else {
//                if (single.getLength() >= 5) {
//                    fiveDirection.add(direction);
//                }
//            }
//        } else {
//            if (single.getLength() >= 5) {
//                fiveDirection.add(direction);
//            } 
//        }
//        return fiveDirection;
//    }
//
//    public Direction findAliveFour(int color, Point location, Direction direction) {
//        Direction fourDirection = new Direction();
//        if (getChess(location) == null) {
//            return fourDirection;
//        }
//        if (getChess(location).getColor() != color) {
//            return fourDirection;
//        }
//        
//        Direction[] directionArray = Direction.createDirectionArray();
//        if (direction.isSingle() == false) {
//            for (int i = 0; i < directionArray.length; i++) {
//                if (direction.contain(directionArray[i])) {
//                    fourDirection.append(findAliveFour(color, location, directionArray[i]));
//                }
//            }
//            return fourDirection;
//        }
//        
//        SingleContinue single = getContinueAttribute(color, location, direction).getContinue(direction);
//        if (single.getLength() != 4) {
//            return fourDirection;
//        }
//        Point[] breakPoint = single.getBreakPoint();
//        int fivePoint = 0;
//        for (int i = 0; i < breakPoint.length; i++) {
//            if (breakPoint[i] != null) {
//                boolean isImaginary = false;
//                if (map.getPiece(breakPoint[i]) == null) {
//                    chessMap.imaginaryChess(color, breakPoint[i]);
//                    isImaginary = true;
//                }
//
//                if (findFive(color, breakPoint[i], direction).getQuantity() > 0) {
//                    fivePoint++;
//                }
//
//                if (isImaginary) {
//                    chessMap.removeImaginaryChess(breakPoint[i]);
//                }
//            }
//        }
//        if (fivePoint == 2) {
//            fourDirection.add(direction);
//        }
//        return fourDirection;
//    }
//
//    public ContinueAttribute getContinueAttribute(int color, Point location, Direction direction) {
//        if (getChess(location) == null) {
//            return null;
//        }
//        ContinueAttribute attribute = new ContinueAttribute(color, location, Direction.empty);
//        
//        Direction[] directionArray = Direction.createDirectionArray();
//        if (direction.isSingle() == false) {
//            for (int i = 0; i < directionArray.length; i++) {
//                if (direction.contain(directionArray[i])) {
//                    attribute.append(getContinueAttribute(color, location, directionArray[i]));
//                }
//            }
//            return attribute;
//        }
//        
//        boolean[] isValid = new boolean[2];
//        for (int i = 0; i < isValid.length; i++) {
//            isValid[i] = true;
//        }
//        Point[] backup = new Point[2];
//        for (int i = 0; i < backup.length; i++) {
//            backup[i] = new Point();
//        }
//        Point[] temp = new Point[2];
//        for (int i = 0; i < temp.length; i++) {
//            temp[i] = new Point();
//        }
//        Point[] continueEnd = new Point[2];
//        Point[] breakPoint = new Point[2];
//        
//        for (int i = 1; i < 15; i++) {
//            if (isValid[0] == false && isValid[1] == false) {
//                break;
//            }
//            if (isValid[0]) {
//                if (direction == Direction.horizontal) {
//                    backup[0].setLocation(location.x - (i - 1), location.y);
//                    temp[0].setLocation(location.x - i, location.y);
//                } else if (direction == Direction.vertical) {
//                    backup[0].setLocation(location.x, location.y - (i - 1));
//                    temp[0].setLocation(location.x, location.y - i);
//                } else if (direction == Direction.mainDiagonal) {
//                    backup[0].setLocation(location.x - (i - 1), location.y + (i - 1));
//                    temp[0].setLocation(location.x - i, location.y + i);
//                } else if (direction == Direction.counterDiagonal) {
//                    backup[0].setLocation(location.x - (i - 1), location.y - (i - 1));
//                    temp[0].setLocation(location.x - i, location.y - i);
//                }
//                isValid[0] = temp[0].isValid();
//            }
//            if (isValid[1]) {
//                if (direction == Direction.horizontal) {
//                    backup[1].setLocation(location.x + (i - 1), location.y);
//                    temp[1].setLocation(location.x + i, location.y);
//                } else if (direction == Direction.vertical) {
//                    backup[1].setLocation(location.x, location.y + (i - 1));
//                    temp[1].setLocation(location.x, location.y + i);
//                } else if (direction == Direction.mainDiagonal) {
//                    backup[1].setLocation(location.x + (i - 1), location.y - (i - 1));
//                    temp[1].setLocation(location.x + i, location.y - i);
//                } else if (direction == Direction.counterDiagonal) {
//                    backup[1].setLocation(location.x + (i - 1), location.y + (i - 1));
//                    temp[1].setLocation(location.x + i, location.y + i);
//                }
//                isValid[1] = temp[1].isValid();
//            }
//            for (int j = 0; j < isValid.length; j++) {
//                if (isValid[j]) {
//                    Piece chess = map.getPiece(temp[j]);
//                    if (chess == null || chess.getColor() != color) {
//                        continueEnd[j] = backup[j];
//                        breakPoint[j] = temp[j];
//                        isValid[j] = false;
//                    }
//                } else {
//                    continueEnd[j] = backup[j];
//                    breakPoint[j] = temp[j];
//                }
//                if (temp[j].isValid() == false) {
//                    breakPoint[j] = null;
//                }
//            }
//            SingleContinue single = new SingleContinue();
//            single.setDirection(direction);
//            single.setContinueEnd(continueEnd);
//            single.setBreakPoint(breakPoint);
//            single.generateLength();
//            attribute.setContinue(single);
//        }
//        return attribute;
//    }
//    
//    public Direction findAsleepFour(int color, Point location, Direction direction) {
//        Direction fourDirection = new Direction();
//        if (getChess(location) == null) {
//            return fourDirection;
//        }
//        if (getChess(location).getColor() != color) {
//            return fourDirection;
//        }
//        
//        Direction[] directionArray = Direction.createDirectionArray();
//        if (direction.isSingle() == false) {
//            for (int i = 0; i < directionArray.length; i++) {
//                if (direction.contain(directionArray[i])) {
//                    fourDirection.append(findAsleepFour(color, location, directionArray[i]));
//                }
//            }
//            return fourDirection;
//        }
//        
//        SingleContinue single = getContinueAttribute(color, location, direction).getContinue(direction);
//        if (single.getLength() > 4) {
//            return fourDirection;
//        }
//        Point[] breakPoint = single.getBreakPoint();
//        int fivePoint = 0;
//        for (int i = 0; i < breakPoint.length; i++) {
//            if (breakPoint[i] != null) {
//                boolean isImaginary = false;
//                if (map.getPiece(breakPoint[i]) == null) {
//                    chessMap.imaginaryChess(color, breakPoint[i]);
//                    isImaginary = true;
//                }
//
//                if (findFive(color, breakPoint[i], direction).getQuantity() > 0) {
//                    fivePoint++;
//                }
//
//                if (isImaginary) {
//                    chessMap.removeImaginaryChess(breakPoint[i]);
//                }
//            }
//        }
//        if (fivePoint == 1) {
//            fourDirection.add(direction);
//        } else if (fivePoint == 2 && single.getLength() != 4) {
//            fourDirection.doubleAdd(direction);
//        }
//        return fourDirection;
//    }
//
//    public int getFourQuantity(int color, Point location, Direction direction) {
//        return findAliveFour(color, location, direction).getQuantity()
//                + findAsleepFour(color, location, direction).getQuantity();
//    }
//
//    public Direction findAliveThree(int color, Point location, Direction direction) {
//        Direction threeDirection = new Direction();
//        if (getChess(location) == null) {
//            return threeDirection;
//        }
//        if (getChess(location).getColor() != color) {
//            return threeDirection;
//        }
//        
//        Direction[] directionArray = Direction.createDirectionArray();
//        if (direction.isSingle() == false) {
//            for (int i = 0; i < directionArray.length; i++) {
//                if (direction.contain(directionArray[i])) {
//                    threeDirection.append(findAliveThree(color, location, directionArray[i]));
//                }
//            }
//            return threeDirection;
//        }
//        
//        SingleContinue single = getContinueAttribute(color, location, direction).getContinue(direction);
//        if (single.getLength() > 3) {
//            return threeDirection;
//        }
//        Point[] breakPoint = single.getBreakPoint();
//        int aliveFourPoint = 0;
//        for (int i = 0; i < breakPoint.length; i++) {
//            if (breakPoint[i] != null) {
//                boolean isImaginary = false;
//                if (map.getPiece(breakPoint[i]) == null) {
//                    chessMap.imaginaryChess(color, breakPoint[i]);
//                    isImaginary = true;
//                }
//
//                if (findAliveFour(color, breakPoint[i], direction).getQuantity() > 0) {
//                    aliveFourPoint++;
//                }
//
//                if (isImaginary) {
//                    chessMap.removeImaginaryChess(breakPoint[i]);
//                }
//            }
//        }
//        if (aliveFourPoint > 0) {
//            threeDirection.add(direction);
//        }
//        return threeDirection;
//    }
//
//    public Direction findAsleepThree(int color, Point location, Direction direction) {
//        Direction threeDirection = new Direction();
//        return threeDirection;
//    }
//
//    public ChessMap getChessMap() {
//        return chessMap;
//    }
//    
//    public ChessTree getChessTree() {
//        return chessTree;
//    }
//    
//    public ArrayList<Point> getForbiddenPoint() {
//        return forbiddenPoints;
//    }
//
//    @Override
//    public Piece getChess(Point location) {
//        return map.getPiece(location);
//    }
//    
//    @Override
//    public Piece getLastChess() {
//        return getChess(getChessNumber());
//    }
//    
//    @Override
//    public Piece getChess(int index) {
//        if (index < 1) {
//            return null;
//        }
//        int t = getChessNumber() - index;
//        if (t < 0) {
//            return null;
//        }
//        
//        TreeNode treeNode = chessTree.getCurrent();
//        for (int i = 0; i < t; i++) {
//            treeNode = treeNode.getParent();
//        }
//        Point location = treeNode.getLocation();
//        return getChess(location);
//    }
//    
//    @Override
//    public int getChessNumber() {
//        return map.getPieceNumber();
//    }
//
//    public void clear() {
//        chessTree.clearChild(chessTree.getRoot());
//        chessMap.clear();
//    }
//    
//    public void removeForbiddenPoint(Point location) {
//        if (forbiddenPoints.contains(location)) {
//            forbiddenPoints.remove(location);
//        }
//    }
//    
//    private void recordForbiddenPoint() {
//        if (usingForbidden == false) {
//            return;
//        }
////      bannedPoint.clear();
//        for (int i= 1; i <= 15; i++) {
//            for (int j = 1; j <= 15; j++) {
//                Point location = new Point(i, j);
////              System.out.print("i = " + i + " / " + location);
//                if (chessMap.isAvailable(location) == false) {
////                  System.out.println("  continue");
//                    continue;
//                }
//                if (isForbiddenPoint(location, Direction.all)) {
////                  System.out.print("  add");
//                    forbiddenPoints.add(location);
//                }
////              System.out.println();
//            }
//        }
//    }
//    
//    public void updateForbiddenPoints() {
//        forbiddenPoints.clear();
//        recordForbiddenPoint();
//    }
//    
//    @Override
//    public boolean addChess(Point location) {
//        if (chessMap.isAvailable(location) == false) {
//            return false;
//        }
//        chessTree.addChess(location);
//        chessMap.addChess(location);
//        updateForbiddenPoints();
//        return true;
//    }
//    
//    @Override
//    public boolean removeLastChess() {
//        if (getChessNumber() == 0) {
//            return false;
//        }
//        chessTree.back();
//        chessMap.removeLastChess();
//        updateForbiddenPoints();
//        return true;
//    }
//}