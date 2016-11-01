package com.haochen.renju.control.ai;

import com.haochen.renju.bean.Cell;
import com.haochen.renju.bean.Piece;
import com.haochen.renju.bean.RealPiece;
import com.haochen.renju.calculate.ContinueAttribute;
import com.haochen.renju.calculate.ContinueType;
import com.haochen.renju.calculate.SingleContinue;
import com.haochen.renju.control.Mediator;
import com.haochen.renju.control.ai.WinTreeFinder.MoveSetGetter;
import com.haochen.renju.control.ai.WinTreeFinder.WinMethod;
import com.haochen.renju.control.wintree.WinTree;
import com.haochen.renju.storage.Direction;
import com.haochen.renju.storage.PieceColor;
import com.haochen.renju.storage.PieceMap;
import com.haochen.renju.storage.Point;

import java.util.*;

public class AI {

    public static boolean usingForbiddenMove = true;
    private Mediator mediator;

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    public PieceColor findWinner(PieceMap map, Piece piece) {
        Point location = piece.getLocation();
        PieceColor color = piece.getColor();
        if (color.equals(PieceColor.BLACK)) {
            if (usingForbiddenMove && map.getCell(location).isForbiddenMove()) {
                return PieceColor.WHITE;
            }
        }

        PieceColor winner = null;
        boolean isImaginary = false;
        //插入假想棋子
        if (map.available(location)) {
            map.addPiece(-1, location, color);
            isImaginary = true;
        }

        ContinueAttribute attribute = getContinueAttribute(map, color, location, Direction.all);
        if (findFive(attribute, Direction.all).getQuantity() > 0) {
            winner = color;
        }

        //删除假想棋子
        if (isImaginary) {
            map.removeCell(location);
        }
        return winner;
    }

    public ContinueAttribute getContinueAttribute(PieceMap map, PieceColor color, Point location, Direction direction) {
        //坐标不合法，或坐标处没有棋子
        if (!location.isValid() || map.available(location)) {
            return null;
        }
        if (!map.getCell(location).getColor().equals(color)) {
            return new ContinueAttribute(color, location, direction);
        }

        ContinueAttribute attribute = new ContinueAttribute(color, location, Direction.empty);

        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    attribute.append(getContinueAttribute(map, color, location, d));
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
            if (!isValid[0] && !isValid[1]) {
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

    public Direction findFive(ContinueAttribute attribute, Direction direction) {
        Direction result = new Direction();
        if (attribute == null) {
            return result;
        }

        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    result.append(findFive(attribute, d));
                }
            }
            return result;
        }

        SingleContinue single = attribute.getContinue(direction);
        if (usingForbiddenMove) {
            if (attribute.getColor().equals(PieceColor.BLACK)) {
                if (single.getLength() == 5) {
                    result.add(direction);
                }
            } else {
                if (single.getLength() >= 5) {
                    result.add(direction);
                }
            }
        } else {
            if (single.getLength() >= 5) {
                result.add(direction);
            }
        }
        return result;
    }

    public Direction findAliveFour(PieceMap map, ContinueAttribute attribute, Direction direction) {
        Direction result = new Direction();
        if (attribute == null) {
            return result;
        }

        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    result.append(findAliveFour(map, attribute, d));
                }
            }
            return result;
        }

        PieceColor color = attribute.getColor();
        SingleContinue single = attribute.getContinue(direction);
        if (single.getLength() != 4) {
            return result;
        }
        Point[] breakPoint = single.getBreakPoint();
        int fivePoint = 0;
        for (Point p : breakPoint) {
            if (p != null) {
                boolean isImaginary = false;
                if (map.available(p)) {
                    //插入假想棋子
                    map.addPiece(-1, p, color);
                    isImaginary = true;
                }

                ContinueAttribute breakAttr = getContinueAttribute(map, color, p, direction);
                if (findFive(breakAttr, direction).getQuantity() > 0) {
                    fivePoint++;
                }

                //删除假想棋子
                if (isImaginary) {
                    map.removeCell(p);
                }
            }
        }
        if (fivePoint == 2) {
            result.add(direction);
        }
        return result;
    }

    public Direction findAsleepFour(PieceMap map, ContinueAttribute attribute, Direction direction) {
        Direction result = new Direction();
        if (attribute == null) {
            return result;
        }

        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    result.append(findAsleepFour(map, attribute, d));
                }
            }
            return result;
        }

        PieceColor color = attribute.getColor();
        SingleContinue single = attribute.getContinue(direction);
        if (single.getLength() > 4) {
            return result;
        }
        Point[] breakPoint = single.getBreakPoint();
        int fivePoint = 0;
        for (Point p : breakPoint) {
            if (p != null) {
                boolean isImaginary = false;
                if (map.available(p)) {
                    //插入假想棋子
                    map.addPiece(-1, p, color);
                    isImaginary = true;
                }

                ContinueAttribute breakAttr = getContinueAttribute(map, color, p, direction);
                if (findFive(breakAttr, direction).getQuantity() > 0) {
                    fivePoint++;
                }

                //删除假想棋子
                if (isImaginary) {
                    map.removeCell(p);
                }
            }
        }
        if (fivePoint == 1) {
            result.add(direction);
        } else if (fivePoint == 2 && single.getLength() != 4) {
            result.doubleAdd(direction);
        }
        return result;
    }

    public Direction findAliveThree(PieceMap map, ContinueAttribute attribute, Direction direction) {
        Direction result = new Direction();
        if (attribute == null) {
            return result;
        }

        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    result.append(findAliveThree(map, attribute, d));
                }
            }
            return result;
        }

        PieceColor color = attribute.getColor();
        SingleContinue single = attribute.getContinue(direction);
        if (single.getLength() > 3) {
            return result;
        }
        Point[] breakPoint = single.getBreakPoint();
        int aliveFourPoint = 0;
        for (Point p : breakPoint) {
            if (p != null) {
                //先判断此处是不是禁手
                if (map.getCell(p).isForbiddenMove()) {
                    continue;
                }
                boolean isImaginary = false;
                if (map.available(p)) {
                    //插入假想棋子
                    map.addPiece(-1, p, color);
                    isImaginary = true;
                }

                ContinueAttribute breakAttr = getContinueAttribute(map, color, p, direction);
                if (findAliveFour(map, breakAttr, direction).getQuantity() > 0) {
                    aliveFourPoint++;
                }

                //删除假想棋子
                if (isImaginary) {
                    map.removeCell(p);
                }
            }
        }
        if (aliveFourPoint > 0) {
            result.add(direction);
        }
        return result;
    }

    public Direction findAsleepThree(PieceMap map, ContinueAttribute attribute, Direction direction) {
        Direction result = new Direction();
        if (attribute == null) {
            return result;
        }

        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    result.append(findAsleepThree(map, attribute, d));
                }
            }
            return result;
        }

        PieceColor color = attribute.getColor();
        SingleContinue single = attribute.getContinue(direction);
        if (single.getLength() > 3) {
            return result;
        }
        Point[] breakPoint = single.getBreakPoint();
        int asleepFourPoint = 0;
        for (Point p : breakPoint) {
            if (p != null) {
                //先判断此处是不是禁手
                if (map.getCell(p).isForbiddenMove()) {
                    continue;
                }
                boolean isImaginary = false;
                if (map.available(p)) {
                    //插入假想棋子
                    map.addPiece(-1, p, color);
                    isImaginary = true;
                }

                ContinueAttribute breakAttr = getContinueAttribute(map, color, p, direction);
                if (findAsleepFour(map, breakAttr, direction).getQuantity() > 0) {
                    asleepFourPoint++;
                }
                if (findAliveFour(map, breakAttr, direction).getQuantity() > 0) {
                    asleepFourPoint = 0;
                }

                //删除假想棋子
                if (isImaginary) {
                    map.removeCell(p);
                }
            }
        }
        if (asleepFourPoint > 0) {
            result.add(direction);
        }
        return result;
    }


    public Direction findLongContinue(ContinueAttribute attribute, Direction direction) {
        Direction result = new Direction();

        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    result.append(findLongContinue(attribute, d));
                }
            }
            return result;
        }

        SingleContinue single = attribute.getContinue(direction);
        if (single.getLength() > 5) {
            result.add(direction);
        }
        return result;
    }


//    public Direction findLongContinue(PieceMap map, Point location, Direction direction) {
//        Direction result = new Direction();
//        if (!location.isValid() || map.available(location)) {
//            return result;
//        }
//
//        Direction[] directionArray = Direction.createDirectionArray();
//        if (!direction.isSingle()) {
//            for (Direction d : directionArray) {
//                if (direction.contains(d)) {
//                    result.append(findLongContinue(map, location, d));
//                }
//            }
//            return result;
//        }
//
//        SingleContinue single = getContinueAttribute(map, PieceColor.BLACK, location, direction).getContinue(direction);
//        if (single.getLength() > 5) {
//            result.addAllChildren(direction);
//        }
//        return result;
//    }

    public boolean isDoubleFour(PieceMap map, Point location) {
        boolean isImaginary = false;
        //插入假想棋子
        if (map.available(location)) {
            map.addPiece(-1, location, PieceColor.BLACK);
            isImaginary = true;
        }

        ContinueAttribute attribute = getContinueAttribute(map, PieceColor.BLACK, location, Direction.all);
        int four = findAliveFour(map, attribute, Direction.all).getQuantity()
                + findAsleepFour(map, attribute, Direction.all).getQuantity();

        //删除假想棋子
        if (isImaginary) {
            map.removeCell(location);
        }
        return four >= 2;
    }

    public boolean isDoubleThree(PieceMap map, Point location) {
        boolean isImaginary = false;
        //插入假想棋子
        if (map.available(location)) {
            map.addPiece(-1, location, PieceColor.BLACK);
            isImaginary = true;
        }

        ContinueAttribute attribute = getContinueAttribute(map, PieceColor.BLACK, location, Direction.all);
        int three = findAliveThree(map, attribute, Direction.all).getQuantity();

        //删除假想棋子
        if (isImaginary) {
            map.removeCell(location);
        }
        return three >= 2;
    }


    public boolean isForbiddenMove(PieceMap map, ContinueAttribute attribute, Direction direction) {
        boolean forbidden = false;
        PieceColor color = PieceColor.BLACK;

        if (findFive(attribute, direction).getQuantity() > 0) {
            forbidden = false;
        } else if (findAliveFour(map, attribute, direction).getQuantity()
                + findAsleepFour(map, attribute, direction).getQuantity() >= 2
                || findLongContinue(attribute, direction).getQuantity() > 0) {
            forbidden = true;
        } else {
            Direction aliveThree = findAliveThree(map, attribute, direction);
            if (aliveThree.getQuantity() >= 2) {
                Direction[] directionArray = Direction.createDirectionArray();
                int valid = 0;
                for (Direction d : directionArray) {
                    if (aliveThree.contains(d)) {
                        SingleContinue single = attribute.getContinue(d);
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
                                ContinueAttribute breakAttr = getContinueAttribute(map, color, breakPoint[j], d);
                                if (findAliveFour(map, breakAttr, d).getQuantity() > 0) {
                                    aliveFourPoint[j] = breakPoint[j];
                                }
                                //删除假想棋子
                                if (isImaginary1) {
                                    map.removeCell(breakPoint[j]);
                                }
                            }
                        }
                        for (Point p : aliveFourPoint) {
                            if (p != null) {
                                boolean isImaginary1 = false;
                                //插入假想棋子
                                if (map.available(p)) {
                                    map.addPiece(-1, p, color);
                                    isImaginary1 = true;
                                }

                                Direction dire = direction.remove(d);
                                ContinueAttribute aliveFour = getContinueAttribute(map, color, p, Direction.all);
                                if (findFive(aliveFour, dire).getQuantity() > 0) {
                                    //删除假想棋子
                                    if (isImaginary1) {
                                        map.removeCell(p);
                                    }
                                    continue;
                                }
                                if (isForbiddenMove(map, aliveFour, Direction.all)) {
                                    //删除假想棋子
                                    if (isImaginary1) {
                                        map.removeCell(p);
                                    }
                                    continue;
                                }
                                //删除假想棋子
                                if (isImaginary1) {
                                    map.removeCell(p);
                                }
                                valid++;
                                break;
                            }
                        }
                    }
                }
                if (valid >= 2) {
                    forbidden = true;
                }
            }
        }
        return forbidden;
    }


//    public boolean isForbiddenMove(PieceMap map, Point location, Direction direction) {
//        boolean forbidden = false;
//        boolean isImaginary = false;
//        PieceColor color = PieceColor.BLACK;
//        //插入假想棋子
//        if (map.available(location)) {
//            map.addPiece(-1, location, color);
//            isImaginary = true;
//        }
//
//        ContinueAttribute allDirection = getContinueAttribute(map, color, location, direction);
//        if (findFive(allDirection, direction).getQuantity() > 0) {
//            forbidden = false;
//        } else if (findAliveFour(map, allDirection, direction).getQuantity()
//                + findAsleepFour(map, allDirection, direction).getQuantity() >= 2
//                || findLongContinue(map, location, direction).getQuantity() > 0) {
//            forbidden = true;
//        } else {
//            Direction aliveThree = findAliveThree(map, allDirection, direction);
//            if (aliveThree.getQuantity() >= 2) {
//                ContinueAttribute aliveThreeAttr = getContinueAttribute(map, color, location, aliveThree);
//                Direction[] directionArray = Direction.createDirectionArray();
//                int valid = 0;
//                for (Direction d : directionArray) {
//                    if (aliveThree.contains(d)) {
//                        SingleContinue single = aliveThreeAttr.getContinue(d);
//                        Point[] breakPoint = single.getBreakPoint();
//                        Point[] aliveFourPoint = new Point[2];
//                        for (int j = 0; j < breakPoint.length; j++) {
//                            if (breakPoint[j] != null) {
//                                boolean isImaginary1 = false;
//                                //插入假想棋子
//                                if (map.available(breakPoint[j])) {
//                                    map.addPiece(-1, breakPoint[j], color);
//                                    isImaginary1 = true;
//                                }
//                                ContinueAttribute breakAttr = getContinueAttribute(map, color, breakPoint[j], d);
//                                if (findAliveFour(map, breakAttr, d).getQuantity() > 0) {
//                                    aliveFourPoint[j] = breakPoint[j];
//                                }
//                                //删除假想棋子
//                                if (isImaginary1) {
//                                    map.removeCell(breakPoint[j]);
//                                }
//                            }
//                        }
//                        for (Point p : aliveFourPoint) {
//                            if (p != null) {
//                                boolean isImaginary1 = false;
//                                //插入假想棋子
//                                if (map.available(p)) {
//                                    map.addPiece(-1, p, color);
//                                    isImaginary1 = true;
//                                }
//
//                                Direction dire = direction.remove(d);
//                                ContinueAttribute aliveFour = getContinueAttribute(map, color, p, dire);
//                                if (findFive(aliveFour, dire).getQuantity() > 0) {
//                                    //删除假想棋子
//                                    if (isImaginary1) {
//                                        map.removeCell(p);
//                                    }
//                                    continue;
//                                }
//                                if (isForbiddenMove(map, p, Direction.all)) {
//                                    //删除假想棋子
//                                    if (isImaginary1) {
//                                        map.removeCell(p);
//                                    }
//                                    continue;
//                                }
//                                //删除假想棋子
//                                if (isImaginary1) {
//                                    map.removeCell(p);
//                                }
//                                valid++;
//                                break;
//                            }
//                        }
//                    }
//                }
//                if (valid >= 2) {
//                    forbidden = true;
//                }
//            }
//        }
//        //删除假想棋子
//        if (isImaginary) {
//            map.removeCell(location);
//        }
//        return forbidden;
//    }


    public Map<Direction, ContinueType> getContinueTypes(PieceMap map, ContinueAttribute attribute) {
        Map<Direction, ContinueType> result = new HashMap<>();
        Direction[] directions = Direction.createDirectionArray();
        for (Direction direction : directions) {
            ContinueType type = getContinueType(map, attribute, direction);
            if (type != null) {
                result.put(direction, type);
            }
        }
        return result;
    }

    private ContinueType getContinueType(PieceMap map, ContinueAttribute attribute, Direction direction) {
        SingleContinue single = attribute.getContinue(direction);
        if (single == null) {
            return null;
        }
        if (usingForbiddenMove
                && attribute.getColor().equals(PieceColor.BLACK)
                && isForbiddenMove(map, attribute, Direction.all)) {
            return ContinueType.FORBIDDEN_MOVE;
        }
        if (single.getLength() >= 5) {
            return ContinueType.FIVE;
        }
        if (findAliveFour(map, attribute, direction).contains(direction)) {
            return ContinueType.ALIVE_FOUR;
        } else if (findAsleepFour(map, attribute, direction).contains(direction)) {
            return ContinueType.ASLEEP_FOUR;
        } else if (findAliveThree(map, attribute, direction).contains(direction)) {
            return ContinueType.ALIVE_THREE;
        } else if (findAsleepThree(map, attribute, direction).contains(direction)) {
            return ContinueType.ASLEEP_THREE;
        } else {
            return ContinueType.NONE;
        }
    }

    public Point getCloseMove(PieceMap map, PieceColor color, Piece lastPiece) {
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

    public Point getMove(PieceMap map, PieceColor color) {
        Map<Point, Integer> scores = getAllScore(map, color);
        Set<Map.Entry<Point, Integer>> set = scores.entrySet();

        Map.Entry<Point, Integer>[] entries = set.toArray(new Map.Entry[1]);
        Point[] highScore = getHighScorePoints(entries, 1);

        return highScore[0];
    }

    private Point[] getHighScorePoints(Map.Entry<Point, Integer>[] entries, int size) {
        if (size > entries.length) {
            size = entries.length;
        }

        Point[] highScore = new Point[size];
        for (int i = 0; i < size; ++i) {
            int max = i;
            for (int j = i; j < entries.length; ++j) {
                if (entries[j].getValue() > entries[i].getValue()) {
                    max = j;
                }
            }
            if (max != i) {
                Map.Entry<Point, Integer> t = entries[max];
                entries[max] = entries[i];
                entries[i] = t;
                highScore[i] = t.getKey();
            }
        }
        return highScore;
    }

    private Map<Point, Integer> getAllScore(PieceMap map, PieceColor color) {
        Map<Point, Integer> result = new HashMap<>();
        ContinueAttribute current;
        ContinueAttribute other;

        for (Point point : map) {
            if (map.available(point)) {
                current = getContinueAttribute(map, color, point, Direction.all);
                other = getContinueAttribute(map, color.foeColor(), point, Direction.all);
                result.put(point, getScore(map, current, other));
            }
        }
//        for (int i = 1; i <= 15; ++i) {
//            for (int j = 1; j<= 15; ++j) {
//                point = new Point(i, j);
//                if (map.available(point)) {
//                    current = getContinueAttribute(map, color, point, Direction.all);
//                    other = getContinueAttribute(map, color.foeColor(), point, Direction.all);
//                    result.put(point, getScore(map, current, other));
//                }
//            }
//        }
        return result;
    }

    private int getScore(PieceMap map, ContinueAttribute currentPlayer, ContinueAttribute otherPlayer) {
        return new Random().nextInt();
    }


    public List<Point> findAllCatchPoint(PieceMap map) {
        List<Point> result = new ArrayList<>();
        if (findPoints(map, PieceColor.BLACK, Arrays.asList(ContinueType.FIVE), null).size() > 0) {
            return result;
        }
        List<Point> five = findPoints(map, PieceColor.WHITE, Arrays.asList(ContinueType.FIVE), null);
        List<Point> four = findPoints(map, PieceColor.WHITE,
                Arrays.asList(ContinueType.ASLEEP_FOUR, ContinueType.ALIVE_FOUR),
                Arrays.asList(ContinueType.FIVE));
        for (Point p : four) {
            map.addPiece(-1, p, PieceColor.WHITE);

            List<Point> def = findPoints(map, PieceColor.WHITE, Arrays.asList(ContinueType.FIVE), null);
            def.removeAll(five);

            Point def0 = def.get(0);
            map.addPiece(-1, def0, PieceColor.BLACK);
            ContinueAttribute attribute = getContinueAttribute(map, PieceColor.BLACK, def0, Direction.all);
            map.removeCell(def0);

            if (isForbiddenMove(map, attribute, Direction.all)) {
                result.add(p);
            }

            map.removeCell(p);
        }
        return result;
    }

    public List<Point> findAllWinPoints(PieceMap map, PieceColor color) {
        List<Point> five = findPoints(map, color, Arrays.asList(ContinueType.FIVE), null);
        if (color.equals(PieceColor.WHITE)) {
            five.addAll(findAllCatchPoint(map));
        }
        return five;
    }

    public WinTree findVCF(PieceMap map, PieceColor color) {
        System.out.println("findVCF");
        WinMethod winMethod = null;
        MoveSetGetter moveSetGetter = null;
        WinTree result = null;

        winMethod = new WinTreeFinder.WinMethod() {
            @Override
            public boolean isWin(PieceMap map, Point point, PieceColor color) {
                Piece p = new RealPiece(-1, point, color);
                PieceColor winner = findWinner(map, p);
                return winner != null && winner.equals(color);
            }
        };

        final PieceColor c = color;
        moveSetGetter = new WinTreeFinder.MoveSetGetter() {
            @Override
            public List<Point> getMoveSet(PieceMap map, Point lastFoeMove, PieceColor color) {
                List<Point> result = new ArrayList<>();
                if (color.equals(c)) {
                    result = findPoints(map, color, Arrays.asList(ContinueType.FIVE, ContinueType.ALIVE_FOUR, ContinueType.ASLEEP_FOUR), null);
                } else if (color.equals(c.foeColor())) {
                    result = findPoints(map, color, Arrays.asList(ContinueType.FIVE), null);
                    ContinueAttribute attribute = getContinueAttribute(map, c, lastFoeMove, Direction.all);
                    Map<Direction, ContinueType> types = getContinueTypes(map, attribute);
                    for (Map.Entry<Direction, ContinueType> entry : types.entrySet()) {
                        if (entry.getValue().equals(ContinueType.ALIVE_FOUR)
                                || entry.getValue().equals(ContinueType.ASLEEP_FOUR)) {
                            Point[] points = attribute.getContinue(entry.getKey()).getBreakPoint();
                            for (Point p : points) {
                                if (p != null && c.equals(findWinner(map, new RealPiece(-1, p, c)))) {
                                    result.add(p);
                                }
                            }
                        }
                    }
//                    result.addAll(findPoints(map, color.foeColor(), Arrays.asList(ContinueType.FIVE), null));
                }
                return result;
            }
        };

        result = new WinTreeFinder(winMethod, moveSetGetter).getWinTree(map, null, color);

        return result;
    }

    public WinTree findVCT(PieceMap map, PieceColor color) {
        System.out.println("findVCT");
        WinMethod winMethod = null;
        MoveSetGetter moveSetGetter = null;
        WinTree result = null;

        winMethod = new WinMethod() {
            @Override
            public boolean isWin(PieceMap map, Point point, PieceColor color) {
                Piece p = new RealPiece(-1, point, color);
                PieceColor winner = findWinner(map, p);
                return (winner != null && winner.equals(color))
                        || !findVCF(map, color).isEmpty();
            }
        };

        final PieceColor c = color;
        moveSetGetter = new MoveSetGetter() {
            @Override
            public List<Point> getMoveSet(PieceMap map, Point lastFoeMove, PieceColor color) {
                System.out.println("getMoveSet");
                List<Point> result = new ArrayList<>();
                if (color.equals(c)) {
                    result = findPoints(map, color, Arrays.asList(
                            ContinueType.FIVE, ContinueType.ALIVE_FOUR, ContinueType.ASLEEP_FOUR,
                            ContinueType.ALIVE_THREE), null);
                } else if (color.equals(c.foeColor())) {
                    result = findPoints(map, color, Arrays.asList(ContinueType.FIVE, ContinueType.ALIVE_FOUR,
                                    ContinueType.ASLEEP_FOUR), null);
                    ContinueAttribute attribute = getContinueAttribute(map, c, lastFoeMove, Direction.all);
                    Map<Direction, ContinueType> types = getContinueTypes(map, attribute);
                    for (Map.Entry<Direction, ContinueType> entry : types.entrySet()) {

                        if (entry.getValue().equals(ContinueType.ALIVE_FOUR)
                                || entry.getValue().equals(ContinueType.ASLEEP_FOUR)
                                || entry.getValue().equals(ContinueType.ALIVE_THREE)
                                || entry.getValue().equals(ContinueType.ASLEEP_THREE)) {
                            Point[] points = attribute.getContinue(entry.getKey()).getBreakPoint();
                            for (Point p : points) {
                                if (p != null && map.available(p)) {
                                    result.add(p);
                                }
                            }
                        }


//                        if (entry.getValue().equals(ContinueType.ALIVE_FOUR)
//                                || entry.getValue().equals(ContinueType.ASLEEP_FOUR)) {
//                            result.addAll(findPoints(map, c, Arrays.asList(ContinueType.FIVE), null));
//                            Point[] points = attribute.getContinue(entry.getKey()).getBreakPoint();
//                            for (Point p : points) {
//                                if (p != null && c.equals(findWinner(map, new RealPiece(-1, p, c)))) {
//                                    result.add(p);
//                                }
//                            }
//                        } else if (entry.getValue().equals(ContinueType.ALIVE_THREE)
//                                || entry.getValue().equals(ContinueType.ASLEEP_THREE)) {
//                            result = findPoints(map, color, Arrays.asList(ContinueType.FIVE, ContinueType.ALIVE_FOUR,
//                                    ContinueType.ASLEEP_FOUR), null);
//                            Point[] points = attribute.getContinue(entry.getKey()).getBreakPoint();
//                            for (Point p : points) {
//                                if (p != null && findPoints(map, c, Arrays.asList(ContinueType.ALIVE_FOUR, ContinueType.ASLEEP_FOUR), null).contains(p)) {
//                                    result.add(p);
//                                }
//                            }
//                        }
                    }
//                    result.addAll(findPoints(map, color.foeColor(), Arrays.asList(ContinueType.FIVE), null));
                }
                return result;
            }
        };
        result = new WinTreeFinder(winMethod, moveSetGetter).getWinTree(map, null, color);
        return result;
    }

    public List<Point> findPoints(PieceMap map, PieceColor color, Collection<ContinueType> contains, Collection<ContinueType> excludes) {
        List<Point> result = new ArrayList<>();
        ContinueAttribute attribute;
        ContinueType type;
        Map<Direction, ContinueType> types;
        for (Point point : map) {
            if (map.available(point)) {

                map.addPiece(-1, point, color);

                attribute = getContinueAttribute(map, color, point, Direction.all);
                types = getContinueTypes(map, attribute);

                if (containsAType(types, contains) && excludesAllTypes(types, excludes)) {
                    result.add(point);
                }

                map.removeCell(point);
            }
        }
        return result;
    }

    private boolean containsAType(Map<Direction, ContinueType> map, Collection<ContinueType> types) {
        if (types == null) {
            return true;
        }
        for (ContinueType t : types) {
            if (map.containsValue(t)) {
                return true;
            }
        }
        return false;
    }

    private boolean excludesAllTypes(Map<Direction, ContinueType> map, Collection<ContinueType> types) {
        if (types == null) {
            return true;
        }
        for (ContinueType t : types) {
            if (map.containsValue(t)) {
                return false;
            }
        }
        return true;
    }
}